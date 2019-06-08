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

import java.util.Collections;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;
import java.util.jar.JarFile;

public class JarGatheringAbiInspector<T, U, V> implements GatheringAbiInspector<Reporting, Class<?>, JarFile> {

	private AbiInspector<Reporting, Set<JarFile>> jarAbiInspector = new ReportingJarAbiInspector<>( );

	@Override
	public Set<Reporting> inspect( Set<Class<?>> projectClasses,
			Set<JarFile> dependencies ) {

		Set<Reporting> reports = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

		projectClasses.stream( ).parallel( ).forEach( prjCls -> {
			Reporting report = this.jarAbiInspector.inspect( prjCls,
					dependencies );
			if ( !report.getGAVs( ).isEmpty( ) )
			  reports.add( report  );			
		} );
		return reports;
	}

	@Override
	public Set<Reporting> inspect( Class<?> aProjectClass,
			JarFile aDependency ) {

		Reporting report = this.jarAbiInspector.inspect( aProjectClass, Set.of( aDependency ) );

		return report.getGAVs().isEmpty( ) ? Collections.emptySet() : Set.of( report  );
	}



/* 
	@Override
	public Map<Class<?>, Reporting> inspect( Class<?> aProjectClass,
			JarFile aDependency ) {

		Map<Class<?>, Reporting> groupedAbi = new HashMap<>( );

		this.groupBy( aProjectClass, aDependency, groupedAbi );

		return groupedAbi;
	} */
/* 
	@Override
	public Map<Class<?>, Reporting> inspect( Set<Class<?>> projectClasses,
			Set<JarFile> dependencies ) {

		Map<Class<?>, Reporting> groupedAbi = new HashMap<>( );

		for ( Class<?> aProjectClass : projectClasses ) {

			for ( JarFile aDependency : dependencies ) {

				this.groupBy( aProjectClass, aDependency, groupedAbi );
			}
		}

		return groupedAbi;
	} */
/* 
	private void groupBy( Class<?> aProjectClass, Set<JarFile> dependencies,
			Map<Class<?>, Reporting> groupedAbi ) {

		Reporting entry = this.jarAbiInspector.inspect( aProjectClass,
				dependencies );

		if ( entry != null ) {
			if ( groupedAbi.get( aProjectClass ) == null )
				groupedAbi.put( aProjectClass, entry ); */
/* 
			String jarFilePath = aDependency.getName( );

			gav.toArtifactToken( Paths.get( jarFilePath ) )
					.ifPresent( artifact -> groupedAbi.get( aProjectClass )
							.add( artifact ) );
 *//* 		}
	} */
}