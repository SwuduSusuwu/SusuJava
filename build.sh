#!/bin/sh
#
# /* This is the new build script for `./susuwu/FishSim.java`. */
# /* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
#  * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./build.sh` (henceforth "*this source code*").
#  * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
#  * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those. */

PATH_TO_CLASS="susuwu/FishSim"
PATH_TO_SOURCE="${PATH_TO_CLASS}.java"
PATH_TO_NATIVE="susuwu/sdl_gles2_jni.c"
PATH_TO_NATIVE_LIB="susuwu/libsdl_gles2_jni.so" # /* `.so` on Linux/Android, `.dll` on Windows, `.dylib` on macOS */
JAVA_FLAGS="${JAVA_FLAGS} -enableassertions" # /* Notice: remove `-enableassertions` so performance improves */
JAVA_FLAGS="${JAVA_FLAGS} -Djava.library.path=susuwu" # /* Allows JNI to find `libsdl_gles2_jni.so` */
export JAVA_BUILD_TEST_FLAGS="-verbose"
export JAVA_TEST_FLAGS="-verbose:module"
if command -v sudo >/dev/null; then
	APTITUDE="sudo apt -y install "
else
	APTITUDE="apt -y install " # /* Fixes "The program sudo is not installed." on platforms such as smartphones */
fi
command -v java >/dev/null || ${APTITUDE} openjdk-21-jdk-headless || ${APTITUDE} default-jdk-headless

if ! dpkg -l libsdl2-dev >/dev/null 2>&1; then # /* Install SDL2 + GLES2 dev headers (replaces `openjfx`) */
	${APTITUDE} libsdl2-dev libgles2-mesa-dev || true
fi

# /* Compile the JNI native library: `libsdl_gles2_jni.so` (replaces `--module-path`/`--add-modules javafx.*`) */
JAVA_HOME="${JAVA_HOME:-$(java -XshowSettings:properties -version 2>&1 | grep 'java.home' | sed 's/.*= //')}"
JNI_INCLUDES="-I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux" # /* Linux; macOS uses `include/darwin`, Android uses NDK paths */
#shellcheck disable=SC2086 # /* Quotes cause errors with pkg-config output */
cc -shared -fPIC "${PATH_TO_NATIVE}" -o "${PATH_TO_NATIVE_LIB}" ${JNI_INCLUDES} $(pkg-config --cflags --libs sdl2) -lGLESv2 || exit $?

if [ -n "${GITHUB_ACTIONS}" ]; then
#shellcheck disable=SC2086 # /* Quotes cause "Unrecognized option:" */
	javac ${JAVA_BUILD_TEST_FLAGS} susuwu/SdlGles2.java susuwu/SimUsages.java ${PATH_TO_SOURCE} # /* Gives "Missing JavaFX application class susuwu/FishSim" unless `cd $(dirname ${PATH_TO_CLASS})` is used. */
#	java ${JAVA_TEST_FLAGS} ${JAVA_FLAGS} ${PATH_TO_CLASS} # /* Assumes `main()` will `return` (but, `FishSim.java`'s does not) */
#	java ${JAVA_TEST_FLAGS} ${JAVA_FLAGS} --source 16 ${PATH_TO_SOURCE} # /* Assumes `main()` will `return` (but, `FishSim.java`'s does not) */
else
#shellcheck disable=SC2086 # /* Quotes cause "Unrecognized option:" */
	java ${JAVA_FLAGS} --source 16 ${PATH_TO_SOURCE} # /* `--source` is workaround for "error: cannot find symbol\n...\n  symbol: {class Force, variable Utils}" when not compiling all sources together */
fi

exit $? #Status required so [*CodeQL*](https://docs.github.com/en/code-security/code-scanning/introduction-to-code-scanning/about-code-scanning-with-codeql) passes.

