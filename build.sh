#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p build/classes dist
find src/main/java -type f -name '*.java' -print0 | xargs -0 javac --release 17 -encoding UTF-8 -d build/classes
jar --create --file dist/manajemen-restoran.jar --main-class id.ac.restoran.Main -C build/classes .
echo 'Build selesai: dist/manajemen-restoran.jar'
