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
    testImplementation("junit:junit:4.13.2")
}

gradlePlugin {
    plugins {
        create("variantKontrolPlugin") {
            id = "com.github.mrbean355.variantkontrol"
            implementationClass = "com.github.mrbean355.variantkontrol.VariantKontrolPlugin"
        }
    }
}
