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
import static com.lingocoder.reflection.ReflectionHelper.permutate;
import static com.lingocoder.reflection.ReflectionHelper.projPkgs;
import static java.util.Collections.emptySet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

import com.lingocoder.abi.ReportEntry;
import com.lingocoder.abi.Reporting;
import com.lingocoder.abi.ReportingComparator;

public class ReportingConstructorChecker<T, U, V> implements
        ReportingTypesChecker<Set<Reporting>, Class<?>, Set<String>> {

    final Set<Reporting> noLines = emptySet( );

    final Set<String> noGAVs = emptySet( );

    @Override
    public Set<Reporting> check( Class<?> type, Set<String> types ) {

        projPkgs.addAll( permutate( type.getName( ) ) );
        
        projPkgs.addAll( permutate( "[L" + type.getName( ) ) );
        
        Set<Class<?>> constructorTypes = null;

        Set<Reporting> paramLines = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

        Set<Reporting> constructors = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

        int[ ] tracker = { 0 };

        int counter = 0;

        for ( Constructor<?> aMethod : type.getDeclaredConstructors( ) ) {

            if ( Modifier.isPublic( aMethod.getModifiers( ) )/*  && !in(aMethod.getName(), projPkgs) */)  {

                constructorTypes = Arrays.asList( aMethod.getParameterTypes( ) )
                        .stream( ).parallel( ).filter(typ -> !typ.isPrimitive( ) ).filter( ReflectionHelper::notJdk ).filter(typ -> !in(typ, projPkgs))
                        .collect(  toSet( ) );

                paramLines.addAll( constructorTypes
                        .stream( ).parallel( ).filter(typ -> !typ.isPrimitive( ) ).filter( ReflectionHelper::notJdk ).filter(typ -> !in(typ, projPkgs))
                        .map( cls -> {
                            tracker[ 0 ]++;
                            return new ReportEntry( "param", cls.getName( ),
                                    noLines, noGAVs );
                        } ).collect( toCollection( ConcurrentSkipListSet::new ) ) );

                types.addAll( constructorTypes.stream( ).parallel( )
                        .map( cls -> cls.getName( ) )
                        .collect( toCollection( ConcurrentSkipListSet::new ) ) );
                /*
                 * If a parameter type was not added in the notJdk filter
                 * predicate above, I can't add the corresponding method. So I'm
                 * keeping track.
                 */
                if ( tracker[ 0 ] > counter ){
                    constructors.add( new ReportEntry( "constructor",
                            aMethod.getName( ), paramLines, noGAVs ) );
                    counter = tracker[ 0 ];        
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