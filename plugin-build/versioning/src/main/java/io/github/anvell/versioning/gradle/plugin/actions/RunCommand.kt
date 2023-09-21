package io.github.anvell.versioning.gradle.plugin.actions

import org.gradle.api.GradleException
import java.io.Reader
import java.util.concurrent.TimeUnit

internal fun <T> runCommand(
    command: String,
    block: Reader.() -> T
): T = try {
    ProcessBuilder(command.split("\\s".toRegex()))
        .start()
        .run {
            waitFor(10, TimeUnit.SECONDS)
            inputStream
                .bufferedReader()
                .use(block)
        }
} catch (e: Exception) {
    throw GradleException(e.message ?: "Error executing command :$command")
}
