package io.github.anvell.versioning.gradle.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertNotNull
import org.junit.Test

class GradleVersioningPluginTest {
    @Test
    fun `Plugin is applied correctly to the project`() {
        val project =
            ProjectBuilder
                .builder()
                .build()
        project.pluginManager.apply("io.github.anvell.versioning.gradle.plugin")
    }

    @Test
    fun `Extension is created correctly`() {
        val project =
            ProjectBuilder
                .builder()
                .build()
        project.pluginManager.apply("io.github.anvell.versioning.gradle.plugin")

        assertNotNull(project.extensions.getByName("configureVersioning"))
    }
}
