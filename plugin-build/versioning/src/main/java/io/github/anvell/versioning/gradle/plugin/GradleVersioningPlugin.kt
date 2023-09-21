package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.actions.GitVcsActions
import io.github.anvell.versioning.gradle.plugin.actions.VcsActions
import io.github.anvell.versioning.gradle.plugin.models.CalendarVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.time.LocalDateTime
import java.time.ZoneOffset

class GradleVersioningPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project
            .extensions
            .create("configureVersioning", GradleVersioningExtension::class.java)
            .apply {
                actions.convention(GitVcsActions())
                remote.convention("origin")
                autoPush.convention(false)
            }

        project.tasks.register("publishVersionTag") { task ->
            task.group = "versioning"

            task.doLast { publishVersionTag(extension) }
        }
    }

    private fun publishVersionTag(extension: GradleVersioningExtension) {
        val actions = extension.actions.get()
        val branch = actions.getBranchName()
        val branches = extension.branches.get()
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val suffix = branches[branch]
            .takeUnless(String?::isNullOrBlank)
            ?.let { "-$it" }
            ?: ""

        if (branch !in branches) {
            println("Skipping auto versioning for current branch")
            return
        }
        val version = createVersionTag(actions, now) + suffix

        actions.addTag(version)

        if (extension.autoPush.get()) {
            actions.pushTag(extension.remote.get(), version)
        }
    }

    private fun createVersionTag(
        actions: VcsActions,
        pointInTime: LocalDateTime
    ): String {
        val version = CalendarVersion
            .parse(actions.getLatestTag().substringBefore('-'))
            .getOrNull()
            ?.increment(pointInTime)
            ?: CalendarVersion.generate(pointInTime, revision = 1)

        return version.toString()
    }
}
