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
  implementation("org.jetbrains.kotlin:kotlin-serialization")
  implementation("com.github.ajalt.clikt:clikt:5.1.0")
  implementation("org.apache.maven:maven-model:3.9.16")
}

spotless {
  kotlin { ktfmt() }
  kotlinGradle { ktfmt() }
}

application {
  mainClass.set("mezlogo.mvnexplore.app.Main")
  applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}
