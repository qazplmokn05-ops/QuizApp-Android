#!/usr/bin/env sh

APP_HOME=$( cd "$( dirname "$0" )" && pwd )
APP_HOME=${APP_HOME%/}

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ ! -f "$CLASSPATH" ]; then
  echo "Gradle wrapper JAR not found. Please generate wrapper in Android Studio or run gradle wrapper."
  exit 1
fi

java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
