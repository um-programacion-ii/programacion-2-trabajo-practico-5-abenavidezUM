#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# Maven Wrapper shell script

MAVEN_OPTS="${MAVEN_OPTS:-}"

basedir="$(cd "$(dirname "$0")" && pwd)"

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$basedir}"
export MAVEN_PROJECTBASEDIR

# Look for the maven wrapper jar
if [ -r "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" ] ; then
    jarfile="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
else
    if command -v wget > /dev/null; then
        echo "Downloading maven wrapper"
        wget "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -O "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
    elif command -v curl > /dev/null; then
        echo "Downloading maven wrapper"
        curl -L "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -o "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
    else
        echo "Unable to download maven wrapper"
        exit 1
    fi
fi

# Find java
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD=$(which java)
fi

if [ ! -x "$JAVACMD" ] ; then
    echo "Error: JAVA_HOME is not defined correctly." >&2
    echo "  We cannot execute $JAVACMD" >&2
    exit 1
fi

exec "$JAVACMD" $MAVEN_OPTS -classpath "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" org.apache.maven.wrapper.MavenWrapperMain "$@"
