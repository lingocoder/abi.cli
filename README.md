# Application Binary Interface (ABI) Inspector

The ABI Inspector is a command line application that scans the API of a Java library to detect whether the library exposes types from its dependencies to the library consumers' runtime classpath. That's known as „*dependency leakage*“.

## Usage

*Requires JDK 11+*

Please, don't let the long-winded wall of text below intimidate you. The ABI Inspector is very simple to use. To have it scan your library's API, the following input must be provided to the tool:

    java -cp <your_library's_class_path...> com.lingocoder.abi.app.AbiApp -c <classes-dir>  -p <package-to-scan> -a <artifacts-gav-file> -d <dependencies-file>

1. The runtime dependencies required by your library. These must be included in this tool's classpath (*`-cp <your_library's_class_path...>`*)
2. The absolute path of the root directory of your library's classes (*`-c <classes-dir>`*). For example: `target/classes`
3. A list of packages of which your library's classes are members (*`-p <package-to-scan>`*) For example: `com.example.my.api`
4. The absolute path to a file that contains a list of Maven-style *`G:A:V`* module identifiers of your library's dependencies (*`-a <artifacts-gav-file>`*)
5. The absolute path to a file that contains a list of absolute paths to your library's dependencies (*`-d <dependencies-file>`*)

## What You Get

The ABI inspection results in output that looks like this:
    
    Project class com.lingocoder.poc.HttpClientWrapper has an ABI dependency on...
        |___ httpclient
    
    Project class com.lingocoder.poc.Track has an ABI dependency on...
        |___ jackson-annotations
    
    Project class com.lingocoder.poc.Frankenstein has an ABI dependency on...
        |___ commons-math
        |___ de.huxhorn.sulky.generics
        |___ bitcoinj-core-0.15
        |___ janerics
    
    Project class com.lingocoder.poc.ProjectClass$1 has an ABI dependency on...
        |___ lingocoder.core

## Helpful Tips

For smaller projects, it would be easy to assemble the required input by hand. But for larger projects, you could also leverage a dependency management tool like Ant, Maven or Gradle to automate the preparation of the input. They all provide some way to dump a class path. First, you would need to define your library's dependencies in the respective tool's build script. Here's a Gradle script example of dependencies defined for some library:

    ...
    dependencies {
        ...
        api 'org.apache.httpcomponents:httpclient:4.5.7'
    
        api group: 'com.fasterxml.jackson.core', name: 'jackson-annotations', version: '2.9.8'

        implementation 'org.apache.commons:commons-lang3:3.5'

        implementation 'com.lingocoder:lingocoder.core:0.4'

        testImplementation 'junit:junit:4.12'

        implementation group: 'org.bitcoinj', name: 'bitcoinj-core', version: '0.15-SNAPSHOT'

        api 'org.apache.commons:commons-math:2.2'

        implementation group: 'jp.dodododo.janerics', name: 'janerics', version: '1.0.1'

        implementation group: 'de.huxhorn.sulky', name: 'de.huxhorn.sulky.generics', version: '8.2.0'
        ...
    }
    ...

And here is a task that would print to standard out a listing of those dependencies that you should save to a file:

    ...
    task printRuntimeClasspath{ 
        doLast{
            println configurations.default.properties.files.each{c -> println c  }
        }
    }
    ...



Running that script will print each item on its own line; like this:



        
    > Task :printRuntimeClasspath

    ...
    
    /home/lingocoder/.gradle\caches\modules-2\files-2.1\org.apache.httpcomponents\httpclient\4.5.7\dda059f4908e1b548b7ba68d81a3b05897f27cb0\httpclient-4.5.7.jar
    /home/lingocoder/.gradle\caches\modules-2\files-2.1\com.fasterxml.jackson.core\jackson-annotations\2.9.8\ba7f0e6f8f1b28d251eeff2a5604bed34c53ff35\jackson-annotations-2.9.8.jar
    /home/lingocoder/.gradle\caches\modules-2\files-2.1\org.apache.commons\commons-math\2.2\4877b85d388275f994a5cfc7eceb73a8045d3006\commons-math-2.2.jar
    /home/lingocoder/.gradle\caches\modules-2\files-2.1\org.apache.commons\commons-lang3\3.5\6c6c702c89bfff3cd9e80b04d668c5e190d588c6\commons-lang3-3.5.jar
    /home/lingocoder/.gradle\caches\modules-2\files-2.1\com.lingocoder\lingocoder.core\0.4\6004ed8142c98a9b27d0b2cf44f4c92c8431450e\lingocoder.core-0.4.jar
    /home/lingocoder/.m2\repository\org\bitcoinj\bitcoinj-core\0.15-SNAPSHOT\bitcoinj-core-0.15-SNAPSHOT.jar
    /home/lingocoder/.gradle\caches\modules-2\files-2.1\jp.dodododo.janerics\janerics\1.0.1\bb363a3612c390cc7708b2f33a5133963a2ac486\janerics-1.0.1.jar
    /home/lingocoder/.gradle\caches\modules-2\files-2.1\de.huxhorn.sulky\de.huxhorn.sulky.generics\8.2.0\6821b3791399d14c3e03aaededa95069feca591\de.huxhorn.sulky.generics-8.2.0.jar
    ...

    BUILD SUCCESSFUL in 6s
    1 actionable task: 1 executed

You need to save that module location output to a file — omitting, the build tool's status messages. It's important to point out that you will, coincidentally, be passing a subset of those same artifacts in that list, as the classpath to the ABI executable. However, the file itself will not be used as any kind of classpath for the ABI executable. The above file that lists your library's dependencies will be read by the ABI tool as input data. It will be used by the tool to speed up its dependency analysis. 

The inspection process also requires the module's artifact identifiers (*the `G:A:V` coordinates*). This script:

    task printRtCpGAVs{ 
        doLast{
            println configurations.default.properties.allDependencies.each{c -> if ( c.name != 'unspecified' & c.hasProperty('module') ){ println c.module.toString() + ":" + c.version } } 
        }
    }


...produces this output:


    > Task :printRtCpGAVs
    org.apache.commons:commons-lang3:3.5
    com.lingocoder:lingocoder.core:0.4
    org.bitcoinj:bitcoinj-core:0.15-SNAPSHOT
    jp.dodododo.janerics:janerics:1.0.1
    de.huxhorn.sulky:de.huxhorn.sulky.generics:8.2.0
    ch.qos.logback:logback-core:1.3.0-alpha4
    ch.qos.logback:logback-classic:1.3.0-alpha4
    commons-cli:commons-cli:1.4
    org.apache.httpcomponents:httpclient:4.5.7
    com.fasterxml.jackson.core:jackson-annotations:2.9.8
    org.apache.commons:commons-math:2.2
    ...
        
    BUILD SUCCESSFUL in 4s
    1 actionable task: 1 executed

Save that to another file — minus the „`Task`“, „`[DefaultExternal...]`“ and „`BUILD SUCCESSFUL`“ status messages.

And now, with the list of your library's dependencies and the list of those dependencies' artifact identifiers both saved to a file, you can run an ABI inspection scan on your library's API with the command illustrated above.

## How To Silence The Logging

Create a file named, „*logback.xml*“. Paste the following configuration into it. Then put it in your classpath:
    
    <configuration>

      <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
          <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
      </appender>

      <root level="info">
        <appender-ref ref="STDOUT" />
      </root>
    </configuration>

## Watch This Space

A Gradle plugin with additional capabilities is in the works. Any and all questions or suggestions are welcomed. [*Please get in touch*](mail:to=coder@lingcoder.com).