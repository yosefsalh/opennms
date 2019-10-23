#!/bin/sh -e

find_tests()
{
    # Generate surefire test list
    circleci tests glob **/src/test/java/**/*Test*.java |\
        sed -e 's#^.*src/test/java/\(.*\)\.java#\1#' | tr "/" "." > surefire_classnames
    cat surefire_classnames | circleci tests split --split-by=timings --timings-type=classname > /tmp/this_node_tests

    # Generate failsafe list
    circleci tests glob **/src/test/java/**/*IT*.java |\
        sed -e 's#^.*src/test/java/\(.*\)\.java#\1#' | tr "/" "." > failsafe_classnames
    cat failsafe_classnames | circleci tests split --split-by=timings --timings-type=classname > /tmp/this_node_it_tests
}

# Tests are forked out in separate JVMs, so the Maven runner shouldn't need a big heap
export MAVEN_OPTS="-Xms256m -Xms256m"

cd ~/project/smoke-test-v2
echo "#### Executing complete suite of smoke/system tests"
find_tests
# Iterate through the tests and stop after the first failure
for TEST_CLASS in $(cat /tmp/this_node_it_tests)
do
  echo "###### Testing: ${TEST_CLASS}"
  mvn -N -DskipTests=false -DskipITs=false -DfailIfNoTests=false -Dit.test=$TEST_CLASS install verify
done