#!/bin/sh

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $SCRIPT_DIR
echo "SCRIPT_DIR: $SCRIPT_DIR"

# import common helper functions
. common_build_functions.sh

GRADLE_PROPERTIES_FILE=../gradle.properties

UUReadGitRevisionNumber BUILD_NUMBER

UUReadGradlePropertiesVersion $GRADLE_PROPERTIES_FILE EXISTING_VERSION

TO_REPLACE="version=${EXISTING_VERSION}"

UUDebugLog "EXISTING_VERSION: $EXISTING_VERSION"
UUDebugLog "TO_REPLACE: ${TO_REPLACE}"

UPDATED_VERSION="${EXISTING_VERSION}-DEV-${BUILD_NUMBER}"

UUDebugLog "UPDATED_VERSION: ${UPDATED_VERSION}"

REPLACEMENT="version=${UPDATED_VERSION}"

UUDebugLog "REPLACEMENT: ${REPLACEMENT}"

UUReplaceStringInFile $GRADLE_PROPERTIES_FILE $TO_REPLACE $REPLACEMENT

cd ..

./gradlew clean :library:assembleRelease :library:publishToMavenLocal

cd $SCRIPT_DIR
UURevertGitChanges $GRADLE_PROPERTIES_FILE