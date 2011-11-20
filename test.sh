#!/bin/bash

# Run all omakase tests.

pushd $(dirname $0) > /dev/null

test/run-tests.sh

popd > /dev/null
