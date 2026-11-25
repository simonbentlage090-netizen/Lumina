#!/bin/bash
# Baut die Stubs (android-stubs.jar aus Java, compose-stubs.jar aus Kotlin) und das umgebaute Compose-Plugin nach build/.
set -e
CHK=${CHK:-$(cd "$(dirname "$0")" && pwd)}
KC=${KC:-/home/claude/tools/kotlinc}
export PATH=$KC/bin:$PATH
cd $CHK
rm -rf build/java_src build/java_out build/kt_src && mkdir -p build/java_src build/java_out build/kt_src
python3 split.py android_bundle.java build/java_src
javac -nowarn -d build/java_out $(find build/java_src -name "*.java") 2>&1 | grep -v "^Picked up" || true
(cd build/java_out && jar cf ../android-stubs.jar .)
for b in kt_bundle1.kt kt_bundle2.kt kt_bundle3.kt kt_bundle4.kt; do python3 split.py $b build/kt_src; done
kotlinc -nowarn -jvm-target 17 -cp build/android-stubs.jar:$KC/lib/kotlinx-coroutines-core-jvm.jar -d build/compose-stubs.jar $(find build/kt_src -name "*.kt") 2>&1 | grep -v "^Picked up\|^warning" || true
[ -f build/compose-compiler-cli.jar ] || python3 relocate.py $KC/lib/compose-compiler.jar build/compose-compiler-cli.jar
ls -la build/android-stubs.jar build/compose-stubs.jar build/compose-compiler-cli.jar
