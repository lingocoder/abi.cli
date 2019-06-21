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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Collections;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class ReportEntryTest extends BaseReportingAbiInspectorTest {
    
    @Before
    public void setUp( ) {
        dependency4 = finder.findInCache( bitcoinjGAV ).orElse( artifact4Path ).toFile( );
    }

    @Test
    public void testEquals( ) {

        Reporting entry1 = new ReportEntry( "exception",
                "org.bitcoinj.protocols.channels.ValueOutOfRangeException",
                Collections.emptySet( ), Collections.emptySet( ) );
        Reporting entry2 = new ReportEntry( "exception",
                "org.bitcoinj.protocols.channels.ValueOutOfRangeException",
                Collections.emptySet( ), Collections.emptySet( ) );

        Reporting entry3 = new ReportEntry( "exception",
                "org.bitcoinj.protocols.channels.ValueOutOfRangeException",
                Set.of( entry1 ), Collections.emptySet( ) );

        assertEquals( entry1, entry2 );
        assertNotEquals( entry2, entry3 );

        Set<Reporting> lines = new ConcurrentSkipListSet<>( );
        lines.add( entry1 );
        lines.add( entry2 );
        lines.add( entry3 );

        assertEquals( 2, lines.size( ) );

    }

    @Test
    public void testEqualsForOverloadedMethods( ) {

        Reporting foo = new ReportEntry("param", "foo", Collections.emptySet(), Collections.emptySet() );
        Reporting bar = new ReportEntry("param", "bar", Collections.emptySet(), Collections.emptySet() );
        Reporting baz = new ReportEntry( "return", "baz", Collections.emptySet( ), Collections.emptySet( ) );
        
        Reporting method1 = new ReportEntry( "method", "m", Set.of(foo), Collections.emptySet() );
        Reporting method2 = new ReportEntry( "method", "m", Set.of(foo, bar), Collections.emptySet() );
        Reporting method3 = new ReportEntry( "method", "m", Set.of( foo, baz ), Collections.emptySet( ) );
        
        assertFalse( method1.equals( method2 ) );
        assertFalse( method3.equals( method2 ) );
        
    }
}