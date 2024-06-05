package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.models.CalendarVersion
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class CalendarVersionTest {

    @Test
    fun `Short version format is respected`() {
        val dateTime = LocalDateTime.of(2024, 1, 10, 12, 0)
        val version = CalendarVersion
            .parse(dateTime, "2047.01.10")
            .getOrThrow()

        assertEquals("2047.01.10", version.formatVersion(false))
        assertEquals("47.01.10", version.formatVersion(true))
    }

    @Test
    fun `When year value is short it's expanded to full`() {
        val dateTime = LocalDateTime.of(2024, 1, 10, 12, 0)
        val version = CalendarVersion
            .parse(dateTime, "24.01.10")
            .getOrThrow()

        assertEquals("2024.01.10", version.formatVersion(false))
        assertEquals("24.01.10", version.formatVersion(true))
    }

    @Test
    fun `Short major is expanded when value is incremented`() {
        val dateTime = LocalDateTime.of(2024, 1, 10, 12, 0)
        val version = CalendarVersion(24, 1, 10).increment(dateTime)

        assertEquals("2024.01.11", version.formatVersion(false))
        assertEquals("24.01.11", version.formatVersion(true))
    }
}
