# Introduction #

This page contains some examples of javacpp usage.

# Quick start: Wrapping simple C++ class #

For example, we have c++ library named **foo**, i.e. binary named **libfoo.so** in Linux or **libfoo.dylib** in Mac OS X or **foo.dll** in Windows, that contains some classes including **Abc**. Header file **Abc.hpp**:
```
class Abc {
public:
    void testMethod(int a);
};
```

To use it from Java we need Java wrapper class for each class that we want to use. javacpp also uses this Java class to generate JNI wrapper library. For example for class **Abc.java**:
```
import com.googlecode.javacpp.*;
import com.googlecode.javacpp.annotation.*;

@Platform(include = "Abc.hpp", link = "foo")
// "link" tells javacpp which original library should be linked (if not specified, "Abc" will be used)
public class Abc extends Pointer {
    static {
        Loader.loadLibrary("jnifoo");  // Specify name of our wrapper library
        //Loader.load();  // This method can be used if JNI wrapper named as class, i.e. "jniAbc"
    }
    
    public Abc() {
        allocate();  // To be able to create Abc instances from Java we should wrap allocator
    }
    
    public native void allocate();  // Define allocator

    public native void testMethod(int a);  // Method we want to use

    
    public static void main(String[] args) {
        Abc abc = new Abc();
        abc.testMethod(123);
    }
}
```

Now we should compile Abc.java:
```
$ javac -cp javacpp Abc.java
```

And generate JNI wrapper (assume that all headers and libraries located in current directory):
```
$ java -jar javacpp.jar -d . -o jnifoo -Xcompiler -L. Abc
```

**-d** option can be used to specify output directory, **-o** - resulting JNI library name. Using **-Xcompiler** we can pass options to compiler, e.g. **-L.** - to search libraries in current path.

Run it:
```
$ java -cp javacpp.jar:. Abc
testMethod: 123
```

# String parameters and result #

String parameters are also supported gracefully. Methods:
```
    void testStringMethod(std::string s);
    void testCharMethod(const char* s);
```

Can be wrapped with:
```
    public native void testStringMethod(String s);
    public native void testCharMethod(String s);
```