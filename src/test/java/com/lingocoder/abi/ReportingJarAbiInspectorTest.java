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
import static com.lingocoder.poc.ProjectClass.projectClass1;
import static com.lingocoder.poc.ProjectClass.projectClass2;
import static com.lingocoder.poc.ProjectClass.projectClass3;
import static com.lingocoder.poc.ProjectClass.projectClass4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.jar.JarFile;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author lingocoder
 *
 */
public class ReportingJarAbiInspectorTest extends BaseReportingAbiInspectorTest {
	
	private AbiInspector<Reporting, Set<JarFile>> classUnderTest;

	private static File aDependency;
 
	@BeforeClass
	public static void setUpOnce( ) throws Exception {

	    aDependency = finder.findInCache( httpClientGAV ).orElse( artifact1Path ).toFile( );
		
		dependency1 = aDependency;

		dependency2 = finder.findInCache( jarexecGAV ).orElse( artifact2Path ).toFile( );

		dependency3 = finder.findInCache( jacksonGAV ).orElse( artifact3Path ).toFile( );

		dependency4 = finder.findInCache( bitcoinjGAV ).orElse( artifact4Path ).toFile( );

		dependency5 = finder.findInCache( cmnsMathGAV ).orElse( artifact5Path ).toFile( );

		dependency6 = finder.findInCache( genericsGAV ).orElse( artifact6Path ).toFile( );

		dependency7 = finder.findInCache( janericsGAV ).orElse( artifact7Path ).toFile( );

	}
	
	@Before
	public void setUp( ) throws Exception {

		this.classUnderTest = new ReportingJarAbiInspector<>( );		
	}
	
	@Test
	public void testInspectBuildsReportingStructureForOne( ) throws Exception {

		Reporting actual = this.classUnderTest.inspect( projectClass1,
				Set.of(new JarFile( aDependency ) ) );

		assertEquals( expected1.getName( ), actual.getName( ) );
		
		assertEquals( expected1.getType( ), actual.getType( ) );

		expected1.print( );

		actual.print( );

		assertFalse( actual.getLines( ).isEmpty( ) );

		assertTrue( actual.getLines( ).equals( expected1.getLines( ) ) );
	}
	
	@Test
	public void testInspectBuildsReportingStructureForFour( ) throws Exception {

		Reporting actual = this.classUnderTest.inspect( projectClass4,
				Set.of( new JarFile( dependency4 ),
						new JarFile( dependency5 ),
						new JarFile( dependency6 ),
						new JarFile( dependency7 ) ) );

		assertEquals( expected4.getName( ), actual.getName( ) );
		assertEquals( expected4.getType( ), actual.getType( ) );
		assertFalse( actual.getLines( ).isEmpty( ) );

		System.out.println( "Expected: " );
		expected4.print( );
		System.out.println( "\n\n_________________\n\nActual: " );

		actual.print( );

		assertTrue( expected4.getLines( ).containsAll( actual.getLines( ) ) );
	}


	@Test
	public void testSummarizeMapsExpectedGAVs( )
			throws IOException {

		Set<JarFile> dependencies = Set.of( new JarFile( dependency1 ),
			new JarFile( dependency2 ),
				new JarFile( dependency3 ) );

		Set<Reporting> actualReports = new HashSet<>( );

		Set.of( projectClass1, projectClass2, projectClass3 ).stream( ).forEach( aProjectClass -> {
		
		    Reporting actualReport = classUnderTest.inspect( aProjectClass, dependencies );
			actualReports.add( actualReport );

		    } );
		
		Map<String, LongAdder> actualSummary = ( (Summarizer) this.classUnderTest ).summarize( );
		
		summarize( actualSummary );

/* 		print( actualReports ); */
		Set<String> allGAVs = new HashSet<>( );
		
		actualReports.forEach( rpt -> allGAVs.addAll( rpt.getGAVs( ) ) );

		allGAVs.forEach( expectedGAV -> assertTrue( actualSummary.containsKey( expectedGAV ) ) );
		
	}
}
