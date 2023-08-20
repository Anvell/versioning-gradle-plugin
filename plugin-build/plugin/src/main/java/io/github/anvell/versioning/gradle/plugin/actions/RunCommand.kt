package io.github.anvell.versioning.gradle.plugin.actions

import org.gradle.api.GradleException
import java.io.BufferedReader
import java.util.concurrent.TimeUnit

internal fun runCommand(command: String): String = try {
    ProcessBuilder(command.split("\\s".toRegex()))
        .start()
        .run {
            waitFor(10, TimeUnit.SECONDS)
            inputStream
                .bufferedReader()
                .use(BufferedReader::readText)
                .trim()
        }
} catch (e: Exception) {
    throw GradleException(e.message ?: "Error executing command :$command")
}
