package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.actions.GitVcsActions
import io.github.anvell.versioning.gradle.plugin.models.CalendarVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val PublishTagTaskLabel = "publishVersionTag"
private const val PublishCatalogTaskLabel = "publishVersion"
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
                useShorterFormat.convention(false)
                commitTemplate.convention("Version: %s")
            }

        with(project) {
            afterEvaluate {
                for (variant in extension.variants.get()) {
                    tasks.register(PublishTagTaskLabel + variant.capitalized()) { task ->
                        task.group = TaskGroupLabel

                        task.doLast { publishVersionTag(extension, variant) }
                    }
                }

                tasks.register(PublishCatalogTaskLabel) { task ->
                    task.group = TaskGroupLabel

                    task.doLast { publishVersion(project, extension) }
                }
            }
        }
    }

    private fun publishVersion(
        project: Project,
        extension: GradleVersioningExtension
    ) {
        val actions = extension.actions.get()
        val file = extension.versionCatalog.asFile.get()
        val useShorterFormat = extension.useShorterFormat.get()

        if (!extension.versionCatalog.isPresent) {
            println("Version catalog property is not configured")
            return
        }

        val vcsPath = file
            .toRelativeString(project.rootDir)
            .replace('\\', '/')
        val vcsContent = actions.getLatestContents(vcsPath)
        val now = LocalDateTime.now(ZoneOffset.UTC)

        if (vcsContent.isEmpty()) {
            println("No version catalog is found, creating new one: $vcsPath")

            val newVersion = CalendarVersion.create(now, revision = 1)
            val newCode = 1L
            val newContent = VersionCatalogManager.serialize(
                version = newVersion,
                code = newCode,
                useShorterFormat = useShorterFormat
            )
            file.writeText(newContent)

            val comment = extension
                .commitTemplate
                .get()
                .format(newVersion.formatVersion(useShorterFormat))
            actions.commitFile(vcsPath, comment)
        } else {
            val (prevVersion, prevCode) = VersionCatalogManager.deserialize(vcsContent)
            val newVersion = prevVersion.increment(now)
            val newCode = prevCode + 1
            val newContent = VersionCatalogManager.serialize(
                version = prevVersion.increment(now),
                code = newCode,
                useShorterFormat = useShorterFormat
            )
            file.writeText(newContent)

            val comment = extension
                .commitTemplate
                .get()
                .format(
                    "${prevVersion.formatVersion(useShorterFormat)} → " +
                        newVersion.formatVersion(useShorterFormat)
                )
            actions.commitFile(vcsPath, comment)
        }

        if (extension.autoPush.get()) {
            actions.pushHead(extension.remote.get())
        }
    }

    private fun publishVersionTag(
        extension: GradleVersioningExtension,
        variant: String? = null
    ) {
        val actions = extension.actions.get()
        val branch = actions.getBranchName()
        val branches = extension.branches.get()
        val shortFormat = extension.useShorterFormat.get()

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
                    CalendarVersion.parse(now, tag).onSuccess {
                        put(tag.substringAfter(CalendarVersion.SuffixSeparator, ""), it)
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
                ?: calendarVersionFrom(latestTag, now)
        } else {
            calendarVersionFrom(latestTag, now)
        }
        val suffix = variant.takeUnless(String?::isNullOrBlank)
        val versionTag = version.formatVersion(shortFormat, suffix)

        actions.addTag(versionTag)

        if (extension.autoPush.get()) {
            actions.pushTag(extension.remote.get(), versionTag)
        }
    }

    private fun calendarVersionFrom(
        previousTag: String,
        pointInTime: LocalDateTime
    ) = CalendarVersion
        .parse(pointInTime, previousTag)
        .getOrNull()
        ?.increment(pointInTime)
        ?: CalendarVersion.create(pointInTime, revision = 1)
}
