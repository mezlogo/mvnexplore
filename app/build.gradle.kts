plugins {
  kotlin("jvm") version "2.4.20"
  kotlin("plugin.serialization") version "2.4.20"
  id("com.diffplug.spotless") version "8.10.2"
  application
}

repositories {
  mavenCentral()
}

dependencies {
  // CLI
  implementation("com.github.ajalt.clikt:clikt:5.1.0")

  // JSON
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

  // LOGGING
  implementation("org.slf4j:slf4j-api:2.0.20")
  implementation("ch.qos.logback:logback-classic:1.6.4")

  // Domain: maven model
  implementation("org.apache.maven:maven-model:3.9.16")
}

spotless {
  kotlin { ktfmt() }
  kotlinGradle { ktfmt() }
}

application {
  mainClass.set("mezlogo.mvnexplore.application.Main")
  applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}
