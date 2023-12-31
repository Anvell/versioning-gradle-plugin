@file:Suppress("SpellCheckingInspection")

package io.github.anvell.versioning.gradle.plugin.actions

import org.gradle.api.GradleException

class GitVcsActions : VcsActions {
    override fun getBranchName() =
        runCommand("git", "rev-parse", "--abbrev-ref", "HEAD") {
            val branchName = readText().trim()
            println("Current branch: $branchName")

            branchName
        }

    override fun getLatestTag() =
        runCommand("git", "tag", "--sort=-committerdate") {
            val tag = readLines().firstOrNull()?.trim()

            if (tag.isNullOrBlank()) {
                throw GradleException("Error reading current tag")
            }
            println("Latest tag: $tag")

            tag
        }

    override fun getHeadTags(): List<String> =
        runCommand("git", "tag", "--points-at", "HEAD") {
            readLines().map(String::trim)
        }

    override fun commitFile(filepath: String, comment: String) {
        runCommand("git", "add", filepath) {
            println(readText())
        }

        runCommand("git", "commit", "-m", comment, filepath) {
            println(readText())
        }
    }

    override fun addTag(tag: String) =
        runCommand("git", "tag", tag) {
            println(readText())
        }

    override fun pushHead(remote: String) =
        runCommand("git", "push", remote, "HEAD") {
            println(readText())
        }

    override fun pushTag(remote: String, tag: String) =
        runCommand("git", "push", remote, tag) {
            println(readText())
        }

    override fun getLatestContents(filePath: String): String {
        val hash = runCommand("git", "log", "--all", "--pretty=format:%H", "-n", "1") {
            readText()
        }

        return runCommand("git", "show", "$hash:$filePath") { readText() }
    }
}
