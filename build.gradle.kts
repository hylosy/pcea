plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
}

kotlin {
    jvmToolchain(11)
}

group = "hylosy.pcea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version: String by project
val logback_version: String by project

dependencies {
    implementation(kotlin("stdlib"))
    // Ktor Server
    implementation("io.ktor:ktor-server-core:${ktor_version}")
    implementation("io.ktor:ktor-server-netty:${ktor_version}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktor_version}")
    implementation("io.ktor:ktor-server-cors:${ktor_version}")
    implementation("io.ktor:ktor-server-di:${ktor_version}")

    implementation("io.ktor:ktor-client-core:${ktor_version}")
    implementation("io.ktor:ktor-client-cio:${ktor_version}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    // logging
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("ch.qos.logback:logback-classic:${logback_version}")
    // NOTE: If you are use newer version than v9, you need update java to 17.
    implementation("org.flywaydb:flyway-core:8.5.13")
    implementation("org.flywaydb:flyway-mysql:8.5.13")
    // NOTE: mysql-connector-java is old package.
    implementation("com.mysql:mysql-connector-j:9.3.0")

    implementation("org.jetbrains.exposed:exposed-core:0.45.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.45.0")

    implementation("org.jsoup:jsoup:1.17.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
