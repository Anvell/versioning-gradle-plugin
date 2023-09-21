@file:Suppress("SpellCheckingInspection")

package io.github.anvell.versioning.gradle.plugin.actions

import org.gradle.api.GradleException

class GitVcsActions : VcsActions {
    override fun getBranchName() = runCommand(
        "git rev-parse --abbrev-ref HEAD"
    ) {
        val branchName = readText().trim()
        println("Current branch: $branchName")

        branchName
    }

    override fun getLatestTag() = runCommand(
        "git tag --sort=-committerdate"
    ) {
        val tag = readLines().firstOrNull()?.trim()

        if (tag.isNullOrBlank()) {
            throw GradleException("Error reading current tag")
        }
        println("Latest tag: $tag")

        tag
    }

    override fun addTag(tag: String) = runCommand(
        "git tag $tag"
    ) {
        println(readText())
    }

    override fun pushTag(remote: String, tag: String) = runCommand(
        "git push $remote $tag"
    ) {
        println(readText())
    }
}
