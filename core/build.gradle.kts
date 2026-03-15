plugins {
    id("java")
    id("org.springframework.boot") version "3.2.5"
}
apply(plugin = "io.spring.dependency-management")

group = "com.twister"
version = "1.0-SNAPSHOT"

val lombokVersion = "1.18.32"
val junitVersion = "6.0.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
}

tasks.test {
    useJUnitPlatform()
}