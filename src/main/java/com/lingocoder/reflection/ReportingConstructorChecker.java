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
import static com.lingocoder.reflection.ReflectionHelper.projPkgs;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.lingocoder.abi.ReportEntry;
import com.lingocoder.abi.Reporting;
import com.lingocoder.abi.ReportingComparator;

public class ReportingConstructorChecker<T, U, V> implements
        ReportingTypesChecker<Set<Reporting>, Class<?>, Set<String>> {

    final Set<Reporting> noLines = emptySet( );

    final Set<String> noGAVs = emptySet( );

    @Override
    public Set<Reporting> check( Class<?> type, Set<String> types ) {
        
        Set<Class<?>> constructorTypes = null;

        Set<Reporting> paramLines = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

        Set<Reporting> constructors = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

        for ( Constructor<?> aMethod : type.getDeclaredConstructors( ) ) {

            if ( Modifier.isPublic( aMethod.getModifiers( ) )/*  && !in(aMethod.getName(), projPkgs) */)  {

                constructorTypes = List.of( aMethod.getParameterTypes( ) )
                        .parallelStream( ).filter(typ -> !typ.isPrimitive( ) ).filter( ReflectionHelper::notJdk ).filter(typ -> !in(typ, projPkgs))
                        .collect(  toSet( ) );

                paramLines.addAll( constructorTypes
                        .parallelStream( )
                        .map( cls -> {
                            return new ReportEntry( "param", cls.getName( ),
                                    noLines, noGAVs );
                        } ).collect( toCollection( ConcurrentSkipListSet::new ) ) );

                types.addAll( constructorTypes.parallelStream( )
                        .map( cls -> cls.getName( ) )
                        .collect( toCollection( ConcurrentSkipListSet::new ) ) );

                if ( !paramLines.isEmpty( ) ){
                    constructors.add( new ReportEntry( "constructor",
                        aMethod.getName( ), paramLines, noGAVs ) );
                }            
            }
        }
        return constructors;
    }

    @Override
    public Set<Reporting> check( Class<?> type ) {
        return this.check( type, noGAVs );
    }
}