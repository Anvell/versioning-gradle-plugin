package io.github.anvell.versioning.gradle.plugin.models

import io.github.anvell.versioning.gradle.plugin.extensions.toPaddedString
import java.time.LocalDateTime

private const val NumberPadding = 2
private const val ShortMajorLen = 2

internal data class CalendarVersion(
    val major: Int,
    val minor: Int,
    val revision: Int,
) {
    fun increment(pointInTime: LocalDateTime): CalendarVersion {
        val year = pointInTime.year.toString()
        val yearVariants = setOf(year, year.takeLast(ShortMajorLen))

        return when {
            major.toString() in yearVariants && minor == pointInTime.monthValue -> {
                copy(
                    // Revert to full year if it was truncated
                    major = pointInTime.year,
                    revision = revision + 1,
                )
            }
            else -> {
                create(pointInTime, 1)
            }
        }
    }

    fun formatVersion(
        useShorterFormat: Boolean,
        suffixValue: String? = null,
    ) = buildString {
        append(
            when {
                useShorterFormat -> {
                    major.toPaddedString(NumberPadding).takeLast(ShortMajorLen)
                }
                else -> {
                    major.toPaddedString(NumberPadding)
                }
            },
        )
        append(PartSeparator + minor.toPaddedString(NumberPadding))
        append(PartSeparator + revision.toPaddedString(NumberPadding))

        suffixValue?.run { append(SuffixSeparator + suffixValue) }
    }

    companion object {
        const val PartSeparator = '.'
        const val SuffixSeparator = '-'

        fun create(
            pointInTime: LocalDateTime,
            revision: Int,
        ) = CalendarVersion(
            major = pointInTime.year,
            minor = pointInTime.monthValue,
            revision = revision,
        )

        fun parse(
            pointInTime: LocalDateTime,
            versionTag: String,
        ) = runCatching {
            val yearValue = pointInTime.year.toString()
            val rawValues =
                versionTag
                    .substringBefore(SuffixSeparator)
                    .split(PartSeparator)

            require(rawValues.size == 3) {
                "Value '$versionTag' does not match versioning scheme."
            }

            // Revert to full year if it was truncated
            val majorValue =
                when {
                    rawValues[0].length == ShortMajorLen &&
                        yearValue.length > ShortMajorLen -> {
                        yearValue.take(yearValue.length - ShortMajorLen) + rawValues[0]
                    }
                    else -> rawValues[0]
                }

            CalendarVersion(
                major = majorValue.toInt(),
                minor = rawValues[1].toInt(),
                revision = rawValues[2].toInt(),
            )
        }
    }
}
