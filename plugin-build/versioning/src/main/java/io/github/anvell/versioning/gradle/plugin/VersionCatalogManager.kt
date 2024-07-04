package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.models.CalendarVersion
import org.gradle.api.GradleException
import org.tomlj.Toml
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val GroupLabel = "versions"
private const val VersionLabel = "name"
private const val CodeLabel = "code"

internal object VersionCatalogManager {
    fun deserialize(input: String): Pair<CalendarVersion, Long> =
        with(Toml.parse(input)) {
            val now = LocalDateTime.now(ZoneOffset.UTC)
            val versionName =
                getString(listOf(GroupLabel, VersionLabel))
                    ?: throw GradleException("Version name is not specified")
            val versionCode =
                getString(listOf(GroupLabel, CodeLabel))
                    ?.toLongOrNull()
                    ?: throw GradleException("Version code is not specified correctly")
            val version =
                CalendarVersion
                    .parse(now, versionName)
                    .getOrThrow()

            version to versionCode
        }

    fun serialize(
        version: CalendarVersion,
        code: Long,
        useShorterFormat: Boolean,
    ) = buildString {
        appendLine("[$GroupLabel]")
        appendLine("$VersionLabel = \"${version.formatVersion(useShorterFormat)}\"")
        appendLine("$CodeLabel = \"$code\"")
    }
}
