#!/bin/sh -e

# TODO MVR probably make generic :D
TEXT_CLASS="UiTestSuite"
echo "#### Executing ${TEST_CLASS}"
mvn -N -DskipTests=false -DskipITs=false -Dit.test=$TEST_CLASS install verify