package com.github.mrbean355.variantkontrol

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class GeneratorTest {
    @get:Rule
    val folder: TemporaryFolder = TemporaryFolder()

    @Test
    fun testGenerate_BuildTypesOnly() {
        generate(
            dimensions = listOf(
                Dimension("buildType", setOf("debug", "release")),
            ),
            directory = folder.root,
            packageName = "com.github.mrbean355.test",
            buildConfigPackage = "com.github.mrbean355.config",
        )

        validateOutput("build-types-only.txt")
    }

    @Test
    fun testGenerate_OneFlavorAndBuildTypes() {
        generate(
            dimensions = listOf(
                Dimension("tier", setOf("free", "paid")),
                Dimension("buildType", setOf("debug", "release")),
            ),
            directory = folder.root,
            packageName = "com.github.mrbean355.test",
            buildConfigPackage = "com.github.mrbean355.config",
        )

        validateOutput("one-flavor.txt")
    }

    @Test
    fun testGenerate_TwoFlavorsAndBuildTypes() {
        generate(
            dimensions = listOf(
                Dimension("quality", setOf("good", "bad")),
                Dimension("tier", setOf("free", "paid")),
                Dimension("buildType", setOf("debug", "release")),
            ),
            directory = folder.root,
            packageName = "com.github.mrbean355.test",
            buildConfigPackage = "com.github.mrbean355.config",
        )

        validateOutput("two-flavors.txt")
    }

    @Test
    fun testGenerate_ThreeFlavorsAndBuildTypes() {
        generate(
            dimensions = listOf(
                Dimension("colour", setOf("green", "blue")),
                Dimension("quality", setOf("good", "bad")),
                Dimension("tier", setOf("free", "paid")),
                Dimension("buildType", setOf("debug", "release")),
            ),
            directory = folder.root,
            packageName = "com.github.mrbean355.test",
            buildConfigPackage = "com.github.mrbean355.config",
        )

        validateOutput("three-flavors.txt")
    }

    private fun validateOutput(expectedFile: String) {
        val resource = Thread.currentThread().contextClassLoader.getResource(expectedFile)
            ?: error("Failed to load resource: $expectedFile")
        val expected = File(resource.toURI())
        val actual = File(folder.root, "VariantKontrol.kt")
        assertEquals(expected.readText(), actual.readText())
    }
}