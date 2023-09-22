package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.models.CalendarVersion
import org.tomlj.Toml

private const val GroupLabel = "versions"
private const val VersionLabel = "versionName"
private const val CodeLabel = "versionCode"

internal object VersionCatalogManager {
    fun deserialize(
        input: String
    ): Pair<CalendarVersion, Long> = with(Toml.parse(input)) {
        val versionName = getString(listOf(GroupLabel, VersionLabel))
            ?: error("Version name is not specified")
        val versionCode = getLong(listOf(GroupLabel, CodeLabel))
            ?: error("Version code is not specified")
        val version = CalendarVersion.parse(versionName).getOrThrow()

        version to versionCode
    }

    fun serialize(
        version: CalendarVersion,
        code: Long
    ) = buildString {
        appendLine("[$GroupLabel]")
        appendLine("$VersionLabel = \"$version\"")
        appendLine("$CodeLabel = $code")
    }
}
