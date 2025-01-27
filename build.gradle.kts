plugins {
    kotlin("jvm") version "2.1.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("me.tongfei:progressbar:0.10.1")
    implementation("com.github.ajalt.mordant:mordant:2.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}

application {
    mainClass.set("com.altiran.velonet.MainKt")
}
