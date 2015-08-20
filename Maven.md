# Introduction #

JavaCPP has been uploaded to the Central Repository at https://oss.sonatype.org/content/groups/public/com/googlecode/javacpp/ , but the artifacts are also hosted at http://maven2.javacpp.googlecode.com/git/ . To use the latter, we need to specify an additional repository on top of the dependency and settings of JavaCPP itself.

# Getting Started #

To use JavaCPP with a Maven project, add the following sections to the project's `pom.xml` file, where the `repository` element is not required if we plan to rely on the Central Repository:

```
<repositories>
  <repository>
    <id>javacpp</id>
    <name>JavaCPP</name>
    <url>http://maven2.javacpp.googlecode.com/git/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.googlecode.javacpp</groupId>
    <artifactId>javacpp</artifactId>
    <version>0.4</version>
  </dependency>
</dependencies>
```

After adding this (and letting Maven download), we should be able to access [JavaCPP classes and annotations](http://javadoc.javacpp.googlecode.com/git/index.html).


# Running JavaCPP at Build Time #

To build the native bindings, we also need to execute JavaCPP from within the Maven lifecycle, This can be accomplished by using the Maven dependency and exec plugins, or alternatively with [the built-in mojo plugin of JavaCPP](http://javadoc.javacpp.googlecode.com/git/com/googlecode/javacpp/BuildMojo.html).

To use the former set of plugins, we can add the following section to the `pom.xml` file:

```
<plugins>
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>2.3</version>
    <executions>
      <execution>
        <goals>
          <goal>properties</goal>
        </goals>
      </execution>
    </executions>
  </plugin>
  <plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>1.2.1</version>
  </plugin>
</plugins>
```

Further, JavaCPP can be invoked by adding an execution block under the exec plugin that looks something like this:

```
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>1.2.1</version>
  <executions>
    <execution>
      <id>javacpp</id>
      <phase>process-classes</phase>
      <goals>
        <goal>exec</goal>
      </goals>
      <configuration>
        <executable>java</executable>
        <arguments>
          <argument>-jar</argument>       <argument>${com.googlecode.javacpp:javacpp:jar}</argument>
          <argument>-classpath</argument> <argument>${project.build.outputDirectory}</argument>
          <argument>-d</argument>         <argument>${project.build.outputDirectory}/lib/</argument>
        </arguments>
      </configuration>
    </execution>
  </executions>
</plugin>
```

We might also need to reference the appropriate properties file for the target platform. To compile for something like Android, we would add the following:

```
<argument>-properties</argument> <argument>android-arm</argument>
```

Of course, depending on what we are compiling with, we might have to tweak some of the default properties of JavaCPP. For example, in the case of Android, we would have to give the root of the NDK to `platform.root`. This can be done by adding something like the following under the arguments:

```
<argument>-Dplatform.root=${env.ANDROID_NDK_HOME}</argument>
```

Assuming that the environment variable ANDROID\_NDK\_HOME points in the right direction.