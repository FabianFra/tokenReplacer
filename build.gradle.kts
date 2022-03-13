group = "com.fabianfrank.tokrep"
version = "0.0.1"

plugins {
    kotlin("jvm") version ("1.5.31")
    id("java-gradle-plugin")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

gradlePlugin {
    plugins {
        create("tokrep") {
            id = "com.fabianfrank.tokrep"
            implementationClass = "com.fabianfrank.tokrep.TokRepPlugin"
        }
    }
}

(tasks.findByName("test") as Test).useJUnitPlatform()