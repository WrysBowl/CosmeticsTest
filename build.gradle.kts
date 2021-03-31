import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

plugins {
    java
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group "net.siegemc.test"
version "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


repositories {
    mavenCentral()
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
    maven { url = uri("https://nexus.mcdevs.us/repository/mcdevs/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.dmulloy2.net/nexus/repository/public/") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
}

dependencies {
    implementation("us.mcdevs.library.kotlin:Kotlin:1.4.0")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.6.0")
    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")
    implementation("de.tr7zw:item-nbt-api-plugin:2.7.1")
    //compile(fileTree(include(["*.jar"]), dir("libs")))
}

tasks {
    shadowJar {
        relocate("co.aikar.commands", "net.siegemc.test.cosmetics.acf")
        relocate("co.aikar.locales", "net.siegemc.test.cosmetics.locales")
        doFirst {
            exclude("fonts/*.csv")
        }
        dependencies {
            exclude(dependency("com.google.code.gson:.*"))
            exclude(dependency("org.checkerframework:.*"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.4.0"))
            include(dependency("co.aikar:acf-paper:0.5.0-SNAPSHOT"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
//    withType<KotlinCompile> {
//        kotlinOptions.jvmTarget = "1.8"
//    }
}



//val compileKotlin: KotlinCompile by tasks
//
//compileKotlin.kotlinOptions.jvmTarget = "1.8"
