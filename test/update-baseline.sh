#!/bin/bash

# Update the test baseline file.

pushd $(dirname $0) > /dev/null

./run-tests.sh
cp ../out/results.txt expected-results.txt

popd > /dev/null
