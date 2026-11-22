package com.twister.desktop.app;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Scope;

/**
 * Rules inside modules; Gradle covers the boundaries between modules (see {@code .claude/rules/architecture.md}).
 *
 * <p>The rules read bytecode, so unused imports and annotations with SOURCE retention are invisible. Most Lombok
 * annotations are SOURCE, so the Lombok rule only sees what Lombok leaves in the class files: {@code
 * lombok.Generated} on generated members (see {@code lombok.config}) and runtime Lombok types.
 *
 * <p>Rules that fail today are frozen in {@code archunit_store} (see {@link FrozenViolationStore}): recorded violations
 * pass, new ones fail.
 */
class ArchitectureTest {

    // tw-core has no common prefix: its packages are domain and payload.
    private static final String CORE_DOMAIN = "com.twister.domain..";
    private static final String CORE_PAYLOAD = "com.twister.payload..";
    private static final String WORKFLOW = "com.twister.workflow..";
    private static final String PERSISTENCE = "com.twister.persistence..";
    private static final String FX_STARTER = "com.twister.fx.javafx..";
    private static final String DESKTOP = "com.twister.desktop..";

    private static JavaClasses importedClasses;

    @BeforeAll
    static void importClasses() {
        importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("com.twister");
    }

    // Without this, a module missing from the test classpath would make its rules pass vacuously.
    @Test
    void everyModuleIsImported() {
        Stream.of(CORE_DOMAIN, CORE_PAYLOAD, WORKFLOW, PERSISTENCE, FX_STARTER, DESKTOP)
                .forEach(pkg -> assertThat(importedClasses.stream()
                                .filter(JavaClass.Predicates.resideInAPackage(pkg))
                                .count())
                        .as("classes in %s", pkg)
                        .isPositive());
    }

    @Test
    void coreHasNoFrameworkDependencies() {
        noClasses()
                .that()
                .resideInAnyPackage(CORE_DOMAIN, CORE_PAYLOAD)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "javafx..")
                .check(importedClasses);
    }

    // Frozen: tw-core uses Lombok today. See the class comment for what this can detect.
    @Test
    void coreDoesNotUseLombok() {
        FreezingArchRule.freeze(noClasses()
                        .that()
                        .resideInAnyPackage(CORE_DOMAIN, CORE_PAYLOAD)
                        .should()
                        .dependOnClassesThat()
                        .resideInAPackage("lombok.."))
                .check(importedClasses);
    }

    // Frozen: the DAO ports use Page and Pageable today (ADR 0004). Every workflow interface counts as a port,
    // because packages are by feature and a port can sit next to its use cases.
    @Test
    void portsExposeNoSpringDataTypes() {
        FreezingArchRule.freeze(classes()
                        .that(arePorts())
                        .should(exposeNoTypesFrom("org.springframework.data.."))
                        .as("ports should not expose Spring Data types"))
                .check(importedClasses);
    }

    @Test
    void desktopDoesNotDependOnPersistenceOrSpringData() {
        noClasses()
                .that()
                .resideInAPackage(DESKTOP)
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(PERSISTENCE, "org.springframework.data..")
                .check(importedClasses);
    }

    // Frozen: the workflow and JavaFX starter auto-configurations scan today.
    @Test
    void autoConfigurationsDoNotScanComponents() {
        FreezingArchRule.freeze(classes()
                        .that()
                        .areMetaAnnotatedWith(AutoConfiguration.class)
                        .should()
                        .notBeMetaAnnotatedWith(ComponentScan.class)
                        .andShould()
                        .notBeMetaAnnotatedWith(ComponentScans.class))
                .check(importedClasses);
    }

    // No view models exist yet, so this is the only rule allowed to match nothing.
    @Test
    void viewModelsAreScoped() {
        classes()
                .that()
                .haveSimpleNameEndingWith("ViewModel")
                .should()
                .beMetaAnnotatedWith(Scope.class)
                .allowEmptyShould(true)
                .check(importedClasses);
    }

    private static DescribedPredicate<JavaClass> arePorts() {
        return DescribedPredicate.describe(
                "ports (interfaces in com.twister.workflow..)",
                clazz -> clazz.isInterface() && clazz.getPackageName().startsWith("com.twister.workflow"));
    }

    /** Checks parameters, return types, thrown types and supertypes, including their generic arguments. */
    private static ArchCondition<JavaClass> exposeNoTypesFrom(String packageIdentifier) {
        var forbidden = JavaClass.Predicates.resideInAPackage(packageIdentifier);
        return new ArchCondition<>("not expose types from " + packageIdentifier) {
            @Override
            public void check(JavaClass port, ConditionEvents events) {
                for (JavaType supertype : supertypesOf(port)) {
                    report(port, port.getName(), "extends", supertype, events);
                }
                for (JavaMethod method : port.getMethods()) {
                    report(method, method.getFullName(), "returns", method.getReturnType(), events);
                    for (JavaType parameter : method.getParameterTypes()) {
                        report(method, method.getFullName(), "takes", parameter, events);
                    }
                    method.getThrowsClause()
                            .forEach(
                                    thrown -> report(method, method.getFullName(), "throws", thrown.getType(), events));
                }
            }

            private List<JavaType> supertypesOf(JavaClass port) {
                List<JavaType> types = new ArrayList<>(port.getInterfaces());
                port.getSuperclass().ifPresent(types::add);
                return types;
            }

            private void report(Object location, String owner, String verb, JavaType type, ConditionEvents events) {
                type.getAllInvolvedRawTypes().stream()
                        .filter(forbidden)
                        .forEach(raw -> events.add(SimpleConditionEvent.violated(
                                location, owner + " " + verb + " " + raw.getName() + " in " + type.getName())));
            }
        };
    }
}
