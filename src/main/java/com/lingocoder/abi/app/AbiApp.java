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
package com.lingocoder.abi.app;

import static com.lingocoder.abi.io.AbiIo.print;
import static com.lingocoder.abi.io.CmdLineInterface.dependenciesFile;
import static com.lingocoder.abi.io.CmdLineInterface.parseArgs;

import java.util.Map;
import java.util.Set;

public class AbiApp {

	static public void main( String... args ) {

		Abi app = null;

		String[ ] parsedArgs = parseArgs( args );

		if ( dependenciesFile( ).isPresent( ) ) {

			app = new Abi( dependenciesFile( ).get( ) );
			
		} else {
			
			app = new Abi( );
		}

		Map<Class<?>, Set<String>> groupedAbi = app.inspect( parsedArgs );

		print( groupedAbi );
	}
}