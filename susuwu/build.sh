#!/bin/sh
#
# This is the [original build script from *Solar-Pro-2*](https://poe.com/s/ehlOJYRJNsrGJttfJ4HK), plus braces plus quotes (so `shellcheck` passes), published for historical value.
if ! test -d "${PATH_TO_FX}"; then
	echo "$0: Make sure to replace \`\${PATH_TO_FX}\` with the actual path to your JavaFX SDK."
fi
javac --module-path "${PATH_TO_FX}" --add-modules javafx.controls,javafx.fxml FishSim.java
java --module-path "${PATH_TO_FX}" --add-modules javafx.controls,javafx.fxml FishSim
exit $?

