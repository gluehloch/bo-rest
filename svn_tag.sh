#!/bin/bash
# SET release
#
# !!! Take care for the release name !!!
#

function pause(){
   read -p "$*"
}

RELEASE_FLAG="0.2.3_RELEASE"
PROJECT_NAME='betoffice-jweb'

BASE_URL="https://svn.code.sf.net/p/betoffice/svn/main"

TRUNK="${BASE_URL}/trunk/${PROJECT_NAME}"
BRANCH="${BASE_URL}/branches/${PROJECT_NAME}-${RELEASE_FLAG}"
TAG="${BASE_URL}/tags/${PROJECT_NAME}-${RELEASE_FLAG}"

echo "------------------------------------------------"
echo "Execute svn tag and branch:"
echo ""
echo "Project: $PROJECT_NAME"
echo "Version: $RELEASE_FLAG"
echo ""
echo "Tag and branch:"
echo "    $PROJECT_NAME-$RELEASE_FLAG"
echo ""
echo "    TRUNK.: ${TRUNK}"
echo "    BRANCH: ${BRANCH}"
echo "    TAG...: ${TAG}"

pause 'Press [Enter] key to continue...'

command="svn copy ${TRUNK} ${TAG} -m \"TAG: ${PROJECT_NAME}-${RELEASE_FLAG}\" "
echo $command
eval $command

command="svn copy ${TRUNK} ${BRANCH} -m \"BRANCH: ${PROJECT_NAME}-${RELEASE_FLAG}\" "
echo $command
eval $command

#echo "Tag and branch are created."
echo "Tag is created!"

exit 1
