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
package com.lingocoder.classic;

import static com.lingocoder.file.Loader.loadIgnore;
import static com.lingocoder.file.Loader.toBinaryName;
import static com.lingocoder.reflection.ReflectionHelper.ignoreJdk;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarFileClassifier {

	private static final Logger LOG = LoggerFactory.getLogger( "JarFileClassifier" );

	private static final Set<String> ignore = loadIgnore( );

	public static Set<Class<?>> classify( JarFile aDependency ) {

		Set<Class<?>> jarClasses = new HashSet<>();

		Enumeration<JarEntry> entries = aDependency.entries( );

		while ( entries.hasMoreElements( ) ) {

			JarEntry entry = entries.nextElement( );

			if ( !entry.isDirectory( ) ) {

				String name = entry.getName( );

				if ( name.endsWith( ".class" ) && !name.startsWith( "META-INF" )
/* 						&& !name.contains( "$" ) */
						&& !ignoreJdk.contains( name )
						&& !ignore.contains( name ) ) {

					try {

						jarClasses.add( Class.forName( toBinaryName( name ) ) );

					} catch ( ClassNotFoundException | NoClassDefFoundError e ) {
						
						LOG.debug( e.getMessage( ) );						
					}
				}
			}
		}
		return jarClasses;
	}
}