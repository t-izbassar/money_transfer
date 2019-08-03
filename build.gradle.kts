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
val ktorVersion = "1.2.2"
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("org.slf4j:slf4j-simple:1.7.26")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion") {
        exclude("ch.qos.logback")
    }
    testImplementation("io.mockk:mockk:1.9.3")
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
