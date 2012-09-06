#!/bin/bash

OUT_DIR=$(dirname $0)/out

java -jar "$OUT_DIR/omasymbol.jar" $*

