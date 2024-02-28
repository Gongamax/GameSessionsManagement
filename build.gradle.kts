import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
    testImplementation(kotlin("test"))
}

// The following configuration is necessary to compile the project with target Java version 17
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"   // Helps to catch nullability issues, preventing NPEs
        jvmTarget = "17"
    }
}

tasks.register<Copy>("copyRuntimeDependencies") {
    into("build/libs")
    from(configurations.runtimeClasspath)
}