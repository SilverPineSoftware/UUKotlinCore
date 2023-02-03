#!/bin/sh

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $SCRIPT_DIR

# import common helper functions
. "$SCRIPT_DIR/common_build_functions.sh"


GRADLE_PROPERTIES_FILE=../gradle.properties

UUReadGitBranch GIT_BRANCH
UUReadGitRevisionNumber GIT_REV_NUMBER
UUReadLatestGitTag GIT_TAG

UUDebugLog "GIT_BRANCH: ${GIT_BRANCH}"
UUDebugLog "GIT_REV_NUMBER: ${GIT_REV_NUMBER}"
UUDebugLog "GIT_TAG: ${GIT_TAG}"

UUReadGradlePropertiesVersion $GRADLE_PROPERTIES_FILE EXISTING_VERSION

TO_REPLACE="version=${EXISTING_VERSION}"

UUDebugLog "EXISTING_VERSION: $EXISTING_VERSION"
UUDebugLog "TO_REPLACE: ${TO_REPLACE}"

if [ "$GIT_BRANCH" != "main" ] && [ "$GIT_BRANCH" != "HEAD" ]
then
UPDATED_VERSION="${EXISTING_VERSION}.${GIT_REV_NUMBER}-${GIT_BRANCH}-SNAPSHOT"
else
UPDATED_VERSION="${GIT_TAG}"
fi

UUDebugLog "UPDATED_VERSION: ${UPDATED_VERSION}"

REPLACEMENT="version=${UPDATED_VERSION}"

UUDebugLog "REPLACEMENT: ${REPLACEMENT}"

UUReplaceStringInFile $GRADLE_PROPERTIES_FILE $TO_REPLACE $REPLACEMENT
