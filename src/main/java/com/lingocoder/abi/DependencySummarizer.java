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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencySummarizer {

    private final Map<String, LongAdder> frequency = new ConcurrentHashMap<>( );
    
    private static final Logger LOG = LoggerFactory.getLogger( "DependencySummarizer" );

    public long enter( String gav, Set<String> depTypes, Set<String> projTypes ) {
        
        if ( depTypes.isEmpty( ) )
            throw new IllegalArgumentException( "depTypes cannot be empty" );

        var types = new ConcurrentSkipListSet<>( projTypes );

        types.removeIf( t -> !depTypes.contains( t ) );
        
        LOG.debug( "{}", types );

        long count = types.size();

        if ( count == 0 ) {

            LOG.debug( "0 size for '{}' {} project types", projTypes.size( ), gav );

            this.logEmptyTypes( depTypes, projTypes );
        }
        
        this.frequency.computeIfAbsent( gav, k -> new LongAdder( ) ).add( count );

        return this.frequency.get( gav ).sum( );
    }

    public long enter( String gav, Set<String> depTypes, Collection<Reporting> reports ) {
    
        if ( depTypes.isEmpty( ) )
            throw new IllegalArgumentException( "depTypes cannot be empty" );
    
        /* Supertypes first; they have no „lines“ */
        var types = reports.parallelStream( )
            .filter(mbr -> !mbr.getName().equals("method"))
            .map( rpt -> rpt.getType( ) )
            .filter( typ -> depTypes.contains( typ ) )
            .collect( Collectors.toList( ) );
         
        types.addAll( /* Set<Reporting> params =  */reports.parallelStream( )
            .flatMap( mbrs -> mbrs.getLines( ).parallelStream( ) )
            .map( rpt -> rpt.getType( ) )
            .filter( typ -> depTypes.contains( typ ) )
            .collect( Collectors.toList( ) ) );
        
        LOG.debug( "{}", types );

        long count = types.size();

        if ( count == 0 ) {

            LOG.debug( "0 size for '{}' {} members", reports.size( ), gav );

            count += this.enter( depTypes, reports );
        }
        
        this.frequency.computeIfAbsent( gav, k -> new LongAdder( ) ).add( count );

        return this.frequency.get( gav ).sum( );
    }

    public long enter( String gav, Collection<Reporting> reports ) {

        Set<Reporting> members = reports.parallelStream( )
            .flatMap( mbrs -> mbrs.getLines( ).parallelStream( ) )
            .collect( Collectors.toSet( ) );

        Set<Reporting> params = members.parallelStream( )
            .flatMap( prms -> prms.getLines( ).parallelStream( ) )
            .collect( Collectors.toSet( ) );
            
        long count = members.size( ) + params.size( );

        this.frequency.computeIfAbsent( gav, k -> new LongAdder( ) ).add( count );

        return this.frequency.get( gav ).sum( );
    }
     
    public long enter( String gav, long count ) {

        this.frequency.computeIfAbsent( gav, k -> new LongAdder( ) ).add( count );

        return this.frequency.get( gav ).sum( );
    }

    public Map<String, LongAdder> summarize( ) {

        return this.frequency;
    }
   
    private long enter( Set<String> depTypes, Collection<Reporting> reports ) {
        
        /* Supertypes first; they have no „lines“ */
        var types = reports.parallelStream( )
            .filter(mbr -> !mbr.getName().equals("method"))
            .map( rpt -> rpt.getType( ) )
            .collect( Collectors.toList( ) );
         
        types.addAll( /* Set<Reporting> params =  */reports.parallelStream( )
            .flatMap( mbrs -> mbrs.getLines( ).parallelStream( ) )
            .map( rpt -> rpt.getType( ) )
            .collect( Collectors.toList( ) ) );
        
        this.logEmptyTypes( depTypes, types );
        
        return types.size( );
    }

    private void logEmptyTypes( Set<String> depTypes, Collection<?> types ) {

        LOG.debug( "{}", "__________________________________________" );
        LOG.debug( "{}", types );

        LOG.debug( "{}", "==========================================" );
        LOG.debug( "depTypes {} ({}) (isEmpty : {}) depTypes", depTypes, depTypes.size(), depTypes.isEmpty() );
        LOG.debug( "{}", "==========================================" );
        LOG.debug( "{}", "__________________________________________" );

    }
}