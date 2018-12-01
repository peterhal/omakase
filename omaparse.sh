#!/bin/bash

OUT_DIR=$(dirname $0)/out

$(dirname $0)/check-setup.sh

${JAVA_11_HOME}/java -jar "$OUT_DIR/omaparse.jar" $*

