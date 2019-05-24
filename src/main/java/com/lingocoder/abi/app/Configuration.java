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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Configuration {

	private Path classesDir;

	private String packageToScan;

	/**
	 * 
	 * @param args
	 *                 <ul>
	 *                 <li>Element <code>0</code> must be
	 *                 <em><code>projectClassesRoot</code></em>.</li>
	 *                 <li>Element <code>1</code> must be
	 *                 <em><code>a specific package</code></em>.</li>
	 *                 <li>Elements <code>2-args.length</code> must be
	 *                 <em><code>G:A:V</code>-style</em> dependency
	 *                 coordinates.</li>
	 *                 </ul>
	 * @return A {@link Set} of <em><code>G:A:V</code>-style</em> dependency
	 *         coordinates.
	 */
	protected Set<String> configure( String... args ) {
		this.classesDir = Paths.get( args[ 0 ] );
		this.packageToScan = args[ 1 ];
		Set<String> dpendnCoordinates = Arrays
				.asList( Arrays.copyOfRange( args, 2, args.length ) ).stream( )
				.parallel( ).collect( Collectors.toSet( ) );
		return dpendnCoordinates;
	}

	protected String getPackageToScan( ) {
		return this.packageToScan;
	}

	protected Path getClassesDir( ) {
		return this.classesDir;
	}

	protected void setClassesDir( Path classesDir ) {
		this.classesDir = classesDir;
	}

	protected void setPackageToScan( String packageToScan ) {
		this.packageToScan = packageToScan;
	}
}