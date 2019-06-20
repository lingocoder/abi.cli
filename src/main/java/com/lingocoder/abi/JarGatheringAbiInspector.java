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

import static com.lingocoder.reflection.ReflectionHelper.prime;
import java.util.Collections;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;
import java.util.jar.JarFile;

public class JarGatheringAbiInspector<T, U, V> implements GatheringAbiInspector<Reporting, Class<?>, JarFile> {

	private AbiInspector<Reporting, Set<JarFile>> jarAbiInspector = new ReportingJarAbiInspector<>( );

	@Override
	public Set<Reporting> inspect( Set<Class<?>> projectClasses,
			Set<JarFile> dependencies ) {

		prime( projectClasses );
				
		Set<Reporting> reports = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

		projectClasses.stream( ).forEach( prjCls -> {
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
}