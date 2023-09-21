package io.github.anvell.versioning.gradle.plugin.actions

interface VcsActions {
    fun getBranchName(): String

    fun getLatestTag(): String

    fun addTag(tag: String)

    fun pushTag(remote: String, tag: String)
}
