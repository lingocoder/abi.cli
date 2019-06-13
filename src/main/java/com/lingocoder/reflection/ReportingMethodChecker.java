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

import static com.lingocoder.reflection.ReflectionHelper.permutate;
import static com.lingocoder.reflection.ReflectionHelper.projPkgs;
import static com.lingocoder.reflection.ReflectionHelper.in;
import static com.lingocoder.reflection.ReflectionHelper.notJdk;
import static com.lingocoder.reflection.ReflectionHelper.primitiveArray;
import static java.util.Collections.emptySet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

import com.lingocoder.abi.ReportEntry;
import com.lingocoder.abi.Reporting;
import com.lingocoder.abi.ReportingComparator;

public class ReportingMethodChecker<T, U, V> implements
        ReportingTypesChecker<Set<Reporting>, Class<?>, Set<String>> {

    private final Set<Reporting> noLines = emptySet( );

    private final Set<String> noGAVs = emptySet( );

    @Override
    public Set<Reporting> check( Class<?> type, Set<String> types ) {

        projPkgs.addAll( permutate( type.getName( ) ) );
        
        projPkgs.addAll( permutate( "[L" + type.getName( ) ) );
/*         System.out.println( projPkgs ); */
        Set<Class<?>> paramTypes = null;

        Set<Reporting> methods = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

        int[ ] tracker = { 0 };

        int counter = 0;

        for ( Method aMethod : type.getDeclaredMethods( ) ) {

            Set<Class<?>> exceptionTypes = null;

            Set<Reporting> paramLines = new ConcurrentSkipListSet<>( new ReportingComparator( ) );

            if ( Modifier.isPublic( aMethod.getModifiers( ) ) ) {

                paramTypes = List.of( aMethod.getParameterTypes( ) )
                        .parallelStream( ).distinct( ).filter(prm -> !in(prm, projPkgs)).parallel( )
                        
                           .filter( ReflectionHelper::notJdk ).filter(prm ->
                          !prm.isPrimitive() && !primitiveArray.contains(
                          prm.getTypeName( ) ) )
                         .collect( toSet( ) );

                paramLines.addAll( paramTypes.parallelStream( )
                        
/*                          .filter( ReflectionHelper::notJdk ).filter(prm -> !in(prm, projPkgs) ) */
                         .map( cls -> {
                            tracker[ 0 ]++;
                            return new ReportEntry( "param", cls.getName( ),
                                    noLines, noGAVs );
                        } ).collect( toCollection( ConcurrentSkipListSet::new ) ) );

                exceptionTypes = Set.of( aMethod.getExceptionTypes( ) )
                        .parallelStream( ).distinct( ).filter(exc -> !in( exc, projPkgs ) ).parallel( )
                        .filter( ReflectionHelper::notJdk )
                        .collect( toSet( ) );

                paramLines.addAll( exceptionTypes.parallelStream( )
                        .filter( ReflectionHelper::notJdk ).map( cls -> {
                            tracker[ 0 ]++;
                            return new ReportEntry( "exception", cls.getName( ),
                                    noLines, noGAVs );
                        } ).collect( toCollection( ConcurrentSkipListSet::new ) ) );

                types.addAll( paramTypes.parallelStream( )
                        .map( cls -> cls.getName( ) )
                        .collect( toSet( ) ) );

                types.addAll( exceptionTypes.parallelStream( )
                        .map( cls -> cls.getName( ) )
                        .collect( toSet( ) ) );

                Class<?> retType = aMethod.getReturnType( );
                
                if ( !in( retType, projPkgs ) && !retType.getSimpleName( ).equals( "void" ) &&
                !retType.isPrimitive( ) && !primitiveArray.contains(
                retType.getTypeName( ) ) && notJdk( retType )) {
                    types.add( retType.getName( ) );
                tracker[ 0 ]++;

                paramLines.add( new ReportEntry( "return", retType.getName( ),
                        noLines, noGAVs ) );
                }
                    /*
                     * If a parameter type was not added in the notJdk tests
                     * above, I can't add the corresponding method. So I'm
                     * keeping track.
                     */
                if ( tracker[ 0 ] > counter ){
                    methods.add( new ReportEntry(
                            "method", aMethod.getName( ), paramLines,
                            noGAVs ) );
                     counter = tracker[ 0 ];
                }            
            }
        }
        return methods;
    }

    @Override
    public Set<Reporting> check( Class<?> type ) {
        return this.check( type, noGAVs );
    }

}