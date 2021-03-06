#!/bin/bash

set -e

pushd $(dirname $0) > /dev/null

./check-setup.sh

echo Building Omakase...

mkdir -p out > /dev/null

cp lib/guava-10.0.1/guava-10.0.1.jar out
cp lib/joda-primitives-1.0/joda-primitives-1.0.jar out

${JAVA_11_HOME}/javac -Xlint:unchecked -cp "lib/guava-10.0.1/guava-10.0.1.jar:out/joda-primitives-1.0.jar" -d out -sourcepath src src/omakase/printtokens/Program.java src/omakase/printtree/Program.java src/omakase/codegeneration/Program.java src/omakase/printsymbols/Program.java

${JAVA_11_HOME}/jar mcf build/omascan.MF out/omascan.jar -C out omakase

${JAVA_11_HOME}/jar mcf build/omaparse.MF out/omaparse.jar -C out omakase

${JAVA_11_HOME}/jar mcf build/omasymbol.MF out/omasymbol.jar -C out omakase

${JAVA_11_HOME}/jar mcf build/oma.MF out/oma.jar -C out omakase

popd > /dev/null
