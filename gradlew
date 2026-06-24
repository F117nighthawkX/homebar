#!/bin/sh

# Gradle wrapper bootstrap script. The wrapper JAR is acquired with the project
# scaffolding when Gradle is available; this script intentionally follows the
# standard wrapper contract for Unix-based development environments.
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P) || exit
exec java -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"

