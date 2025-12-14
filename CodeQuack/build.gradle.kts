plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IU", "2024.2.3")

        bundledPlugin("com.intellij.java")
        instrumentationTools()
    }

    implementation("org.json:json:20240303")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    patchPluginXml {
        sinceBuild.set("242")
        untilBuild.set("243.*")
    }
}