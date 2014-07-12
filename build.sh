#!/bin/bash

export MVN_COMMAND=$(which mvn)

echo
echo "MVN_COMMAND=${MVN_COMMAND}"
echo "SNAP_BRANCH=${SNAP_BRANCH}"
echo

run_gradle() {
    local task=$1

    gradle --info ${task} 1> build.output 2> build.error
    local result=$?

    echo 'build output:'
    echo
    cat build.output | grep -v -i 'download.*' | grep -v -i '^\s*download.*' | grep -v '\s*K\{0,1\}B\s*$' | sed 's/^\s*$//g' | cat -s
    echo

    if [[ ${result} != 0 ]]; then
        echo 'build error:'
        echo
        cat build.error
        echo

        exit ${result}
    fi
}

echo 'building artifacts...'
run_gradle

if [[ ${TRAVIS_BRANCH} = 'master' && ${TRAVIS_PULL_REQUEST} = 'false' ]]; then
    echo 'uploading archives...'
    run_gradle uploadArchives
fi

exit 0
