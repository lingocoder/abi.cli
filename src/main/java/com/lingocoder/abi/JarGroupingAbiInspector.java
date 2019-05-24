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

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import com.lingocoder.file.GavTokenHelper;

public class JarGroupingAbiInspector<T, U>
		implements GroupingAbiInspector<Class<?>, JarFile> {

	private AbiInspector<Set<String>, JarFile> jarAbiInspector = new JarAbiInspector( );

	private GavTokenHelper gav = new GavTokenHelper( );

	@Override
	public Map<Class<?>, Set<String>> inspect( Class<?> aProjectClass,
			JarFile aDependency ) {

		Map<Class<?>, Set<String>> groupedAbi = new HashMap<>( );

		this.groupBy( aProjectClass, aDependency, groupedAbi );

		return groupedAbi;
	}

	@Override
	public Map<Class<?>, Set<String>> inspect( Set<Class<?>> projectClasses,
			Set<JarFile> dependencies ) {

		Map<Class<?>, Set<String>> groupedAbi = new HashMap<>( );

		for ( Class<?> aProjectClass : projectClasses ) {

			for ( JarFile aDependency : dependencies ) {

				this.groupBy( aProjectClass, aDependency, groupedAbi );
			}
		}

		return groupedAbi;
	}

	private void groupBy( Class<?> aProjectClass, JarFile aDependency,
			Map<Class<?>, Set<String>> groupedAbi ) {

		Set<String> types = this.jarAbiInspector.inspect( aProjectClass,
				aDependency );

		if ( !types.isEmpty( ) ) {
			if ( groupedAbi.get( aProjectClass ) == null )
				groupedAbi.put( aProjectClass, new HashSet<String>( ) );

			String jarFilePath = aDependency.getName( );

			gav.toArtifactToken( Paths.get( jarFilePath ) )
					.ifPresent( artifact -> groupedAbi.get( aProjectClass )
							.add( artifact ) );
		}
	}
}