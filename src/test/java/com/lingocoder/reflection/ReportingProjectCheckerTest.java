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

import static com.lingocoder.classic.JarFileClassifier.classify;
import static com.lingocoder.reflection.ReflectionHelper.prime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import com.lingocoder.abi.BaseReportingAbiInspectorTest;
import com.lingocoder.abi.DependencySummarizer;
import com.lingocoder.abi.Reporting;

import org.apache.commons.math.util.MathUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportingProjectCheckerTest extends BaseReportingAbiInspectorTest {
    
    private static final Logger LOG = LoggerFactory.getLogger( "ReportingProjectCheckerTest" );

    private static ReportingTypesChecker<Set<String>, Class<?>, Set<Reporting>> classUnderTest;

    private static JarFile classesDir;
    
    private static JarFile aDependency;

    private static Set<Class<?>> testClasses;

    private int expectedNumberOfTypes = 0;

    private int actualNumberOfTypes = 0;

    private static List<String> expectedTypes = new ArrayList<>( );
    
    private List<String> actualTypes = new ArrayList<>( );
    
    private int N;

    private static long expectedNumberOfLines = 0L;

    private long actualNumberOfLines = 0L;

    private static Set<Reporting> expectedLines;

    private static List<Reporting> expectedAccumulatedLines;

    private Set<Reporting> actualLines = new ConcurrentSkipListSet<>( );

    private static Set<Class<?>> dependencies;

    private static Set<String> depTypes;

    private static File bitcoinj = finder.findInCache( bitcoinjGAV ).orElse( artifact4Path ).toFile( );
        
    private static File protobuf = finder.findInCache( protobufGAV ).orElse( artifact9Path ).toFile( );

    private static File guava = finder.findInCache( guavaGAV ).orElse( artifact10Path ).toFile( );

/*     private static File bitcoinj16 = finder.findInCache( bitcoinj16GAV ).orElse( artifact11Path ).toFile( ); */
                        

    @BeforeClass
    public static void setUpOnce( ) throws Exception {

        classesDir = new JarFile( bitcoinj );

        aDependency = new JarFile( guava );

        testClasses = classify( classesDir );

        prime( testClasses );

        dependencies = classify( aDependency );
        
        assertFalse( dependencies.isEmpty( ) );
        
        depTypes = dependencies.stream( )
                .map( cls -> cls.getName( ) ).collect( Collectors.toSet( ) );

        LOG.info( "'{}' Project classes", testClasses.size( ) );

        expectedTypes = new ArrayList<>( );

        classUnderTest = new ReportingProjectChecker<>( );

        expectedAccumulatedLines = new ArrayList<>( );
        
        for ( Class<?> prjCls : testClasses ) {

            expectedLines = new ConcurrentSkipListSet<>( );

            expectedTypes.addAll( new ReportingProjectChecker<>( ).check( prjCls, expectedLines ) );
            
            expectedAccumulatedLines.addAll( expectedLines );

        }
        
        assertFalse( expectedAccumulatedLines.isEmpty( ) );
        
        expectedNumberOfLines = new DependencySummarizer( ).enter( GAV3,

        depTypes, expectedAccumulatedLines );
    }

    @Before
    public void setUp( ) throws Exception {


        this.expectedNumberOfTypes = 0;

        this.actualNumberOfTypes = 0;

        this.actualTypes = new ArrayList<>();

        this.N = 5;

        this.expectedNumberOfTypes = expectedTypes.size( );

    }

    @Test
    public void testCheckCalledNTimesReturnsSameNumberOfProjectTypes( ) {

/*         LOG.info( "Expected Types {}", expectedTypes ); */
        for ( int i = 1; i <= N; i++ ) {

            actualNumberOfTypes = 0;

            actualTypes = new ArrayList<>( );

            for ( Class<?> prjCls : testClasses ) {

                actualTypes.addAll( new ReportingProjectChecker<>( ).check( prjCls ) );
            }

            actualNumberOfTypes = actualTypes.size( );

            LOG.info(
                    "expectedNumberOfTypes is '{}' / actualNumberOfTypes is '{}' ",
                    expectedNumberOfTypes, actualNumberOfTypes );

/*             LOG.info( "Actual Types {}", actualTypes ); */

            assertEquals( expectedNumberOfTypes, actualNumberOfTypes );
        }
    }

    @Test
    public void testCheckCalledNTimesReturnsSameNumberOfProjectLines( ) {

        assertFalse( depTypes.isEmpty( ) );
        for ( int i = 1; i <= N; i++ ) {

            List<Reporting> actualAccumulatedLines = new ArrayList<>( );

            actualNumberOfLines = 0L;



            for ( Class<?> prjCls : testClasses ) {

                actualLines = new ConcurrentSkipListSet<>( );
                actualTypes.addAll(
                        new ReportingProjectChecker<>( ).check( prjCls,
                                actualLines ) );
                    
                actualAccumulatedLines.addAll( actualLines);
            }

            actualNumberOfLines = new DependencySummarizer( ).enter( GAV3,
                    depTypes, actualAccumulatedLines );

            LOG.info(
                "expectedNumberOfLines is '{}' / actualNumberOfLines is '{}' (Both are close enough to {})",
                expectedNumberOfLines, actualNumberOfLines, ((int)Math.floor(MathUtils.round( actualNumberOfLines, 3 ) ) ) );
    
/*             diff( extractLines(expectedLines), extractLines(actualLines) ); */
            assertTrue( extractLines(expectedLines).containsAll(extractLines(actualLines) ) );
            assertEquals( Math.floor( expectedNumberOfLines ), Math.floor( actualNumberOfLines ), 50 );
        }
    }
    
    private void diff( List<String> set1, List<String> set2 ) {

        if ( set1.size( ) > set2.size( ) ) {
            set1.removeAll( set2 );
/*             LOG.info( "Set 1 is larger. The difference is {}", set1 ); */
        } else {
            set2.removeAll( set1 );
/*             LOG.info( "Set 2 is larger. The difference is {}", set2 ); */
        }
    }
    
    private List<String> extractLines( Set<Reporting> reports ) {
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
       return types; 
    }
}
