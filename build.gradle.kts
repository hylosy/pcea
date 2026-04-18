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

val ktorVersion: String by project
val logbackVersion: String by project

dependencies {
    implementation(kotlin("stdlib"))
    // Ktor Server
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion") // TODO: version up
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-di:$ktorVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    // logging
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("ch.qos.logback:logback-classic:$logbackVersion") // TODO: version up
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

val sqlLogLevel = System.getProperty("SQL_LOG_LEVEL", "warn")

tasks.register<JavaExec>("runEventResult") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("hylosy.pcea.script.RunEventResultScriptKt")
    systemProperty("SQL_LOG_LEVEL", sqlLogLevel)
    args = listOfNotNull(
        System.getProperty("from")?.let { listOf("--from", it) },
        System.getProperty("to")?.let { listOf("--to", it) },
    ).flatten()
}

tasks.register<JavaExec>("runCardInDeck") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("hylosy.pcea.script.RunCardInDeckScriptKt")
    systemProperty("SQL_LOG_LEVEL", sqlLogLevel)
    args = listOfNotNull(
        System.getProperty("from"),
        System.getProperty("to"),
    )
}

tasks.register<JavaExec>("runEvent") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("hylosy.pcea.script.EventFetcherKt")
    systemProperty("SQL_LOG_LEVEL", sqlLogLevel)
}

tasks.register<JavaExec>("fetchDeckImages") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("hylosy.pcea.script.DeckImageFetcherKt")
    systemProperty("SQL_LOG_LEVEL", sqlLogLevel)
    args = listOfNotNull(
        System.getProperty("from")?.let { listOf("--from", it) },
        System.getProperty("to")?.let { listOf("--to", it) },
    ).flatten()
}

tasks.register<JavaExec>("runPhysicalCard") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("hylosy.pcea.script.RunPhysicalCardScriptKt")
    systemProperty("SQL_LOG_LEVEL", sqlLogLevel)
    systemProperty("expansion", System.getProperty("expansion") ?: "")
    systemProperty("expansionId", System.getProperty("expansionId") ?: "")
}

tasks.register<JavaExec>("migrateDatabase") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("hylosy.pcea.script.MigrateDatabaseKt")
    systemProperty("SQL_LOG_LEVEL", sqlLogLevel)
}
