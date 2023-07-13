plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("jvm") version "1.9.0"
}

group = "pub.gdt"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.mamoe", "mirai-core", "2.15.0")
    implementation("org.asynchttpclient", "async-http-client", "2.12.3")
    implementation("org.apache.logging.log4j", "log4j-core", "2.20.0")
    implementation("org.slf4j", "slf4j-simple", "2.0.7")
    implementation("com.google.code.gson", "gson", "2.10.1")
    implementation("it.unimi.dsi", "fastutil", "8.5.12")
    implementation(files("..\\_local-libs\\fix-protocol-version-1.9.4.mirai2.jar"))
}

application {
    mainClass.set("pub.gdt.keke.RobotMain")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "utf-8"
}