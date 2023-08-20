package io.github.anvell.versioning.gradle.plugin.actions.git

import io.github.anvell.versioning.gradle.plugin.actions.runCommand

internal fun pushTag(remote: String, tag: String) = runCommand("git push $remote $tag")
