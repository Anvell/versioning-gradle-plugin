package io.github.anvell.versioning.gradle.plugin.actions

interface VcsActions {
    fun getBranchName(): String

    fun getLatestTag(): String

    fun getHeadTags(): List<String>

    fun commitFile(filepath: String, comment: String)

    fun addTag(tag: String)

    fun pushHead(remote: String)

    fun pushTag(remote: String, tag: String)

    fun getLatestContents(filePath: String): String
}
