package com.here.gradle.plugins.jobdsl.tasks

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class AbstractTaskTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile
    List<File> pluginClasspath
    GradleRunner gradleRunner

    def classpathString() {
        def classpath = pluginClasspath
                .collect { it.absolutePath.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { "'$it'" }
                .join(', ')
        new File('/tmp/classpath') << "${classpath.replace(', ', '\n')}\n"
        return classpath
    }

    def readResource(String path) {
        getClass().classLoader.getResource(path).text
    }

    def readBuildGradle(String path) {
        readResource(path).replace('CLASSPATH_STRING', classpathString())
    }

    def copyResourceToTestDir(String from, String to = 'src/jobdsl/jobdsl.groovy') {
        testProjectDir.newFile(to) << readResource(from)
    }

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        testProjectDir.newFolder('src', 'jobdsl')

        gradleRunner = GradleRunner.create().withProjectDir(testProjectDir.root)

        def resource = getClass().classLoader.findResource('plugin-under-test-metadata.properties')
        if (resource == null) {
            throw new IllegalStateException('Could not find metadata')
        }

        pluginClasspath = resource.readLines()[0].split('=')[1].split('\\\\:').collect { new File(it) }
    }

}
