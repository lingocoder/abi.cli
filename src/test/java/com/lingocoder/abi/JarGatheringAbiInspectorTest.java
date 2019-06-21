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

import static com.lingocoder.abi.io.AbiIo.print;
import static com.lingocoder.poc.ProjectClass.projectClass1;
import static com.lingocoder.poc.ProjectClass.projectClass2;
import static com.lingocoder.poc.ProjectClass.projectClass3;
import static com.lingocoder.poc.ProjectClass.projectClass4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.jar.JarFile;

import com.lingocoder.reflection.test.BaseAbiInspectorTest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JarGatheringAbiInspectorTest extends BaseAbiInspectorTest {

	private GatheringAbiInspector<Reporting, Class<?>, JarFile> classUnderTest;

	private static final String expectedAnnotationPkg = "com.fasterxml.jackson.annotation.";

	private static final Set<String> expectedAnnotations = Set.of( expectedAnnotationPkg + "JsonAnySetter",
			expectedAnnotationPkg + "JsonAnyGetter",
			expectedAnnotationPkg + "JsonPropertyOrder", expectedAnnotationPkg + "JsonProperty",
			expectedAnnotationPkg + "JsonInclude" );

	public JarGatheringAbiInspectorTest( ) {
		super( );
	}

	@BeforeClass
	public static void setUpOnce( ) throws Exception {

		dependency1 = finder.findInCache( httpClientGAV ).orElse( artifact1Path ).toFile( );

		dependency2 = finder.findInCache( jarexecGAV ).orElse( artifact2Path ).toFile( );

		dependency3 = finder.findInCache( jacksonGAV ).orElse( artifact3Path ).toFile( );

		dependency4 = finder.findInCache( bitcoinjGAV ).orElse( artifact4Path ).toFile( );

		dependency5 = finder.findInCache( cmnsMathGAV ).orElse( artifact5Path ).toFile( );

		dependency6 = finder.findInCache( genericsGAV ).orElse( artifact6Path ).toFile( );

		dependency7 = finder.findInCache( janericsGAV ).orElse( artifact7Path ).toFile( );
	}
	
	@Before
	public void setUp( ) {

		this.classUnderTest = new JarGatheringAbiInspector<>( );
	}

	@Test
	public void testInspectGathersOneClassForOneDependency( )
			throws IOException {

		Set<Reporting> actualReports = classUnderTest.inspect( projectClass3, new JarFile( dependency3 ) );

		Set<String> actualAnnotations = new ConcurrentSkipListSet<>( );		

		assertEquals( 1, actualReports.size( ) );

		for ( Reporting actualReport : actualReports ) {

			assertEquals( "class", actualReport.getName( ) );

			assertEquals( projectClass3.getName( ), actualReport.getType( ) );

			for ( Reporting level1Line : actualReport.getLines( ) ) {

				assertFalse( level1Line.getLines( ).isEmpty( ) );

				for ( Reporting level2Line : level1Line.getLines( ) ) {

					if ( level2Line.getName( ).equals( "annotation" ) ) {

						actualAnnotations.add( level2Line.getType( ) );
					}
				}
			}
			
			assertTrue( ( this.expectedDependenciesForClass3.containsAll( actualReport.getGAVs( )  )

			|| this.altDependenciesForClass3.containsAll( actualReport.getGAVs( ) ) ) );
		}
		assertEquals( expectedAnnotations, actualAnnotations );
	}

	@Test
	public void testInspectDoesNotGatherOneClassForOneDependency( )
			throws IOException {

		Set<Reporting> actualReports = classUnderTest
				.inspect( this.getClass( ), new JarFile( dependency1 ) );

		print( actualReports );

		assertTrue( actualReports.isEmpty( ) );
	}

	@Test
	public void testInspectGathersTwoClassesForTwoDependencies( )
			throws IOException {

		Set<Reporting> actualReports = classUnderTest
				.inspect( Set.of( projectClass1, projectClass2 ),
						Set.of( new JarFile( dependency1 ),
								new JarFile( dependency2 ) ) );

		print( actualReports );


		/** Refactored the inspection rules so that it doesn't needlessly create
		 *  report entries for types native to the library it's currently inspecting.
		 */
		assertEquals( 1, actualReports.size( ) );

		for ( Reporting actualReport : actualReports ) {

			assertFalse( actualReport.getGAVs( ).isEmpty( ) );

			for ( String aGav : actualReport.getGAVs( ) ) {
				
			    assertTrue( ( this.expectedDependenciesForClass1.contains( aGav )
						|| this.altDependenciesForClass1.contains( aGav ) ) );
			}

		}

	}

	@Test
	public void testInspectGathersThreeClassesForThreeDependencies( )
			throws IOException {

		Set<Reporting> actualReports = classUnderTest
				.inspect( Set.of( projectClass1, projectClass2, projectClass3 ),
						Set.of( new JarFile( dependency1 ),
								new JarFile( dependency2 ),
								new JarFile( dependency3 ) ) );

		print( actualReports );

		/** Refactored the inspection rules so that it doesn't needlessly create
		 *  report entries for types native to the library it's currently inspecting.
		 */
/* 		assertEquals( 2, actualReports.size( ) ); */
								
		for ( Reporting actualReport : actualReports ) {
									
			assertFalse( actualReport.getGAVs( ).isEmpty( ) );
									
			for ( String aGav : actualReport.getGAVs( ) ){

				assertTrue( ( this.expectedDependenciesForClass1.contains(aGav ) 
						|| this.altDependenciesForClass1.contains( aGav )
						|| this.expectedDependenciesForClass3.contains( aGav )
				        || this.altDependenciesForClass3.contains(aGav ) ) );
			}			
		}
	}

	@Test
	public void testInspectGathersOneClassForFourDependencies( )
			throws IOException {

		Set<Reporting> actualReports = classUnderTest
				.inspect( Set.of( projectClass4 ),
						Set.of( new JarFile( dependency4 ),
								new JarFile( dependency5 ),
								new JarFile( dependency6 ),
								new JarFile( dependency7 ) ) );

		print( actualReports );
							
		assertEquals( 1, actualReports.size( ) );
								
		for ( Reporting actualReport : actualReports ) {
									
			assertFalse( actualReport.getGAVs( ).isEmpty( ) );

/* 			assertEquals( 4, actualReport.getGAVs( ).size( ) ); */

			assertTrue(  this.expectedDependenciesForClass4.containsAll( actualReport.getGAVs( ) )  || this.altDependenciesForClass4.containsAll( actualReport.getGAVs( ) ) );						
									
		}		
	}
}