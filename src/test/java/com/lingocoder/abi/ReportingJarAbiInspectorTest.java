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

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.jar.JarFile;

import org.junit.Before;
import org.junit.Test;

/**
 * @author lingocoder
 *
 */
public class ReportingJarAbiInspectorTest extends BaseReportingAbiInspectorTest {
	
	private AbiInspector<Reporting, Set<JarFile>> classUnderTest;

	private String bitcoinjGAV = "org.bitcoinj:bitcoinj-core:0.15-SNAPSHOT";

	private String cmnsMathGAV = "org.apache.commons:commons-math:2.2";
 
	@Before
	public void setUp( ) throws Exception {

		this.classUnderTest = new ReportingJarAbiInspector<>( );
		
		this.aDependency = this.finder.findInCache(  "org.apache.httpcomponents:httpclient:4.5.3" ).orElse( this.artifact1Path ).toFile( );
		
		this.dependency1 = this.aDependency;

		this.dependency2 = this.finder.findInCache(  "com.lingocoder:jarexec.plugin:0.3" ).orElse( this.artifact2Path ).toFile( );

		this.dependency3 = this.finder.findInCache(  "com.fasterxml.jackson.core:jackson-annotations:2.9.8" ).orElse( this.artifact3Path ).toFile( );

		this.dependency4 = this.finder.findInCache(  bitcoinjGAV ).orElse( this.artifact4Path ).toFile( );

		this.dependency5 = this.finder.findInCache(  cmnsMathGAV ).orElse( this.artifact5Path ).toFile( );

		this.dependency6 = this.finder.findInCache(  "de.huxhorn.sulky:de.huxhorn.sulky.generics:8.2.0" ).orElse( this.artifact6Path ).toFile( );

		this.dependency7 = this.finder.findInCache(  "jp.dodododo.janerics:janerics:1.0.1" ).orElse( this.artifact7Path ).toFile( );

	}
	
	@Test
	public void testInspectBuildsReportingStructureForOne( ) throws Exception {

		Reporting actual = this.classUnderTest.inspect( projectClass1,
				Set.of(new JarFile( this.aDependency ) ) );

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
				Set.of( new JarFile( this.dependency4 ),
						new JarFile( this.dependency5 ),
						new JarFile( this.dependency6 ),
						new JarFile( this.dependency7 ) ) );

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

		Set<JarFile> dependencies = Set.of( new JarFile( this.dependency1 ),
			new JarFile( this.dependency2 ),
				new JarFile( this.dependency3 ) );

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
