package io.github.anvell.versioning.gradle.plugin.models

import io.github.anvell.versioning.gradle.plugin.extensions.toPaddedString
import java.time.LocalDateTime

private const val NumPadding = 2

internal data class CalendarVersion(
    val major: Int,
    val minor: Int,
    val revision: Int
) {

    fun increment(pointInTime: LocalDateTime) = when {
        major == pointInTime.year &&
            minor == pointInTime.monthValue -> {
            copy(revision = revision + 1)
        }
        else -> {
            generate(pointInTime, 1)
        }
    }

    override fun toString() = "$major" +
        ".${minor.toPaddedString(NumPadding)}" +
        ".${revision.toPaddedString(NumPadding)}"

    companion object {
        fun generate(
            pointInTime: LocalDateTime,
            revision: Int
        ) = CalendarVersion(
            major = pointInTime.year,
            minor = pointInTime.monthValue,
            revision = revision
        )

        fun parse(versionTag: String) = runCatching {
            val rawValues = versionTag
                .substringBefore('-')
                .split('.')

            require(rawValues.size == 3) {
                "Value '$versionTag' does not match versioning scheme."
            }

            CalendarVersion(
                major = rawValues[0].toInt(),
                minor = rawValues[1].toInt(),
                revision = rawValues[2].toInt()
            )
        }
    }
}
