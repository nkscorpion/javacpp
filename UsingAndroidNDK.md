# Introduction #
JavaCPP already supports Android NDK with command option 'properties'.
But some people may want to use more Android-way. This document explains how to use javacpp for cpp-generation and android-ndk for compilation with/without maven.

# Configuration #
  * Android NDK r8d or above. (http://developer.android.com/tools/sdk/ndk/index.html) r8c is a bit buggy.
  * JavaCPP 0.4 or above.
  * Proper javacpp annotation in java source.
  * Use @Platform(library="XXX") to decide cpp&so name which will be used in Android.mk
```
@Platform(library = "jniHello", include = { "hello.h"})
```
  * jni/Android.mk
```
include $(CLEAR_VARS)
LOCAL_MODULE := jniHello
LOCAL_SRC_FILES := jniHello.cpp
LOCAL_C_INCLUDES := hello.h
LOCAL_LDLIBS := -llog
LOCAL_CPP_FEATURES := exceptions
```
  * jni/Application.mk
```
APP_ABI := armeabi armeabi-v7a x86 # or simply 'all' then it will generate armeabi, armeabi-v7a, x86, mips
APP_STL := gnustl_static
```

# Build without Maven #
  1. Run javacpp
```
C:\> java -jar libs\javacpp.jar -cp bin\classes -cp 'path-to-android.jar' -d jni -nocompile com.hello.package.*
```
    * -cp 'path-to-android.jar' is only required if static {} code in java classes has Android code such as android.util.Log...
    * -d jni, -nocompile is required so that JavaCPP generates cpp files in jni directory and ndk-build compiles them.
> 2. Run ndk-build
```
C:\> ndk-build
```
    * so files will be generated under libs\hardware\_platform such as libs\armeabi, libs\x86

# Build with Maven #
> Assume default android-related plugin is already set in pom.xml.
  * Add javacpp repository in `<repositories>`.
```
<repository>
  <id>javacpp</id>
  <name>JavaCPP</name>
  <url>http://maven2.javacpp.googlecode.com/git/</url>
</repository>
```
  * Add javacpp dependency in `<dependencies>`.
```
<dependency>
  <groupId>com.googlecode.javacpp</groupId>
 <artifactId>javacpp</artifactId>
 <version>0.4</version>
</dependency>
```
  * Use latest android-maven-plugin. Old version cannot properly recognize ndk-build target. (I tested with 3.4.0 and it's ok.)
  * To use the property ${com.googlecode.javacpp:javacpp:jar}, add 'properties' goal in `<build>`.
```
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-dependency-plugin</artifactId>
  <version>2.5.1</version>
  <executions>
    <execution>
      <goals>
        <goal>properties</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```
  * To generate cpp files, add 'exec' goal with javacpp in `<build>`.
```
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>1.2.1</version>
  <executions>
    <execution>
      <id>javacpp_generate</id>
      <phase>process-classes</phase>
      <goals>
        <goal>exec</goal>
      </goals>
      <configuration>
        <executable>java</executable>
        <arguments>
          <argument>-jar</argument>  <argument>${com.googlecode.javacpp:javacpp:jar}</argument>
          <argument>-cp</argument>   <argument>${project.build.outputDirectory}</argument>
          <argument>-d</argument>    <argument>${project.basedir}/jni</argument>
          <argument>-nocompile</argument>
          <argument>com.package.hello.*</argument>
        </arguments>
      </configuration>
    </execution>
  </executions>
</plugin>
```
  * To compile cpp files with ndk-build, add 'ndk-build' goal in `<build>`.
```
<plugin>
  <groupId>com.jayway.maven.plugins.android.generation2</groupId>
  <artifactId>android-maven-plugin</artifactId>
  <executions>
    <execution>
      <id>javacpp_generate</id>
      <phase>process-classes</phase>
      <goals>
        <goal>ndk-build</goal>
      </goals>
      <configuration>
        <target>all</target>
      </configuration>
    </execution>
  </executions>
</plugin>
```
  * To clean generated files, add 'clean' configuration in `<build>`.
```
<plugin>
  <artifactId>maven-clean-plugin</artifactId>
  <version>2.4.1</version>
  <configuration>
    <filesets>
      <fileset>
        <directory>libs</directory>
        <excludes>
          <exclude>*.jar</exclude>
        </excludes>
      </fileset>
      <fileset>
        <directory>obj</directory>
      </fileset>
      <fileset>
        <directory>jni</directory>
        <includes>
          <include>*.cpp</include>
        </includes>
      </fileset>
    </filesets>
  </configuration>
</plugin>
```