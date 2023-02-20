package com.github.mrbean355.variantkontrol

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateVariantKontrolTask : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val buildConfigPackage: Property<String>

    @get:Input
    abstract val productFlavors: MapProperty<String, Set<String>>

    @get:Input
    abstract val buildTypes: ListProperty<String>

    @get:OutputDirectory
    abstract val output: Property<File>

    @TaskAction
    fun run() {
        val dimensions = productFlavors.get()
            .map { (name, flavors) -> Dimension(name, flavors) }
            .plus(Dimension(BuildTypeDimension, buildTypes.get().toSet()))

        val directory = File(output.get(), packageName.get().replace('.', '/'))
            .also(File::mkdirs)

        generate(dimensions, directory, packageName.get(), buildConfigPackage.get())
    }
}