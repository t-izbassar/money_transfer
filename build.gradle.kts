import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    application
    id("com.diffplug.gradle.spotless") version "3.23.1"
}

repositories {
    jcenter()
}

val junitVersion = "5.5.0"
dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation(kotlin("test"))
}

application {
    mainClassName = "com.github.tizbassar.AppKt"
}

tasks.test {
    useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    allWarningsAsErrors = true
}

spotless {
    kotlin {
        ktlint().userData(mapOf(
            "indent_size" to "4",
            "continuation_indent_size" to "4"
        ))
    }
    kotlinGradle {
        ktlint().userData(mapOf(
            "indent_size" to "4",
            "continuation_indent_size" to "4"
        ))
    }
}
