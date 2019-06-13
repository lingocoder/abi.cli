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

import static com.lingocoder.abi.app.Configuration.DEFAULT_CLASSES_DIR;
import static com.lingocoder.abi.io.CLIHelper.formatter;
import static com.lingocoder.abi.io.CLIHelper.usage;
import static com.lingocoder.abi.io.SetUtil.toSet;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import com.lingocoder.abi.app.Configuration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class CmdLineInterface {

    public static Configuration parseArgs( String... args ) {

        // create Options object
        Options options = new Options( );

        OptionGroup gavOptGrp = new OptionGroup( );

        Path classesDir = DEFAULT_CLASSES_DIR;
    
        Set<String> gavs;
    
        String[ ] packagesToScan = { "com" };
    
        CommandLine cmd = CLIHelper.toCommandLine( args, options, gavOptGrp );

        if ( cmd.hasOption( "c" ) ) {

            classesDir = Paths.get( cmd.getOptionValue( "c" ) );
    
        } else {
            
            formatter.printHelp( usage, options, true );

        }
        if ( cmd.hasOption( "p" ) ) {

            packagesToScan = cmd.getOptionValues( "p" );
    
        } else {

            formatter.printHelp( usage, options, true );
        }

        String gSel = gavOptGrp.getSelected( );

        if ( gSel.equals( "a" ) ) {

            gavs =  toSet( Paths.get( cmd.getOptionValue( gSel ) ) );

        } else {

            gavs = toSet( cmd.getOptionValues( gSel ) );
        }
        
        return new Configuration(classesDir, gavs, packagesToScan, cmd.hasOption("v"), cmd.hasOption("s") );
    }
}