package io.github.anvell.versioning.gradle.plugin.actions

import org.gradle.api.GradleException

class GitVcsActions : VcsActions {
    override fun getBranchName() = runCommand(
        "git rev-parse --abbrev-ref HEAD"
    )

    override fun getLatestTag() = runCommand(
        "git tag --sort=-committerdate | head -n 1"
    ).ifEmpty {
        throw GradleException("Error reading current tag")
    }

    override fun addTag(tag: String) {
        runCommand("git tag $tag")
    }

    override fun pushTag(remote: String, tag: String) {
        runCommand("git push $remote $tag")
    }
}
