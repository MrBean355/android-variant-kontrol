package com.github.mrbean355.featuretoggles

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class FeatureTogglesTask : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val buildConfigPackage: Property<String>

    @get:Input
    abstract val buildTypes: ListProperty<String>

    @get:Input
    abstract val productFlavors: MapProperty<String, String>

    @get:OutputDirectory
    abstract val output: Property<File>

    @TaskAction
    fun run() {
        val mapped = mutableMapOf<String, MutableSet<String>>()
        productFlavors.get().forEach { (flavor, dimension) ->
            mapped.getOrPut(dimension) { mutableSetOf() }
                .add(flavor)
        }

        val dimensions = mapped.map { (name, flavors) ->
            Dimension(name, flavors)
        } + Dimension(
            BuildTypeDimension,
            buildTypes.get().toSet()
        )

        val dir = File(output.get(), packageName.get().replace('.', '/'))
            .also(File::mkdirs)

        generate(dimensions, dir, packageName.get(), buildConfigPackage.get())
    }
}