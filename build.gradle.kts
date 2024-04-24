plugins {
    id("java")
    id("application")
    id("org.springframework.boot") version "3.2.5"
    id("org.openjfx.javafxplugin") version "0.1.0"
}
apply(plugin = "io.spring.dependency-management")

group = "com.twister"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val dialectsVersion = "6.5.0.CR2"
val sqliteVersion = "3.45.3.0"
val lombokVersion = "1.18.32"
val flywayVersion = "10.11.1"
val logbackVersion = "7.4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.hibernate.orm:hibernate-community-dialects:${dialectsVersion}")
    implementation("org.xerial:sqlite-jdbc:${sqliteVersion}")
    implementation("org.flywaydb:flyway-core:${flywayVersion}")

    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    implementation("net.logstash.logback:logstash-logback-encoder:${logbackVersion}")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.fxml", "javafx.base", "javafx.graphics")
    configurations = arrayOf("compileOnly", "implementation")
}

application {
    mainClass.set("com.twister.TwisterApplication")
}
