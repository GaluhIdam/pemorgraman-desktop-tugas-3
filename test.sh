#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
./build.sh
mkdir -p build/test-classes
find src/test/java -type f -name '*.java' -print0 | xargs -0 javac --release 17 -encoding UTF-8 -cp build/classes -d build/test-classes
java -ea -cp build/classes:build/test-classes id.ac.restoran.RestaurantTest
