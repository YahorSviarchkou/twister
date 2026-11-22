package com.twister.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.persistence.TestApplication;
import com.twister.persistence.entity.reference.CountryEntity;
import com.twister.persistence.repository.reference.CountryRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.data.domain.AuditorAware;
import org.springframework.util.ClassUtils;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Uses the module as an application does. Cannot extend {@code BaseIntegrationTest}: its {@code @DataJpaTest} slice
 * does not read the auto-configuration imports file.
 */
@SpringBootTest(
        classes = PersistenceAutoConfigurationTest.ConsumerApplication.class,
        properties = "spring.jpa.hibernate.ddl-auto=create")
class PersistenceAutoConfigurationTest {

    private static final String PERSISTENCE_PACKAGE = "com.twister.persistence";

    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    // Not stopped per class: cached contexts outlive it.
    static {
        postgres.start();
    }

    @Test
    void savesAndReadsThroughRepository(@Autowired CountryRepository repository) {
        assertSavedBy(repository, "SYSTEM");
    }

    @Test
    void registersEveryGeneratedMapper(@Autowired ApplicationContext context) {
        // MapStruct's @Mapper is not visible at runtime, so find top-level *Impl classes instead.
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile("[^$]+Impl")));
        var implementations = scanner.findCandidateComponents(PERSISTENCE_PACKAGE);

        assertThat(implementations).isNotEmpty().allSatisfy(implementation -> {
            var type = ClassUtils.resolveClassName(implementation.getBeanClassName(), context.getClassLoader());
            assertThat(context.getBeanNamesForType(type))
                    .as("beans of %s", type.getName())
                    .hasSize(1);
        });
    }

    @Test
    void doesNotPickUpTestApplication(@Autowired ApplicationContext context) {
        assertThat(context.getBeanNamesForType(TestApplication.class)).isEmpty();
    }

    @Nested
    @Import(ApplicationAuditor.class)
    class WithApplicationAuditor {

        @Test
        void prefersApplicationAuditor(@Autowired CountryRepository repository) {
            assertSavedBy(repository, "APPLICATION");
        }
    }

    private static void assertSavedBy(CountryRepository repository, String auditor) {
        var name = UUID.randomUUID().toString();
        var id = repository.save(CountryEntity.builder().name(name).build()).getId();

        // No test transaction, so this reads from the database and not from the persistence context.
        assertThat(repository.findById(id)).hasValueSatisfying(country -> {
            assertThat(country.getName()).isEqualTo(name);
            assertThat(country.getCreatedBy()).isEqualTo(auditor);
            assertThat(country.getCreatedAt()).isNotNull();
        });
    }

    // Not a @SpringBootConfiguration, so that no other test finds it when looking up its configuration.
    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class ConsumerApplication {}

    @Configuration(proxyBeanMethods = false)
    static class ApplicationAuditor {

        @Bean
        AuditorAware<String> applicationAuditorAware() {
            return () -> Optional.of("APPLICATION");
        }
    }
}
