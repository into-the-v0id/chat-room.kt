plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "org.chatRoom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("commons-validator:commons-validator:1.7")
    implementation("com.github.f4b6a3:uuid-creator:5.2.0")
    implementation("org.springframework.security:spring-security-crypto:6.0.1")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
