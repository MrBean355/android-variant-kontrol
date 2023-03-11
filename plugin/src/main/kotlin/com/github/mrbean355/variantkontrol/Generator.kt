package com.github.mrbean355.variantkontrol

import java.io.File

const val BuildTypeDimension = "buildType"

class Dimension(
    val name: String,
    val flavors: Set<String>
)

fun generate(
    dimensions: List<Dimension>,
    directory: File,
    packageName: String,
    buildConfigPackage: String,
) {
    val content = buildString {
        appendLine("package $packageName\n")
        mainClasses(dimensions)
        entryPointFunction()
        containerApi(dimensions)
        constants(dimensions, buildConfigPackage)
        dslAnnotation()
        containerClass(dimensions)
    }

    File(directory, "VariantKontrol.kt").writeText(content)
}

private fun StringBuilder.mainClasses(dimensions: List<Dimension>) {
    dimensions.forEachIndexed { index, dimension ->
        appendLine("@VariantKontrolDsl")
        appendLine("class %sFlavorConfig {".format(dimension.name.capitalised()))
        if (dimension.name == BuildTypeDimension) {
            dimension.flavors.forEach { flavor ->
                appendLine("    var %s: Boolean = false".format(flavor))

            }
        } else {
            val next = dimensions[index + 1]
            dimension.flavors.forEach { flavor ->
                appendLine("    val %1\$s: %2\$sFlavorConfig = %2\$sFlavorConfig()".format(flavor, next.name.capitalised()))
            }
        }
        if (index > 0) {
            appendLine()
            appendLine("    operator fun invoke(default: Boolean? = null, config: %sFlavorConfig.() -> Unit = {}) {".format(dimension.name.capitalised()))
            appendLine("        if (default != null) {")
            if (dimension.name == BuildTypeDimension) {
                dimension.flavors.forEach { flavor ->
                    appendLine("            %s = default".format(flavor))
                }
            } else {
                dimension.flavors.forEach { flavor ->
                    appendLine("            %s(default = default)".format(flavor))
                }
            }
            appendLine("        }")
            appendLine("        config(this)")
            appendLine("    }")
        }
        appendLine("}")
        appendLine()
    }
}

private fun StringBuilder.entryPointFunction() {
    appendLine("fun <T> configureToggles(configure: FeatureTogglesScope<T>.() -> Unit): Set<T> {")
    appendLine("    return FeatureTogglesScopeImpl<T>().apply(configure).enabledToggles.toSet()")
    appendLine("}")
    appendLine()
}

private fun StringBuilder.constants(dimensions: List<Dimension>, buildConfigPackage: String) {
    dimensions.forEach {
        appendLine(
            "private const val active%s = %s".format(
                it.name.capitalised(),
                "$buildConfigPackage.BuildConfig.${buildConfigField(it.name)}"
            )
        )
    }
    appendLine()
}

private fun StringBuilder.dslAnnotation() {
    appendLine("@DslMarker")
    appendLine("private annotation class VariantKontrolDsl")
    appendLine()
}

private fun StringBuilder.containerApi(dimensions: List<Dimension>) {
    appendLine("interface FeatureTogglesScope<T> {")
    appendLine("    fun T.configure(config: %sFlavorConfig.() -> Unit)".format(dimensions.first().name.capitalised()))
    appendLine("}")
    appendLine()
}

private fun StringBuilder.containerClass(dimensions: List<Dimension>) {
    var prev = dimensions.first()

    appendLine("private class FeatureTogglesScopeImpl<T> : FeatureTogglesScope<T> {")
    appendLine("    val configuredToggles = mutableSetOf<T>()")
    appendLine("    val enabledToggles = mutableSetOf<T>()")
    appendLine()

    appendLine("    override fun T.configure(config: %sFlavorConfig.() -> Unit) {".format(prev.name.capitalised()))
    appendLine("        check(configuredToggles.add(this)) {")
    appendLine("            \"Toggle '\$this' was already configured.\"")
    appendLine("        }")
    appendLine("        val %s = %sFlavorConfig().apply(config)".format(prev.name, prev.name.capitalised()))

    dimensions.drop(1).forEach { dimension ->
        appendLine("        val %s = when (active%s) {".format(dimension.name, prev.name.capitalised()))

        prev.flavors.forEach { flavor ->
            appendLine("            \"%1\$s\" -> %2\$s.%1\$s".format(flavor, prev.name))
        }
        appendLine("            else -> error(\"Unexpected %s: \$active%s\")".format(prev.name, prev.name.capitalised()))

        appendLine("        }")
        prev = dimension
    }

    appendLine("        val enabled = when (active%s) {".format(BuildTypeDimension.capitalised()))
    dimensions.last().flavors.forEach { flavor ->
        appendLine("            \"%1\$s\" -> %2\$s.%1\$s".format(flavor, BuildTypeDimension))
    }
    appendLine("            else -> error(\"Unexpected build type: \$active%s\")".format(BuildTypeDimension.capitalised()))
    appendLine("        }")
    appendLine("        if (enabled) enabledToggles += this")

    appendLine("    }")
    appendLine("}")
}

private fun buildConfigField(dimension: String): String {
    return if (dimension == BuildTypeDimension) "BUILD_TYPE" else "FLAVOR_$dimension"
}

private fun String.capitalised(): String {
    return replaceFirstChar(Char::uppercase)
}