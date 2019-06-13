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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class DependencySummarizer {

    private final Map<String, LongAdder> dependencyCount = new ConcurrentHashMap<>( );
    
    public long enter( String gav, Collection<Reporting> entries ) {

        List<Reporting> members = entries.parallelStream( )
            .flatMap( cls -> cls.getLines( ).parallelStream( ) )
            .collect( Collectors.toList( ) );
        
        List<Reporting> params = members.parallelStream( )
            .flatMap( mbrs -> mbrs.getLines( ).parallelStream( ) )
            .collect(Collectors.toList());
                
        long count = members.size( ) + params.size( );

        dependencyCount.putIfAbsent( gav, new LongAdder( ) );

        dependencyCount.get( gav ).add( count );

        return dependencyCount.get( gav ).sum( );
     }
 
     public Map<String, LongAdder> summarize( ){ 
 
         return dependencyCount;
     }
}