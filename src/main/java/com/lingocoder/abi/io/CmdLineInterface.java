/**
 * A standalone suite of libraries to be consumed by disparate applications.
 *
 * Copyright (C) 2019 lingocoder <plugins@lingocoder.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.lingocoder.abi.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdLineInterface {

    private final static Logger LOG = LoggerFactory
            .getLogger( "CmdLineInterface" );

    private static Optional<Set<String>> dependencies;

    public static String[ ] parsedArgs;

    public static String[ ] parseArgs( String... args ) {

        List<String> cliArgsList = new ArrayList<>( );

        // create Options object
        Options options = new Options( );

        OptionGroup gavOptGrp = new OptionGroup( );

        options.addOption( "help", "Print this message" );
        Option pkg = Option.builder( "p" ).argName( "packages-to-scan" )
                .hasArgs( )
                .desc( "A space-delimited sequence of packages in which project classes are contained {e.g. com.example.mypackage net.foo.another.pkg ...}" )
                .required( ).build( );

        Option classes = Option.builder( "c" ).argName( "classes-dir" )
                .hasArg( )
                .desc( "The parent directory that contains the classes of the specified package {e.g. 'build/classes'}" )
                .required( ).build( );

        Option gav = Option.builder( "g" ).argName( "gav-coordinates" )
                .hasArgs( )
                .desc( "A space-delimited sequence of Maven-style G:A:V dependency coordinates {e.g. org.example:my-api:10.18[,eg.foo.wow:anartifactid:v8][,...]}" )
                ./* required(). optionalArg( true ).*/build( );

        Option gavFile = Option.builder( "a" ).argName( "artifacts-gav-file" )
                .hasArg( )
                .desc( "In place of the <gav-coordinates> option, you could alternatively provide a new line-separated file containing Maven-style G:A:V dependency coordinates {e.g. com.lingocoder:abinspector:0.4}" )
                .build( );

        Option dependenciesFile = Option.builder( "d" )
                .argName( "dependencies-file" ).hasArg( )
                .desc( "To improve the processing speed of ABI inspection, provide file a list of your project's dependencies in a new line-separated file containing the absolute file system paths to a project's dependencies {e.g. /home/james/.m2/repository/org/example/my-api/10.18/my-api-10.18.jar}" )
                .build( );

        gavOptGrp.addOption( gav );
        gavOptGrp.addOption( gavFile );
        gavOptGrp.setRequired( true );
        options.addOption( classes );
        options.addOption( pkg );
        options.addOption( dependenciesFile );
        options.addOptionGroup( gavOptGrp )/* .addOption( gav ) */;

        CommandLineParser parser = new DefaultParser( );

        CommandLine cmd = null;

        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter( );

        String usage = "\n\njava -cp {your_project_classpath} com.lingocoder.abi.app.AbiApp";

        try {
            cmd = parser.parse( options, args );
        } catch ( ParseException e ) {
            LOG.error( e.getMessage( ), e );
            formatter.printHelp( usage, options, true );
        }

        if ( cmd.hasOption( "c" ) ) {
            cliArgsList.add( 0, cmd.getOptionValue( "c" ) );

        } else {

        }
        if ( cmd.hasOption( "p" ) ) {

            cliArgsList.add( 1, cmd.getOptionValue( "p" ) );
        } else {

            formatter.printHelp( usage, options, true );
        }
        String gSel = gavOptGrp.getSelected( );
        if ( gSel.equals( "a" ) ) {

            cliArgsList.addAll( 2,
                    toSet( Paths.get( cmd.getOptionValue( gSel ) ) ) );

        } else {
            cliArgsList.addAll( 2,
                    Arrays.asList( cmd.getOptionValues( gSel ) ) );

        }

        if ( cmd.hasOption( "d" ) ) {
            CmdLineInterface.dependencies = Optional
                    .of( toSet( Paths.get( cmd.getOptionValue( "d" ) ) ) );
        } else {
            CmdLineInterface.dependencies = Optional.empty( );
        }

        return CmdLineInterface.parsedArgs = cliArgsList
                .toArray( new String[ 0 ] );
    }

    public static Optional<Set<String>> dependenciesFile( ) {
        return CmdLineInterface.dependencies;
    }

    protected static Set<String> toSet( Path aFile ) {

        Set<String> lines = Set.of( );

        try {

            lines = Files.lines( aFile ).parallel( ).map( f -> {
                return f.strip( );
            } ).collect( Collectors.toSet( ) );
        } catch ( IOException e ) {

            LOG.error( e.getMessage( ), e );
        }
        return lines;
    }
}