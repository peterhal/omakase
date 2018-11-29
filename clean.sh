#!/bin/bash

pushd $(dirname $0) > /dev/null

echo Cleaning Omakase...

rm -rf out > /dev/null

popd > /dev/null
