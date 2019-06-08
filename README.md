# Application Binary Interface Inspector (abi.cli)

The ABI Inspector (*abi.cli*) is a console application that scans the API of a Java library to detect whether the library exposes types from its dependencies to the library consumer's compilation-time classpath. That's known as „*dependency leakage*“.

The inspection will by default report a simple summary of what it calls the *ABI dependencies* it finds for each of your library's classes. Optionally (*given -v*), it will also report finer-grained details about your library's dependencies. Details like:

 * The supertypes your library's classes extend
 * The types your library exposes through its public method and constructor signatures
   - exceptions
   - parameters
   - return types

## Usage

*Requires JDK 11+*

I admit this README is sorta on the *-v* side. But please don't let that intimidate you. The tool is actually pretty easy to use.

## Run ABI Using The JarExec Gradle Plugin

Although abi.cli is a standalone Java application, it might be more convenient to use it within a Gradle build script. A Gradle plugin is planned. But in the meantime, [*I have just the plugin*](http://bit.ly/JarExecPi) that would allow you to do an ABI scan of your library's API from within a Gradle build; today.

General steps for using the JarExec plugin are [*here*](http://bit.ly/JarExecSteps). But to set it up specifically to run abi.cli, [*it needs certain input*](#abi-command-options). You need to:

1. Add [*`'com.lingocoder:abi.cli:n.n.n'`*](http://bit.ly/abiCLImvn) as a dependency in your build script
2. Set the options that abi.cli requires as the value of the *`args`* property of the JarExec task
3. Set *`com.lingocoder.abi.app.AbiApp`* as the value of the *`mainClass`* property of the JarExec task
4. Set the value of the *`classpath`* property of the JarExec task to the Gradle build configuration that contains the abi.cli dependency. The *`classpath`* property also needs to include any other runtime dependencies required by your library.<sup>*1*</sup>


        plugins{
            id 'java-library'
            id 'com.lingocoder.jarexec' version '0.5.1'
        }
        ...
        def abiClassesDir = file("L:/ingocoder/classes/")

        dependencies{

            ...

            implementation group: 'com.lingocoder', name: 'abi.cli', version: '0.5.1'
 
            implementation files(abiClassesDir.absolutePath)
            ...
        }
        ...    
        jarexec{
    
            /* (i) A list of arguments and options the main class needs */

            def abiGavInput = file("L:\\ingocoder\\input\\files\\abi.gav.coordinates.1.txt")

            args = [
                "-a", abiGavInput.absolutePath,
                "-c", abiClassesDir.absolutePath, 
                "-p", "com.lingocoder.poc", "net", "org.springframework", "-v"]

            /* (ii) The class path the main class needs. This is configurable by adding what your main
             * class requires (including directories) to whatever configuration that works for you */

            classpath = configurations.default

            /* (iii) Configure jarexec's 'jar' property with the abi.cli jar. */

            jar = jarhelper.fetch('com.lingocoder:abi.cli:0.5.1').orElse('build/libs/abi.cli-0.0.0.jar')

            /* (iv) Configure jarexec's mainClass property as the abi.cli application. */

            mainClass = 'com.lingocoder.abi.app.AbiApp'

            /* (v) Tell Gradle to watch for changes to the input file. You leverage
             * the incremental build feature this way. */

            watchInFile = abiGavInput

            /* (vi) Configure jarexec's 'watchOutDir' property with an output directory 
             * to make the task build-cacheable. */

            watchOutDir = abiClassesDir
        }

        ...


## What You Get

Running the above JarExec task as *`:execjar`* will get you this report to stdout:

        ...
        > Task :execjar
        Build cache key for task ':execjar' is 704557cc19a9ef9e6f6acbff333c4387
        Task ':execjar' is not up-to-date because:
          Input property 'watchInFile' file L:\ingocoder\input\files\abi.gav.coordinates.1.txt has been added.
        ...
        >--: ABI dependencies found for...        
        |   |__ class : com.lingocoder.poc.Frankenstein
        |   |__ supertype : org.bitcoinj.wallet.CoinSelection
        |   |__ constructor : com.lingocoder.poc.Frankenstein
        |     |__ param : org.bitcoinj.core.Coin
        |   |__ method : m
        |     |__ exception : jp.dodododo.janerics.exception.UnsupportedTypeException
        |     |__ param : org.apache.commons.math.random.EmpiricalDistribution
        |     |__ return : de.huxhorn.sulky.generics.Wrapper
        |
        |
        |_________ Dependencies ________
        |
        |* de.huxhorn.sulky:de.huxhorn.sulky.generics:8.2.0
        |* jp.dodododo.janerics:janerics:1.0.1
        |* org.apache.commons:commons-math:2.2
        |* org.bitcoinj:bitcoinj-core:0.15-SNAPSHOT
        |_______________________________
        
        >--: ABI dependencies found for...
        |__ class : com.lingocoder.poc.HttpClientWrapper
        |   |__ constructor : com.lingocoder.poc.HttpClientWrapper
        |     |__ param : org.apache.http.client.HttpClient
        |
        |
        |_________ Dependencies ________
        |
        |* org.apache.httpcomponents:httpclient:4.5.3
        |_______________________________               
        ...

        BUILD SUCCESSFUL in...

## ABI Command Options

Originally, abi.cli was designed to work as a standalone console application. It provides the following command line interface (*CLI*):

        usage:

        java -cp {your_project_classpath} com.lingocoder.abi.app.AbiApp -a
               <artifacts-gav-file> | -g <gav-coordinates> -c <classes-dir> 
               [-help] -p <package-to-scan>
         -a <artifacts-gav-file>   In place of the <gav-coordinates> option, you
                                   could alternatively provide a new
                                   line-separated file containing Maven-style
                                   G:A:V dependency coordinates {e.g.
                                   com.lingocoder:abinspector:0.4}
         -c <classes-dir>          The parent directory that contains the classes
                                   of the specified package {e.g. 'build/classes'}
         -g <gav-coordinates>      A space-delimited sequence of Maven-style G:A:V
                                   dependency coordinates {e.g.
                                   org.example:my-api:10.18[,eg.foo.wow:anartifact
                                   id:v8][,...]}
         -help                     Print this message
         -p <packages-to-scan>     A space-delimited sequence of packages in which
                                   project classes are contained {e.g.
                                   com.example.mypackage net.foo.another.pkg ...}
         -v                        Show the names of classes, supertypes,
                                   constructors, methods, parameters, return types
                                   and annotations that are exposed in the
                                   analyzed API                                   
                                   
Those options correspond to the properties used in [*the above JarExec plugin usage example*](#run-abi-using-the-jarexec-gradle-plugin).

To scan your library's API from the command line, the same input provided in the plugin example must also be provided to the CLI:

    java -cp <your_library's_class_path> com.lingocoder.abi.app.AbiApp -c <classes-dir>  -p <packages-to-scan> -a <artifacts-gav-file> -v

1. The runtime dependencies required by your library.<sup>*1*</sup> These must be included in this tool's classpath (*`-cp <your_library's_class_path...>`*). Of course, the [*`com.lingocoder:abi.cli:n.n.n`*](http://bit.ly/abiCLImvn) artifact itself must also be in the classpath
2. The absolute path of the root directory of your library's classes (*`-c <classes-dir>`*). For example: `target/classes`
3. A list of packages of which your library's classes are members (*`-p <packages-to-scan>`*) For example: `com.example.my.api org.acme edu`
4. The absolute path to a file that contains a list of Maven-style *`G:A:V`* module identifiers of your library's dependencies (*`-a <artifacts-gav-file>`*)

The output when ran through the CLI is the same as that shown for [*the plugin example above*](#run-abi-using-the-jarexec-gradle-plugin).

## Helpful Tips On Providing The Required Input

The ABI inspection process requires that you provide [*specific information about your library's dependencies*](#abi-command-options) as input to abi.cli.<sup>*1*</sup> For smaller projects, it would be easy to assemble the required input by hand. But for larger projects, you could also leverage a dependency management tool like Ant, Maven or Gradle to automate the preparation of the input. They all provide some way to dump info on your project's dependencies. First, you would need to define your library's dependencies in the respective tool's build script. Here's a Gradle script example of module identifiers defining the dependencies required by some library:

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

The inspection process also requires those same module identifiers (*a.k.a, the `G:A:V` coordinates*). This script:

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

Save that to a file — minus the „`Task`“, „`[DefaultExternal...]`“ and „`BUILD SUCCESSFUL`“ status messages.

And now, with that generated list of artifact identifiers saved to a file, you can run an ABI inspection scan on your library's API with the command illustrated above.

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

A Gradle plugin with additional capabilities is planned. You are welcomed to [*PM me through the Gradle Forums*](http://bit.ly/LogIn2MsgMe) with any and all questions or suggestions.

\_____

<sup><sup><sup>1</sup> *Your library's dependencies that were specified with a „`for compilation only`“ scope in the build script of your library, must also be included in abi.cli's runtime classpath*</sup></sup>