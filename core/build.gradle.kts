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
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("commons-validator:commons-validator:1.7")
    implementation("com.github.f4b6a3:uuid-creator:5.2.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
