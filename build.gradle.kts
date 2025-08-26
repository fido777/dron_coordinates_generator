// build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.josedrivera"
version = "0.0.1-SNAPSHOT"
description = "dron_coordinates_generator"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // Web para exponer endpoints REST
    implementation("org.springframework.boot:spring-boot-starter-web")
    // WebSocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    // JSON (para serializar las coordenadas, puedes usar Jackson)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // Para generar UUIDs o herramientas utilitarias
    implementation("org.apache.commons:commons-lang3")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Task to run the WebSocket test client
tasks.register<JavaExec>("runWebSocketClient") {
    group = "application"
    description = "Run the WebSocket test client to connect and listen to coordinate broadcasts"
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.josedrivera.client.WebSocketTestClientKt")
    
    doFirst {
        println("🚁 Starting WebSocket Test Client...")
        println("Make sure the main application is running on localhost:8080")
    }
}
