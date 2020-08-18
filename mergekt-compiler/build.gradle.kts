plugins {
    kotlin("jvm") version "1.3.72"
    kotlin("kapt") version "1.3.72"
    id("com.github.dcendents.android-maven")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.auto:auto-common:0.10")
    implementation("com.google.auto.service:auto-service:1.0-rc5")
    kapt("com.google.auto.service:auto-service:1.0-rc5")

    implementation("net.ltgt.gradle.incap:incap:0.2")
    kapt("net.ltgt.gradle.incap:incap-processor:0.2")

    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("com.squareup:kotlinpoet:1.5.0")

    implementation(project(":mergekt"))

    testImplementation("com.google.truth:truth:1.0")
    testImplementation("com.google.testing.compile:compile-testing:0.17")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.2.5")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
}
