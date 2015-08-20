# Introduction #

A Java class or classes must be written to load the native C++ shared library and expose methods to the Java system. It is recommended that one class be written to handle native code interactions, unless there is so much content that it would make the class unwieldy.

The introduction page shows the simple case of a single header file containing a single C++ class from which methods are exposed to the Java. Things get more complex when it is desired to connect a more realistic C++ project containing multiple files with Java.

# Important Components #

  * **Imports** . Javacpp references are required to generate helper Java classes.
  * **Platform Options** . See the javacpp source file javacpp/src/main/java/com/googlecode/javacpp/annotation/Platform.java for details as to what platform directives exist. A simple system only needs the include and namespace platform directives. If a build script is used, the same is true of more complex C++ implementations. Avoid the temptation of using platform directives in Java to characterize C++ system. Keeping them separate decreases the possibility of hard-to-trace build problems.
  * **Interface Class** . An interface class is written to define the available native methods.

# Example #
If using a build script, a complex system may be set up with a single Java interface class using only include and namespace platform directives, just like the example on the main page. See the [BuildScript](BuildScript.md) page.