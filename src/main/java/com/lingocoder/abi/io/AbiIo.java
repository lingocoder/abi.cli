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

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.lingocoder.abi.Reporting;

public class AbiIo {

	private static final String next = "|    +--- ";

	private static final String last = "|____" + "\\--- ";
	
	/**
	 * Displays the results of an ABI inspection in <em><code>stdout</code></em>.
	 * 
	 * @param abiMapping An association of ABI-scanned classes to their corresponding dependencies. 
	 */
	public static void print( Map<Class<?>, Set<String>> abiMapping ) {

		try ( PrintWriter prntWrtr = new PrintWriter( System.out, true, UTF_8 ) ) {

			Set<Class<?>> keys = abiMapping.keySet( );

			for ( Class<?> key : keys ) {

				prntWrtr.println( "\n" + ">--: " + "Project class "
						+ key.getName( ) + " has an ABI dependency on..." );

				int numVals = abiMapping.get( key ).size( );

				int valCnt = 0;

				for ( Object dependency : abiMapping.get( key ) ) {

					prntWrtr.println( ( valCnt++ < numVals - 1 ? next : last )
							+ dependency );
				}
			}
		}
	}

	/**
	* Displays the results of an ABI inspection in <em><code>stdout</code></em>.
	* 
	* @param abiReports An association of ABI-scanned classes to their corresponding dependencies. 
	*/
	public static void print( Set<Reporting> abiReports ) {

		try (PrintWriter prntWrtr = new PrintWriter( System.out, true,
				UTF_8 )) {
			for ( Reporting report : abiReports ) {

				prntWrtr.println( "\n\n" + ">--: "
						+ "ABI dependencies found for...\n|" );

				report.print( );
			}
		}
	}
}