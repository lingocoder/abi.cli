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

import java.util.Map;
import java.util.Set;

public class AbiIo {

	private static final String next = "|    +--- ";

	private static final String last = "|____" + "\\--- ";
	
	/**
	 * Displays the results of an ABI inspection in <em><code>stdout</code></em>.
	 * 
	 * @param abiMapping An association of ABI-scanned classes to their corresponding dependencies. 
	 */
	public static void print( Map<Class<?>, Set<String>> abiMapping ) {

		Set<Class<?>> keys = abiMapping.keySet( );

		for ( Class<?> key : keys ) {

			System.out.println( "\n" + ">--: " + "Project class " + key.getName( )
					+ " has an ABI dependency on..." );

			int numVals = abiMapping.get( key ).size( );

			int valCnt = 0;

			for ( Object dependency : abiMapping.get( key ) ) {

				System.out.println(
						( valCnt++ < numVals-1 ? next : last ) + dependency );
			}
		}
	}
}