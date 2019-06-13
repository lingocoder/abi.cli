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

import static com.lingocoder.file.Loader.toBinaryName;

import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.LongAdder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.lingocoder.file.GavTokenHelper;
import com.lingocoder.reflection.ReportingProjectChecker;
import com.lingocoder.reflection.ReportingTypesChecker;

/**
 * @author lingocoder
 *
 */
public class ReportingJarAbiInspector<T extends Reporting>
		implements AbiInspector<Reporting, Set<JarFile>>, Summarizer {

	private final Set<String> ignoreJdk = Set.of( "java.lang.", "sun.", "com.sun.", "javax.",
			"java.", "jdk." );
			
	private final Map<String, Set<String>> dependencyCache = new ConcurrentHashMap<>( );

	private final Map<Class<?>, Set<String>> projClassCache = new ConcurrentHashMap<>( );

	private final GavTokenHelper gav = new GavTokenHelper( );

	private ReportingTypesChecker<Set<String>, Class<?>, Set<Reporting>> projectChecker = new ReportingProjectChecker<>( );

	private DependencySummarizer summarizer = new DependencySummarizer( );

	@SuppressWarnings( "unchecked" )
	@Override
	public T inspect( Class<?> aProjectClass, Set<JarFile> dependencies ) {

		Set<String> allGAVs = new ConcurrentSkipListSet<>( );

		Set<Reporting> lines = new ConcurrentSkipListSet<>( );

		Set<String> projTypes = memoize( aProjectClass, projClassCache,
				( aClass ) -> this.projectChecker.check( aProjectClass, lines ) );

		for ( JarFile aDependency : dependencies ) {

			Set<String> depTypes = memoize( aDependency.getName( ), dependencyCache,
					( aJar ) -> this.inspect( aDependency ) );

			if ( depTypes.retainAll( projTypes ) && !depTypes.isEmpty( ) ) {

				String aGav = gav.toGAV( Paths.get( aDependency.getName( ) ) );
				
				allGAVs.add( aGav );

				summarizer.enter( aGav, lines );
			}
		}

		T report = (T) new ReportEntry( "class", aProjectClass.getName( ), lines,
				allGAVs );

		return report;
	}

	private Set<String> inspect( JarFile aDependency ) {

		Set<String> jarClasses = new ConcurrentSkipListSet<>( );

		Enumeration<JarEntry> entries = aDependency.entries( );

		while ( entries.hasMoreElements( ) ) {

			JarEntry entry = entries.nextElement( );

			if ( !entry.isDirectory( ) ) {

				String name = entry.getName( );

				if ( name.endsWith( ".class" ) && !name.startsWith( "META-INF" )
						&& !name.contains( "$" )
						&& !ignoreJdk.contains( name ) ) {

					jarClasses.add( toBinaryName( name ) );
				}
			}
		}
		return jarClasses;
	}
		
	private <K, V> Set<V> memoize( K aKey, Map<K, Set<V>> cache,
			Function<K, Set<V>> otherWise ) {

		Set<V> types;

		if ( cache.containsKey( aKey ) && cache.get( aKey ) != null && !cache.get( aKey ).isEmpty()) {

			types = cache.get( aKey );

		} else {
			cache.put( aKey, otherWise.apply( aKey ) );
			types = cache.get( aKey );
		}
		return types;
	}

	@Override
	public Map<String, LongAdder> summarize( ) {
		return summarizer.summarize();
	}
}
