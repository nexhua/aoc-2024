#!/bin/bash

set -e

if [ -z "$1" ]
    then
        echo "Please specify AOC day to create"
        exit 1
fi

FILE_NAME=$1

if [ -d "$FILE_NAME" ]
    then
        echo "$FILE_NAME already exists!"
        exit 1
fi

LEN=${#FILE_NAME}

if [ $LEN -eq 2 ]
    then
        mvn archetype:generate -B -DarchetypeGroupId=com.mkoca.archetypes -DarchetypeArtifactId=aoc -DarchetypeVersion=1.0 -DgroupId=com.mkoca.aoc2024 -DartifactId="$FILE_NAME" -Dversion=1.0
fi

git add $FILE_NAME