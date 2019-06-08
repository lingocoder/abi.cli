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
import static com.lingocoder.reflection.ReflectionHelper.permutate;
import static com.lingocoder.reflection.ReflectionHelper.projPkgs;
import static java.util.Collections.emptySet;

import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import com.lingocoder.abi.ReportEntry;
import com.lingocoder.abi.Reporting;
import com.lingocoder.abi.ReportingComparator;

public class ReportingSupertypesChecker<T, U, V> implements ReportingTypesChecker<Set<Reporting>, Class<?>, Set<String>> { 

	private final Set<Reporting> noLines = emptySet( );

	private final Set<String> noGAVs = emptySet( );

    @Override

	public Set<Reporting> check( Class<?> type, Set<String> types ) {

        projPkgs.addAll( permutate( type.getName( ) ) );
        
        projPkgs.addAll( permutate( "[L" + type.getName( ) ) );

		Set<Class<?>> duperTypes = null;

		Set<Reporting> superTypes = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

		Class<?> superClass = ( (Class<?>) type ).getSuperclass( );

		if ( superClass != null && notJdk( superClass ) && !in(superClass, projPkgs) ) {
			superTypes.add( new ReportEntry( "supertype", superClass.getName( ),
					noLines, noGAVs ) );
		}

		duperTypes = Arrays.asList( ( (Class<?>) type ).getInterfaces( ) )
				.stream( ).parallel( ).filter( ReflectionHelper::notJdk ).filter(intf -> !in(intf, projPkgs))
				.collect( toSet( ) );
		
		superTypes.addAll( duperTypes
				.stream( ).parallel( ).filter( ReflectionHelper::notJdk ).filter(intf -> !in(intf, projPkgs))
				.map( cls -> new ReportEntry( "supertype", cls.getName( ),
				noLines, noGAVs ) )
				.collect( toCollection( ConcurrentSkipListSet::new ) ) );

				types.addAll(duperTypes.stream( ).parallel( )
				.map( cls -> cls.getName( ) ).collect( toCollection( ConcurrentSkipListSet::new ) ) );
		
		return superTypes;
	}

    @Override
    public Set<Reporting> check( Class<?> type ) {
        return this.check(type, noGAVs);
    }
}