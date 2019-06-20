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

import static com.lingocoder.reflection.ReflectionHelper.in;
import static com.lingocoder.reflection.ReflectionHelper.notJdk;
import static com.lingocoder.reflection.ReflectionHelper.projPkgs;
import static java.util.Collections.emptySet;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.lingocoder.abi.ReportEntry;
import com.lingocoder.abi.Reporting;
import com.lingocoder.abi.ReportingComparator;

public class ReportingAnnotationChecker<T, U, V> implements
        ReportingTypesChecker<Set<Reporting>, Class<?>, Set<String>> {
         
    final Set<Reporting> noLines = emptySet( );

    final Set<String> noGAVs = emptySet( );

    private final TypesChecker<Set<String>, Class<?>> annotationChecker = new AnnotationChecker<>( );

    @Override
    public Set<Reporting> check( Class<?> type, Set<String> types ) {

		Set<Reporting> annotationLines = new ConcurrentSkipListSet<>( new ReportingComparator( )  );

		Set<Reporting> annotations = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

		Set<String> annotationTypes = annotationChecker.check( type );

        annotationTypes.parallelStream( ).filter( ann -> !in( ann, projPkgs ) ).filter( ann -> notJdk( ann ) ).forEach( ann -> {
            
        types.add( ann );
            
			annotationLines.add( new ReportEntry( "annotation", ann, noLines, noGAVs ) );
		} );

		if ( !annotationLines.isEmpty( ) ) {
			
			annotations.add( new ReportEntry( "annotations", "", annotationLines, noGAVs ) );
		}
		
		return annotations;
    }

    @Override
    public Set<Reporting> check( Class<?> type ) {
   
        return this.check( type, noGAVs );
    }
}