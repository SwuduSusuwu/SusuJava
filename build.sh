#!/bin/sh
#
# /* This is the new build script for `./susuwu/FishSim.java`. */
# /* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
#  * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./build.sh` (henceforth "*this source code*").
#  * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
#  * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those. */

PATH_TO_FX="${PATH_TO_FX:-"/usr/share/openjfx/lib/"}" # /* Notice: prefix " "s in path with slashes, such as "\ ". */
PATH_TO_CLASS="susuwu/FishSim"
PATH_TO_SOURCE="${PATH_TO_CLASS}.java"
JAVA_MODULES="${JAVA_MODULES} --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml" # /* Notice: quotes around `${PATH_TO_FX}` give errors, so ensure to escape the path in `PATH_TO_FX=...` */
#JAVA_MODULES="${JAVA_MODULES} --module-path \"${PATH_TO_FX}\" --add-modules javafx.controls,javafx.fxml" # /* Notice: those quotes (around `${PATH_TO_FX}`) give "java.lang.module.FindException: Module javafx.controls not found" */
JAVA_FLAGS="${JAVA_FLAGS} --enable-native-access=javafx.graphics"
#JAVA_FLAGS="${JAVA_FLAGS} -XX:+HeapDumpOnOutOfMemoryError " # /* Notice: if "Exception java.lang.OutOfMemoryError occurred dispatching signal SIGINT to handler- the VM may need to be forcibly terminated" then uncomment this to use `jhat java_pid*.hprof` */
JAVA_FLAGS="${JAVA_FLAGS} -enableassertions" # /* Notice: remove `-enableassertions` so performance improves */
export JAVA_BUILD_TEST_FLAGS="-verbose"
export JAVA_TEST_FLAGS="-verbose:module"
if command -v sudo >/dev/null; then
	APTITUDE="sudo apt -y install "
else
	APTITUDE="apt -y install " # /* Fixes "The program sudo is not installed." on platforms such as smartphones */
fi
if ! test -d "${PATH_TO_FX}"; then # /* TODO: search for this if default (**Ubuntu**'s) path is not found */
	${APTITUDE} openjfx || ${APTITUDE} libopenjfx-java
	if ! test -d "${PATH_TO_FX}"; then
		echo "$0: '${PATH_TO_FX}' dir not found. Use \`${APTITUDE} install openjfx\`, then set '\${PATH_TO_FX}' to the actual libs."
	fi
fi
command -v java >/dev/null || ${APTITUDE} openjdk-21-jdk-headless || ${APTITUDE} default-jdk-headless

if [ -n "${GITHUB_ACTIONS}" ]; then
#shellcheck disable=SC2086 # /* Quotes cause "Unrecognized option:" */
	javac ${JAVA_BUILD_TEST_FLAGS} ${JAVA_MODULES} ${PATH_TO_SOURCE} # /* Gives "Missing JavaFX application class susuwu/FishSim" unless `cd $(dirname ${PATH_TO_CLASS})` is used. */
#	java ${JAVA_TEST_FLAGS} ${JAVA_FLAGS} ${JAVA_MODULES} ${PATH_TO_CLASS} # /* Assumes `main()` will `return` (but, `FishSim.java`'s does not) */
#	java ${JAVA_TEST_FLAGS} ${JAVA_FLAGS} --source 16 ${JAVA_MODULES} ${PATH_TO_SOURCE} # /* Assumes `main()` will `return` (but, `FishSim.java`'s does not) */
else
#shellcheck disable=SC2086 # /* Quotes cause "Unrecognized option:" */
	java ${JAVA_FLAGS} --source 16 ${JAVA_MODULES} ${PATH_TO_SOURCE} # /* `---source` is workaround for "Missing JavaFX application class susuwu/FishSim" */
fi

exit $? #Status required so [*CodeQL*](https://docs.github.com/en/code-security/code-scanning/introduction-to-code-scanning/about-code-scanning-with-codeql) passes.

