import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
    implementation(group = "org.http4k", name = "http4k-core", version = "4.40.+")
    implementation(group = "org.http4k", name = "http4k-server-jetty", version = "4.40.+")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.5.+")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version = "0.4.+")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.0-alpha5")
    runtimeOnly(group = "org.slf4j", name = "slf4j-simple", version = "2.0.0-alpha5")
    testImplementation(kotlin("test"))
}

// The following configuration is necessary to compile the project with target Java version 17
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict" // Helps to catch nullability issues, preventing NPEs
        jvmTarget = "17"
    }
}

tasks.register<Copy>("copyRuntimeDependencies") {
    into("build/libs")
    from(configurations.runtimeClasspath)
}

// The following configuration is necessary to generate a JAR file with the necessary dependencies
tasks.named<Jar>("jar") {
    dependsOn("copyRuntimeDependencies")
    manifest {
        attributes["Main-Class"] = "pt.isel.ls.sessions.SessionsAppKt"
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
    }
}
