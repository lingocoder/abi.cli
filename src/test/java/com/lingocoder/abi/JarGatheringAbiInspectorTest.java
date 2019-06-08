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

	@Before
	public void setUp( ) {

		this.classUnderTest = new JarGatheringAbiInspector<>( );

		this.dependency1 = this.artifact1Path.toFile( );

		this.dependency2 = this.artifact2Path.toFile( );

		this.dependency3 = this.artifact3Path.toFile( );

		this.dependency4 = this.artifact4Path.toFile( );

		this.dependency5 = this.artifact5Path.toFile( );

		this.dependency6 = this.artifact6Path.toFile( );

		this.dependency7 = this.artifact7Path.toFile( );
	}

	@Test
	public void testInspectGathersOneClassForOneDependency( )
			throws IOException {

		Set<Reporting> actualReports = classUnderTest
				.inspect( projectClass3, new JarFile( this.dependency3 ) );

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
				.inspect( this.getClass( ), new JarFile( this.dependency1 ) );

		print( actualReports );

		assertTrue( actualReports.isEmpty( ) );
	}

	@Test
	public void testInspectGathersTwoClassesForTwoDependencies( )
			throws IOException {

		Set<Reporting> actualReports = classUnderTest
				.inspect( Set.of( projectClass1, projectClass2 ),
						Set.of( new JarFile( this.dependency1 ),
								new JarFile( this.dependency2 ) ) );

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
						Set.of( new JarFile( this.dependency1 ),
								new JarFile( this.dependency2 ),
								new JarFile( this.dependency3 ) ) );

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
						Set.of( new JarFile( this.dependency4 ),
								new JarFile( this.dependency5 ),
								new JarFile( this.dependency6 ),
								new JarFile( this.dependency7 ) ) );

		print( actualReports );
							
		assertEquals( 1, actualReports.size( ) );
								
		for ( Reporting actualReport : actualReports ) {
									
			assertFalse( actualReport.getGAVs( ).isEmpty( ) );

/* 			assertEquals( 4, actualReport.getGAVs( ).size( ) ); */

			assertTrue(  this.expectedDependenciesForClass4.containsAll( actualReport.getGAVs( ) )  || this.altDependenciesForClass4.containsAll( actualReport.getGAVs( ) ) );						
									
		}		
	}
}