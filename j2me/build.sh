#!/bin/sh
# Rebuild Blocks.jar from source. Needs a MIDP 2.0 / CLDC 1.1 SDK (WTK, NetBeans Mobility)
# and a JDK that supports -source 1.3 -target 1.3 (or use 1.4 / 1.5 with a newer one).
# Usage: MIDP=/path/midp.jar CLDC=/path/cldc.jar ./build.sh
set -e
cd "$(dirname "$0")"
rm -rf build && mkdir -p build/classes
javac -source 1.3 -target 1.3 -bootclasspath "$MIDP:$CLDC" -d build/classes BlocksCanvas.java BlocksMIDlet.java
cp -r textures lang.txt build/classes/
jar cfm Blocks-new.jar MANIFEST.MF -C build/classes .
echo "Built j2me/Blocks-new.jar"
