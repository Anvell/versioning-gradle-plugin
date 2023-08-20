package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.actions.git.addTag
import io.github.anvell.versioning.gradle.plugin.actions.git.getBranchName
import io.github.anvell.versioning.gradle.plugin.actions.git.getTag
import io.github.anvell.versioning.gradle.plugin.actions.git.pushTag
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

        project.tasks.register("publishVersionTag") { task ->
            task.group = "versioning"
            with(extension) {
                remote.convention("origin")
                autoPush.convention(false)
            }

            task.doLast {
                val branch = getBranchName()
                val branches = extension.branches.get()
                val now = LocalDateTime.now(ZoneOffset.UTC)
                val suffix = branches[branch]
                    .takeUnless(String?::isNullOrBlank)
                    ?.let { "-$it" }
                    ?: ""

                if (branch !in branches) {
                    println("Skipping auto versioning for current branch.")
                    return@doLast
                }
                val version = createVersionTag(now) + suffix

                addTag(version)

                if (extension.autoPush.get()) {
                    pushTag(extension.remote.get(), version)
                }
            }
        }
    }

    private fun createVersionTag(pointInTime: LocalDateTime): String {
        val version = CalendarVersion
            .parse(getTag().substringBefore('-'))
            .getOrNull()
            ?.increment(pointInTime)
            ?: CalendarVersion.generate(pointInTime, revision = 1)

        return version.toString()
    }
}
