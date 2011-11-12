#!/bin/bash

pushd $(dirname $0) > /dev/null

java -cp "out/omakase.jar;out/guava-10.0.1.jar;out/joda-primitives-1.0.jar" omakase.printtokens.Program $*

popd > /dev/null
