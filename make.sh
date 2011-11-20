#!/bin/bash

pushd $(dirname $0) > /dev/null

echo Building Omakase...

mkdir -p out > /dev/null

cp lib/guava-10.0.1/guava-10.0.1.jar out
cp lib/joda-primitives-1.0/joda-primitives-1.0.jar out
javac -cp "lib/guava-10.0.1/guava-10.0.1.jar;out/joda-primitives-1.0.jar" -d out -sourcepath src src/omakase/printtokens/Program.java

jar mcf build/omascan.MF out/omakase.jar -C out omakase

popd > /dev/null
