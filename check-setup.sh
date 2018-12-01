#!/bin/bash

if [ ! -f ${JAVA_11_HOME}/javac ]; then
  >&2 echo 'JAVA_11_HOME must be set before building.'
  exit 1
fi
