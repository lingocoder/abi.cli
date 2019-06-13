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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;

import static com.lingocoder.abi.io.AbiIo.summarize;

import org.junit.Before;
import org.junit.Test;

public class DependencySummarizerTest extends BaseReportingAbiInspectorTest {

    private static final String GAV1 = "org.apache.commons:commons-math:2.2";
    
    private DependencySummarizer classUnderTest;

    @Before
    public void setUp( ) {
        this.classUnderTest = new DependencySummarizer( );
    }

    @Test
    public void testEnterCountsSevenForProjectClassFour( ) {

        long expected = 7L;

        long actual = this.classUnderTest.enter( GAV1,
                Set.of( this.expected4 ) );

        assertEquals( expected, actual );

        Map<String, LongAdder> actualSummary = this.classUnderTest.summarize( );

        assertEquals( expected, actualSummary.get( GAV1 ).longValue( ) );

    }
    
    @Test
    public void testEnterCountsNineForProjectClassesOneAndFour( ) {

        long expected = 9L;

        long actual = this.classUnderTest.enter( GAV1,
                Set.of( this.expected1, this.expected4 ) );

        assertEquals( expected, actual );

        Map<String, LongAdder> actualSummary = this.classUnderTest.summarize( );

        assertEquals( expected, actualSummary.get( GAV1 ).longValue( ) );
    }

    @Test
    public void testEnterIncrementsCountOnConsecutiveEntries( ) {

        long expected = 2L;

        long actual = 0L;

        Map<String, LongAdder> actualSummary;

        actual = this.classUnderTest.enter( GAV1, Set.of( this.expected1 ) );

        assertEquals( expected, actual);

        actualSummary = this.classUnderTest.summarize( );

        assertEquals( expected, actualSummary.get( GAV1 ).longValue( ) );
    
        expected = 9;

        actual = this.classUnderTest.enter( GAV1, Set.of( this.expected4) );
        
        assertEquals( expected, actual );
        
        actualSummary = this.classUnderTest.summarize( );

        assertEquals( expected, actualSummary.get( GAV1 ).longValue( ) );
    
        summarize(actualSummary);
    }    
}