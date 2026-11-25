#!/bin/bash
# Lumina-Prüfung ohne Android Studio (siehe README.md in diesem Ordner).
#  Durchgang A: ganze App mit dem Compose-Compiler-Plugin (prüft u. a. @Composable-Aufrufe). Danach bricht der
#               Compiler absichtlich ab (keine echte Compose-Runtime), das ist erwartet.
#  Durchgang B: ganze App ohne Plugin bis zum fertigen Bytecode (prüft JVM-Signaturen, z. B. "Platform declaration clash").
# Aufruf: check.sh [A|B]  (ohne Angabe: beide)
CHK=${CHK:-$(cd "$(dirname "$0")" && pwd)}
PROJ=${PROJ:-$(cd "$CHK/../.." && pwd)}
KC=${KC:-/home/claude/tools/kotlinc}
PLUGIN=${PLUGIN:-$CHK/build/compose-compiler-cli.jar}
export PATH=$KC/bin:$PATH
export JAVA_OPTS="-Xmx5g -Xss8m"
mkdir -p $CHK/build/gen
python3 $CHK/genR.py $PROJ/app/src/main/res $CHK/build/gen/R.kt >/dev/null
SRC=$(find $PROJ/app/src/main/java -name "*.kt")
CP=$CHK/build/android-stubs.jar:$CHK/build/compose-stubs.jar:$KC/lib/kotlinx-coroutines-core-jvm.jar
run() {  # $1 = Name, $2 = Zusatzoptionen
  rm -rf /tmp/lumina_out && mkdir -p /tmp/lumina_out
  kotlinc -jvm-target 17 -nowarn $2 -cp $CP -d /tmp/lumina_out $CHK/build/gen/R.kt $SRC > /tmp/lumina_check_$1.log 2>&1
  grep -v "^Picked up" /tmp/lumina_check_$1.log | grep -E ": error:|^error:" > /tmp/lumina_errors_$1.txt
  local crash=$(grep "^exception:" /tmp/lumina_check_$1.log | grep -v "IncompatibleComposeRuntimeVersionException")
  if [ -n "$crash" ]; then echo "$crash" >> /tmp/lumina_errors_$1.txt; fi
  echo "Durchgang $1: $(wc -l < /tmp/lumina_errors_$1.txt) Fehler"
}
MODE=${1:-AB}
: > /tmp/lumina_errors.txt
if [[ "$MODE" == *A* ]]; then run A "-Xplugin=$PLUGIN"; cat /tmp/lumina_errors_A.txt >> /tmp/lumina_errors.txt; fi
if [[ "$MODE" == *B* ]] && [ ! -s /tmp/lumina_errors.txt ]; then run B ""; cat /tmp/lumina_errors_B.txt >> /tmp/lumina_errors.txt; fi
echo "ERRORS: $(wc -l < /tmp/lumina_errors.txt)"
sed 's#'$PROJ'/app/src/main/java/de/funke/app/##' /tmp/lumina_errors.txt | head -40
