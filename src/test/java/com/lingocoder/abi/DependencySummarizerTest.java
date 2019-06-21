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

import static com.lingocoder.abi.io.AbiIo.summarize;
import static com.lingocoder.classic.JarFileClassifier.classify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import com.lingocoder.reflection.ReportingProjectChecker;
import com.lingocoder.reflection.ReportingTypesChecker;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencySummarizerTest extends BaseReportingAbiInspectorTest {

    private static final Logger LOG = LoggerFactory.getLogger( "DependencySummarizerTest" );

    private DependencySummarizer classUnderTest;

    private static JarFile classesDir;

    private static JarFile aDependency;

    private ReportingTypesChecker<Set<String>, Class<?>, Set<Reporting>> projectChecker;

    private static File bitcoinj;

    private static File protobuf;
    
    @BeforeClass
    public static void setUpOnce( ) throws Exception { 

        bitcoinj = finder.findInCache( bitcoinjGAV ).orElse( artifact4Path ).toFile( );
        
        protobuf = finder.findInCache( protobufGAV ).orElse( artifact9Path ) .toFile( );
        
        classesDir = new JarFile( bitcoinj );

        aDependency = new JarFile( protobuf );
    }

    @Before
    public void setUp( ) throws Exception {
        this.classUnderTest = new DependencySummarizer( ); 
        this.projectChecker = new ReportingProjectChecker<>( );
    }

    @Test
    public void testEnterCountsSevenForProjectClassFour( ) {

        long expected = 7L;

        long actual = this.classUnderTest.enter( cmnsMathGAV, Set.of( this.expected4 ) );

        assertEquals( expected, actual );

        Map<String, LongAdder> actualSummary = this.classUnderTest.summarize( );

        assertEquals( expected, actualSummary.get( cmnsMathGAV ).longValue( ) );

    }
    
    @Test
    public void testEnterCountsNineForProjectClassesOneAndFour( ) {

        long expected = 9L;

        long actual = this.classUnderTest.enter( cmnsMathGAV,
                Set.of( this.expected1, this.expected4 ) );

        assertEquals( expected, actual );

        Map<String, LongAdder> actualSummary = this.classUnderTest.summarize( );

        assertEquals( expected, actualSummary.get( cmnsMathGAV ).longValue( ) );
    }

    @Test
    public void testEnterIncrementsCountOnConsecutiveEntries( ) {

        long expected = 2L;

        long actual = 0L;

        Map<String, LongAdder> actualSummary;

        actual = this.classUnderTest.enter( cmnsMathGAV, Set.of( this.expected1 ) );

        assertEquals( expected, actual );

        actualSummary = this.classUnderTest.summarize( );

        assertEquals( expected, actualSummary.get( cmnsMathGAV ).longValue( ) );

        expected = 9;

        actual = this.classUnderTest.enter( cmnsMathGAV, Set.of( this.expected4 ) );

        assertEquals( expected, actual );

        actualSummary = this.classUnderTest.summarize( );

        assertEquals( expected, actualSummary.get( cmnsMathGAV ).longValue( ) );

        summarize( actualSummary );
    }
    
    @Test
    public void testInspectSummarizesSupertypes( ) throws Exception {

        Set<String> depTypes = Set.of( "com.google.protobuf.AbstractParser",
                "com.google.protobuf.InvalidProtocolBufferException",
                "com.google.protobuf.CodedInputStream",
                "com.google.protobuf.ExtensionRegistryLite" );

        Set<String> projTypes = depTypes;

        var expectedFreq = 4L;

        var actualFreq = this.classUnderTest.enter( protobufGAV, depTypes, projTypes );

        assertEquals( expectedFreq, actualFreq );
    }

    @Test
    public void testEnterCalledNTimesReturnsSameFrequency( ) {

        int N = 100;

        long expectedFreq = 0L;

        long actualFreq = 0L;

        Set<Class<?>> classes = classify( classesDir );

        assertFalse( classes.isEmpty( ) );

        Set<String> projTypes = new HashSet<>( );

        classes.parallelStream( )
                .forEach( cls -> projTypes.addAll( this.projectChecker.check( cls ) ) );

        assertFalse( projTypes.isEmpty( ) );

        classes = classify( aDependency );
        
        assertFalse( classes.isEmpty( ) );

        
        Set<String> depTypes = classes.parallelStream( )
                .map( cls -> cls.getName( ) ).collect( Collectors.toSet( ) );

        assertFalse( depTypes.isEmpty( ) );


        expectedFreq = this.classUnderTest.enter( protobufGAV, depTypes, projTypes );
        
        assertTrue( expectedFreq > 0 );
            
        for ( int i = 1; i <= N; i++ ) {

            this.classUnderTest = new DependencySummarizer( );

            actualFreq = this.classUnderTest.enter( protobufGAV, depTypes, projTypes );

            assertTrue( actualFreq > 0 );
            
            assertEquals( expectedFreq, actualFreq );
        }
        LOG.info( "Expected Frequency {} / Actual Frequency {}", expectedFreq, actualFreq );
    }
}