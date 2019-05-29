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
package com.lingocoder.abi.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetUtil {

    private static final Logger LOG = LoggerFactory.getLogger( "SetUtil" );
    
    protected static Set<String> toSet( Path aFile ) {

        Set<String> lines = Set.of( );

        try {

            lines = Files.lines( aFile ).parallel( ).map( f -> {
                return f.strip( );
            } ).collect( Collectors.toSet( ) );

        } catch ( IOException e ) {

            LOG.error( e.getMessage( ), e );
        }

        return lines;
    }
    
    protected static Set<String> toSet( String[] strings ) {

        return Arrays.asList( strings ).stream( ).collect( Collectors.toSet( ) );
    }
}