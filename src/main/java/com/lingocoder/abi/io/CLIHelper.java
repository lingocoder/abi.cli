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

public class CLIHelper {

    private static final Logger LOG = LoggerFactory.getLogger( "CLIHelper" );

    // automatically generate the help statement
    protected static final HelpFormatter formatter = new HelpFormatter( );

    protected static final String usage = "\n\njava -cp {your_project_classpath} com.lingocoder.abi.app.AbiApp";

    protected static CommandLine toCommandLine( String[ ] args, Options options, OptionGroup gavOptGrp ) {
        
        CommandLine cmd = null;

        options.addOption( "help", "Print this message" );

        Option pkgs = Option.builder( "p" ).argName( "packages-to-scan" )
                .hasArgs( )
                .desc( "A space-delimited sequence of packages in which project classes are contained {e.g. com.example.mypackage net.foo.another.pkg ...}" )
                .required( ).build( );

        Option classes = Option.builder( "c" ).argName( "classes-dir" )
                .hasArg( )
                .desc( "The parent directory that contains the classes of the specified package {e.g. 'build/classes'}" )
                .required( ).build( );

        Option gavs = Option.builder( "g" ).argName( "gav-coordinates" )
                .hasArgs( )
                .desc( "A space-delimited sequence of Maven-style G:A:V dependency coordinates {e.g. org.example:my-api:10.18[,eg.foo.wow:anartifactid:v8][,...]}" )
                ./* required(). optionalArg( true ).*/build( );

        Option gavsFile = Option.builder( "a" ).argName( "artifacts-gav-file" )
                .hasArg( )
                .desc( "In place of the <gav-coordinates> option, you could alternatively provide a new line-separated file containing Maven-style G:A:V dependency coordinates {e.g. com.lingocoder:abinspector:0.4}" )
                .build( );
/* 
        Option dependenciesFile = Option.builder( "d" )
                .argName( "dependencies-file" ).hasArg( )
                .desc( "To improve the processing speed of ABI inspection, provide file a list of your project's dependencies in a new line-separated file containing the absolute file system paths to a project's dependencies {e.g. /home/james/.m2/repository/org/example/my-api/10.18/my-api-10.18.jar}" )
                .build( );
 */
        Option verbose = Option.builder( "v" )
                .argName( "verbose" ).hasArg( false )
                .desc( "Show the names of classes, supertypes, constructors, methods, parameters, return types and annotations that are exposed in the analyzed API" )
                .build( );

        gavOptGrp.addOption( gavs );

        gavOptGrp.addOption( gavsFile );

        gavOptGrp.setRequired( true );

        options.addOption( classes );

        options.addOption( pkgs );
/*         options.addOption( dependenciesFile ); */
        options.addOptionGroup( gavOptGrp )/* .addOption( gav ) */;

        options.addOption( verbose );
        
        CommandLineParser parser = new DefaultParser( );

        try {

            cmd = parser.parse( options, args );

        } catch ( ParseException e ) {

            LOG.error( e.getMessage( ), e );

            formatter.printHelp( usage, options, true );
        }

        return cmd;
    }
}