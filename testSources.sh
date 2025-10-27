#!/bin/sh
#
# Simple unit tests

BUILD="java --source 16"
BUILD="javac"
DIFF2="git diff --no-index"
PATH_TO_SOURCES="susuwu/"
for SUSUWU_SOURCE in "Calculus" "ImmutablePos" "Pos" "ImmutablePos2" "Pos2" "ImmutablePosBounds" "PosBounds" "Forces"; do # TODO: "SimUsages" (requires `javafx`)
	${BUILD} "${PATH_TO_SOURCES}${SUSUWU_SOURCE}".java
done
if [ ! "--all" = "${1}" ]; then
	echo "${0}: you should also use \`./${PATH_TO_SOURCES}FishSimTests.sh\`, or use \`${0} --all\`, so tests for \`${PATH_TO_SOURCES}FishSim.sh\` are included"
	return # todo: && of the `for` loop test values
fi

PATH_TO_RESULTS="${PATH_TO_SOURCES}FishSim_local.log"
PATH_TO_SUCCESS="${PATH_TO_SOURCES}FishSim_stock.log"
${BUILD} "${PATH_TO_SOURCES}FishSim.java" 2>"${PATH_TO_RESULTS}"
if ! test -e "${PATH_TO_SUCCESS}"; then
#	cp "${PATH_TO_RESULTS}" "${PATH_TO_SUCCESS}"
	echo "'${PATH_TO_SUCCESS}' not found"
fi
${DIFF2} "${PATH_TO_RESULTS}" "${PATH_TO_SUCCESS}"

