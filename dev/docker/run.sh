#!/bin/sh

export PRIMARY_COMMAND="$@"
/app/gradlew -q bootRun
