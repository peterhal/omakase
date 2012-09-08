#!/bin/bash

# Run all omakase tests

pushd $(dirname $0) > /dev/null

OUT_DIR=../out
mkdir -p $OUT_DIR > /dev/null

RESULTS_FILE=$OUT_DIR/results.txt

echo Running Omakase Tests ...
echo Running Omakase Tests ... > $RESULTS_FILE

echo Scanning Tests ... >> $RESULTS_FILE 2>&1
for error_source in errors/*.oma ; do
  ../omascan.sh $error_source >> $RESULTS_FILE 2>&1
done

for scanner_source in scanner/*.oma ; do
  ../omascan.sh $scanner_source >> $RESULTS_FILE 2>&1
done

echo Parsing Tests ... >> $RESULTS_FILE 2>&1
for parse_source in parser/*.oma ; do
  ../omaparse.sh $parse_source >> $RESULTS_FILE 2>&1
done

echo Symbol Tests ... >> $RESULTS_FILE 2>&1
for symbol_source in symbols/*.oma ; do
  ../omasymbol.sh $symbol_source >> $RESULTS_FILE 2>&1
done

echo Compiling Tests ... >> $RESULTS_FILE 2>&1
for compile_source in compile/*.oma ; do
  ../oma.sh $compile_source >> $RESULTS_FILE 2>&1
done

if diff expected-results.txt $RESULTS_FILE > $OUT_DIR/results.dif ; then
  echo Tests Passed.
else
  echo Tests Failed. See out/results.diff for details.
  cat $OUT_DIR/results.dif
fi

popd > /dev/null
