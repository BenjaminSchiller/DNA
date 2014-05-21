#!/bin/bash

TESTCLASSES=()

for todoLine in "$@"
do
	SINGLELINE=${todoLine:14}
	SINGLELINE=${SINGLELINE%.java}
	
	if [ $# -eq 1 ]; then
		TESTCLASSES+=($SINGLELINE)
	else
		case "$SINGLELINE" in
			BatchTest|GeneratorsTest)
				;;
			*)
				TESTCLASSES+=($SINGLELINE)
				;;
		esac
	fi
done

for todoLine in ${TESTCLASSES[@]}
do
	ant -DaspectJDir=lib/ -DjUnitJar=lib/junit.jar travisTest -DtestClass=$todoLine
done