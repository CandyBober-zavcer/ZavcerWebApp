import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    application
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
    }
}

val http4kVersion: String by project
val http4kConnectVersion: String by project
val junitVersion: String by project
val kotlinVersion: String by project
val slf4jVersion: String by project

application {
    mainClass = "ru.yarsu.WebApplicationKt"
}

tasks.withType<JavaExec>() {
    standardInput = System.`in`
}

repositories {
    mavenCentral()
}

apply(plugin = "kotlin")

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            allWarningsAsErrors = false
            jvmTarget = "11"
            freeCompilerArgs += "-Xjvm-default=all"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    java {
        sourceCompatibility = VERSION_11
        targetCompatibility = VERSION_11
    }
}

dependencies {
    implementation("org.http4k:http4k-client-okhttp:${http4kVersion}")
    implementation("org.http4k:http4k-cloudnative:${http4kVersion}")
    implementation("org.http4k:http4k-core:${http4kVersion}")
    implementation("org.http4k:http4k-format-jackson:${http4kVersion}")
    implementation("org.http4k:http4k-multipart:${http4kVersion}")
    implementation("org.http4k:http4k-server-netty:${http4kVersion}")
    implementation("org.http4k:http4k-template-pebble:${http4kVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    testImplementation("org.http4k:http4k-testing-approval:${http4kVersion}")
    testImplementation("org.http4k:http4k-testing-hamkrest:${http4kVersion}")
    testImplementation("org.http4k:http4k-testing-kotest:${http4kVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.slf4j:slf4j-simple:${slf4jVersion}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
}
