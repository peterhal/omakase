#!/bin/bash

# Clean all generated build and test files.

pushd $(dirname $0) > /dev/null

rm -rf out

popd > /dev/null
