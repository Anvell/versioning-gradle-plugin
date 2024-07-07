package io.github.anvell.versioning.gradle.plugin.extensions

internal fun Int.toPaddedString(length: Int) = toString().padStart(length, '0')
