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
package com.lingocoder.reflection;

import static com.lingocoder.file.Loader.loadIgnore;
import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;

import com.lingocoder.abi.Reporting;
import com.lingocoder.file.Loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReportingProjectChecker<T, U, V> implements
        ReportingTypesChecker<Set<String>, Class<?>, Set<Reporting>> {

    private static final Logger LOG = LoggerFactory.getLogger( "ReportingProjectChecker" );

	private static final Set<String> ignore = loadIgnore( );

	private ReportingTypesChecker<Set<Reporting>, Class<?>, Set<String>> supertypesChecker = new ReportingSupertypesChecker<>( );
	
	private ReportingTypesChecker<Set<Reporting>, Class<?>, Set<String>> methodChecker = new ReportingMethodChecker<>( );

	private ReportingTypesChecker<Set<Reporting>, Class<?>, Set<String>> constructorChecker = new ReportingConstructorChecker<>( );
	
	private ReportingTypesChecker<Set<Reporting>, Class<?>, Set<String>> annotationChecker = new ReportingAnnotationChecker<>( );

	final Set<Reporting> noLines = new HashSet<>( 0 );	

    final Set<String> noGAVsnes = new HashSet<>( 0 );

    @Override
	public Set<String> check( Class<?> aProjectClass,
			Set<Reporting> lines ) {

		Set<String> projTypes = new ConcurrentSkipListSet<>( );

		String className = Loader.toIgnoredName( aProjectClass.getName( ) );

		if ( !ignore.contains( className ) ) {

			LOG.debug( "Checking Project Class '{}' ('{}')", aProjectClass.getName( ),
					className );

			lines.addAll( this.supertypesChecker.check( aProjectClass, projTypes ) );

			lines.addAll( this.constructorChecker.check( aProjectClass, projTypes ) );

			lines.addAll( this.methodChecker.check( aProjectClass, projTypes ) );

			lines.addAll( this.annotationChecker.check( aProjectClass, projTypes ) );

		} else {

			LOG.debug( "Ignoring Project Class '{}'", className );
		}
		return projTypes;
	}

    @Override
	public Set<String> check( Class<?> aProjectClass ) {
		
        return this.check( aProjectClass, noLines );
        }
}