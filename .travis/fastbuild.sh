#!/bin/bash
set -ev;

function post_status {
    var target_url = "https://travis-ci.org/ArcBees/GWTP/builds/${TRAVIS_BUILD_ID}";
    var context = "travis-ci/fast-build";
    var url = "/repos/${TRAVIS_REPO_SLUG}/statuses/${TRAVIS_COMMIT}";

    var status_data = "{
        \"state\": \"${1}\",
        \"target_url\": \"${target_url}\",
        \"description\": \"${2}\",
        \"context\": \"${context}\"
    }";

    curl --data status_data url;
}

if [ "${TRAVIS_PULL_REQUEST}" = "true" ]; then
    post_status "pending" "Checking for compilation or checkstyle errors.";

    mvn install -Dskiptests=true;

    var state;
    var description;

    if [ "$?" -ne 0 ]; then
        post_status "error" "The build contains checkstyle errors!";
    else
        post_status "success" "The build has no checkstyle errors!";
    fi
fi

exit 0;
