package com.github.mrbean355.variantkontrol

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

@Suppress("unused") // referenced in the build script.
class VariantKontrolPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("variantKontrol", VariantKontrolExtension::class.java)

        target.afterEvaluate { project ->
            val appExtension = project.extensions.findByType(AppExtension::class.java)
                ?: error("Must be applied to an Android app module")

            val generateTask = project.tasks.register("generateVariantKontrolDsl", GenerateVariantKontrolTask::class.java) { task ->
                val libExtension = project.extensions.getByType(VariantKontrolExtension::class.java)
                val applicationId = appExtension.defaultConfig.applicationId ?: error("Null application ID")
                val buildTypes = appExtension.buildTypes.map { it.name }
                val dimensionsToFlavors = appExtension.getDimensionsToFlavorsMap()

                task.packageName.set(libExtension.packageName ?: applicationId)
                task.buildConfigPackage.set(applicationId)
                task.buildTypes.addAll(buildTypes)
                task.productFlavors.putAll(dimensionsToFlavors)
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

    private fun AppExtension.getDimensionsToFlavorsMap(): Map<String, Set<String>> {
        val result = mutableMapOf<String, MutableSet<String>>()

        productFlavors.asMap.forEach { (flavorName, flavor) ->
            val dimensionName = flavor.dimension ?: error("Flavor '$flavorName' has null dimension")
            result.getOrPut(dimensionName) { mutableSetOf() }
                .add(flavorName)
        }

        return result
    }

    private fun Project.generatedPath(): File {
        return file("${project.buildDir}/generated/source/android-variant-kontrol")
    }
}
