package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.actions.GitVcsActions
import io.github.anvell.versioning.gradle.plugin.models.CalendarVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val TaskLabel = "publishVersionTag"
private const val TaskGroupLabel = "versioning"
private const val ExtensionLabel = "configureVersioning"

class GradleVersioningPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project
            .extensions
            .create(ExtensionLabel, GradleVersioningExtension::class.java)
            .apply {
                actions.convention(GitVcsActions())
                remote.convention("origin")
                autoPush.convention(false)
                variants.convention(setOf(""))
                branches.convention(emptySet())
            }

        with(project) {
            afterEvaluate {
                for (variant in extension.variants.get()) {
                    tasks.register(TaskLabel + variant.capitalized()) { task ->
                        task.group = TaskGroupLabel

                        task.doLast { publishVersionTag(extension, variant) }
                    }
                }
            }
        }
    }

    private fun publishVersionTag(
        extension: GradleVersioningExtension,
        variant: String? = null
    ) {
        val actions = extension.actions.get()
        val branch = actions.getBranchName()
        val branches = extension.branches.get()

        if (branches.isNotEmpty() &&
            branches.none { it.matches(branch) }
        ) {
            println("Skipping auto versioning for current branch")
            return
        }

        val latestTag = actions.getLatestTag()
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val headTags = actions.getHeadTags()
        val version = if (headTags.isNotEmpty()) {
            val headVersions = buildMap {
                for (tag in headTags) {
                    CalendarVersion.parse(tag).onSuccess {
                        put(tag.substringAfter('-', ""), it)
                    }
                }
            }

            if (variant.orEmpty() in headVersions) {
                println("Version tag is already applied on current commit")
                return
            }

            headVersions
                .values
                .firstOrNull()
                ?: createNextVersion(latestTag, now)
        } else {
            createNextVersion(latestTag, now)
        }
        val suffix = variant
            .takeUnless(String?::isNullOrBlank)
            ?.let { "-$it" }
            .orEmpty()
        val versionTag = version.toString() + suffix

        actions.addTag(versionTag)

        if (extension.autoPush.get()) {
            actions.pushTag(extension.remote.get(), versionTag)
        }
    }

    private fun createNextVersion(
        previousTag: String,
        pointInTime: LocalDateTime
    ) = CalendarVersion
        .parse(previousTag)
        .getOrNull()
        ?.increment(pointInTime)
        ?: CalendarVersion.generate(pointInTime, revision = 1)
}
