# Introduction #

Javacpp excels at making native C++ code available to Java via JNI without requiring the developer to type the massive amount of repetitive overhead code demanded of jni implementations. Originally the goal was to provide Java developers with a quick way to call existing native libraries. However, javacpp also provides developers writing custom C++ with a fast and powerful way to expose their C++ to the Java world, and to call Java methods from C++.

jace http://code.google.com/p/jace/ is a more general way to link native code and Java. However, I have found that javacpp can do this quickly with less programmer study for simple cases.

# Component Overview #
  * **Java Interface Class.** Javacpp provides support for quickly creating the Java class that will load the native library and provide access to native methods in that library. You must include javacpp.jar in the Java build path, and point javacpp.jar to the native shared library that will be compiled using javacpp.
  * **JVM Access From C++** Javacpp provides a way to expose the JVM so that the C++ can call Java methods.
  * **JNI Code Generation** Javacpp provides a C++ wrapper code generator and linker to create the native library starting from custom code that is referenced through #include statements. This must be set up carefully to ensure that all the custom C++ source gets compiled in the same pass and that the resultant library is placed in the proper location so it can be found and loaded at runtime.

# Suggested Implementation Method #

Whenever two independent sets of code must interact it becomes very difficult to test if each set is not proven independently. In addition to the typical bugs and issues associated with normal Java and C++ development, JNI adds additional complexity with the need to manage the JVM in the C++ world and the need to properly load the native library in the Java world.

The following steps make JNI development as straightforward as possible:

  * Restrict the number of interface classes that talk to native code on the Java side and which talk to Java on the C++ side. One for each side is the ideal number.
  * Never forget that Java is a multi-threaded environment. The JNI spec clearly states that native code must be thread-safe.
  * Write the Java and C++ interface code skeletons as standalone code, i.e. pure Java and C++ respectively. Insert print statements and run the skeletons separately to make sure the "pure" code works as intended. **Make sure the C++ uses a namespace, this will make life much easier when trying to get javacpp to function.** This work is best done in an IDE unless you never use an IDE.
  * Add the javacpp code to the Java class that will talk to the native c++.
  * Include the javacpp.jar file in the Java build path in the IDE. Define the target directory for the native shared library. Run javacpp to compile and link the shared library. Note that the IDE is not used to compile the library. Javacpp generates a C++ source file which includes your C++ source files and adds the jni calls to the methods defined in the Java interface code you have written, then compiles and links using command-line calls to the development tools (gcc or whatever).
  * Verify that the C++ skeleton code now works when the Java side invokes code on the C++ side.
  * To call Java from C++, add a reference to the JVM in the C++ and use standard JNI calls to obtain references to the Java interface class and methods. **Use #defines in such a way as to not break the "pure" c++ code.** You will be switching back and forth a lot as the C++ is developed. Test the Java skeleton from C++ to make sure that the calls work as expected.
  * Flesh out the application, iterating the above procedure as often as necessary to avoid getting overwhelmed with interacting bugs.