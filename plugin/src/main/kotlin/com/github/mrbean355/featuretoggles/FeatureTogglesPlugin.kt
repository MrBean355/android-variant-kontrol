package com.github.mrbean355.featuretoggles

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

class FeatureTogglesPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("variantKontrol", FeatureTogglesExtension::class.java)

        target.afterEvaluate { project ->
            val appExtension = project.extensions.findByType(AppExtension::class.java)
                ?: error("Must be applied to an Android app module")

            val generateTask = project.tasks.register("generateFeatureTogglesDsl", FeatureTogglesTask::class.java) { task ->
                val libExtension = project.extensions.getByType(FeatureTogglesExtension::class.java)
                val buildTypes = appExtension.buildTypes.map { it.name }
                val productFlavors = appExtension.productFlavors.asMap.mapValues { it.value.dimension }
                val applicationId = appExtension.defaultConfig.applicationId ?: error("Null application ID")

                task.packageName.set(libExtension.packageName ?: applicationId)
                task.buildConfigPackage.set(applicationId)
                task.buildTypes.addAll(buildTypes)
                task.productFlavors.putAll(productFlavors)
                task.output.set(project.generatedPath())
            }

            appExtension.applicationVariants.forEach {
                it.registerJavaGeneratingTask(generateTask, project.generatedPath())
            }

            project.tasks.withType(KotlinCompile::class.java) {
                it.dependsOn(generateTask)
                it.source(project.generatedPath())
            }
        }
    }

    private fun Project.generatedPath(): File {
        return file("${project.buildDir}/generated/source/android-variant-kontrol")
    }
}
