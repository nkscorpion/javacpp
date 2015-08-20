# Introduction #

This page explores a way to automate the build of a C++ system that spans multiple source files and which requires linking against system libraries not called for by default. If you are looking for a simple implementation with one file and no special OS libraries, the example on the main page is the way to go.

# Build Script #
A simple way to package all the options and setup needed to compile the shared library  is to use a script. This script must be run whenever either of these events occurs:
  * A native method is added, removed or changed from in the Java interface class.
  * Any C++ code changes.

The following example below is for linux/unix. It depends only on class StuffInter.java having @Platform(include "") and
an optional @Namespace() directives like this:
```
@Platform(include="javacpp-code.h") // References all C++ code to create library
@Namespace("cprg") // Namespace where all c++ code must reside
```

In this example, file **javacpp-code.h** is used only to define c++ files for the javacpp compilation, it is not used when the C++ is independently compiled for testing. In this project the include file appears as:
```
// All the C++ sources in the system.
// Referenced in StuffInter.java by @platform attribute.
// Used by javacpp to compile the shared native library used by jni

#include "hmi.cpp"
#include "sender.cpp"
#include "logger.cpp"
#include "receiver.cpp"
#include "scenario.cpp"
#include "udp.cpp"
```
The script below references all the include directories and defines the classpath.
Important points:
  * The output .so file will appear in a platform subdirectory of the directory in which the compiled tps/TPSInter.class file appears. In the case of Eclipse this is the bin directory, which must be spelled out in the classpath (-cp option).
  * javacpp.jar automatically picks up the $JAVA\_HOME/include and $JAVA\_HOME/include/linux as include paths. These are necessary for JNI compilation.
  * Note that since javacpp-code.h references source files which in turn reference internal headers, an -I option is required for the include and the source directories in the native application C++ tree.
```
#!/bin/bash
# Make sure the environment is sane
if  [ ! -d "$JAVA_HOME" ]  ; then
	echo Environment variable JAVA_HOME does not point to a directory, exiting.
	exit 1
fi

SWLIB=../SW_Sim/lib

# Generate the C++ jni code and the shared library
# Note that the intermediate c++ file is not saved, 
# If you need to see the intermediate c++ file then include javacpp option
# -nocompile which will neither compile nor delete the generated c++ file.
#
# Note that the default output directory is the bin directory...
${JAVA_HOME}/bin/java -jar $SWLIB/javacpp.jar \
-cp ../SW_Sim/bin \
 -Xcompiler -I../tps/src \
 -Xcompiler -I../tps/inc \
 -Xcompiler -lpthread \
 -Xcompiler -lssl \
 tps/StuffInter
```