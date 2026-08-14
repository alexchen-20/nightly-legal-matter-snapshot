#!/usr/bin/env sh
set -eu
rm -rf out
mkdir -p out
javac -d out $(find src/main/java src/test/java -name '*.java')
java -cp out com.example.legal.LegalSnapshotServiceTest

if [ -n "${INFRAI_API_KEY:-}" ]; then
  echo "INFRAI_API_KEY is set; call LegalSnapshotService.runNightly from your scheduler."
else
  echo "Set INFRAI_API_KEY to run the storage step."
fi
