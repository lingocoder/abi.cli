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
package com.lingocoder.abi;

import static com.lingocoder.file.Loader.loadIgnore;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.lingocoder.reflection.ComposedTypesChecker;
import com.lingocoder.reflection.TypesChecker;

public class JarAbiInspector implements AbiInspector<Set<String>, JarFile> {

	private TypesChecker<Set<String>, Class<?>> checker = new ComposedTypesChecker<>( );

	private static final Set<String> ignore = loadIgnore( );

	private Map<String, Set<String>> dependencyCache = new ConcurrentHashMap<>( );

	private Map<Class<?>, Set<String>> projClassCache = new ConcurrentHashMap<>( );

	@Override
	public Set<String> inspect( Class<?> aProjectClass, JarFile aDependency ) {

		Set<String> depTypes = memoize( aDependency.getName( ),
				this.dependencyCache,
				( aJar ) -> this.checkDependency( aDependency ) );
		
		Set<String> projTypes = memoize( aProjectClass, this.projClassCache,
				( aClass ) -> this.checker.check( aProjectClass ) );
		
		depTypes.retainAll( projTypes );

		return depTypes;
	}

	private Set<String> checkDependency( JarFile aDependency ) {

		Set<String> jarClasses = new HashSet<>( );

		Enumeration<JarEntry> entries = aDependency.entries( );

		while ( entries.hasMoreElements( ) ) {

			JarEntry entry = entries.nextElement( );

			if ( !entry.isDirectory( ) ) {

				String name = entry.getName( );

				if ( name.endsWith( ".class" ) && !name.startsWith( "META-INF" )
						&& !name.contains( "$" ) && !ignore.contains( name ) ) {

					jarClasses.add(
							name.replace( "/", "." ).replace( ".class", "" ) );
				}
			}
		}
		return jarClasses;
	}
	
	private <K, V> Set<V> memoize( K key, Map<K, Set<V>> cache,
			Function<K, Set<V>> otherWise ) {

		Set<V> types = Collections.emptySet( );

		if ( cache.containsKey( key ) ) {

			types = cache.get( key );

		} else {
			types = otherWise.apply( key );
		}
		return types;
	}
}