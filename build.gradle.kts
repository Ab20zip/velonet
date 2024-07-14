plugins {
    kotlin("jvm") version "2.0.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("me.tongfei:progressbar:0.10.1")
    implementation("com.github.ajalt.mordant:mordant:2.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
}

application {
    mainClass.set("com.altiran.velonet.MainKt")
}
