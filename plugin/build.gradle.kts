plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm")
}

group = "com.github.mrbean355"
val artifactId by extra("feature-toggles")
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation("com.android.tools.build:gradle:7.4.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
}

gradlePlugin {
    plugins {
        create("featureTogglesPlugin") {
            id = "com.github.mrbean355.featuretoggles"
            implementationClass = "com.github.mrbean355.featuretoggles.FeatureTogglesPlugin"
        }
    }
}
