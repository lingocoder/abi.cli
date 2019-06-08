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
import static com.lingocoder.poc.ProjectClass.projectClass1;
import static com.lingocoder.poc.ProjectClass.projectClass4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.jar.JarFile;

import org.junit.Before;
import org.junit.Test;

/**
 * @author lingocoder
 *
 */
public class ReportingJarAbiInspectorTest extends BaseReportingAbiInspectorTest {
	
	private AbiInspector<Reporting, Set<JarFile>> classUnderTest;

	@Before
	public void setUp( ) throws Exception {

		this.classUnderTest = new ReportingJarAbiInspector<>( );

		this.aDependency = this.artifact1Path.toFile( );

		this.dependency4 = this.artifact4Path.toFile( );
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
				Set.of( new JarFile( this.artifact4Path.toFile( ) ),
						new JarFile( this.artifact5Path.toFile( ) ),
						new JarFile( this.artifact6Path.toFile( ) ),
						new JarFile( this.artifact7Path.toFile( ) ) ) );

		assertEquals( expected4.getName( ), actual.getName( ) );
		assertEquals( expected4.getType( ), actual.getType( ) );
		assertFalse( actual.getLines( ).isEmpty( ) );

		System.out.println( "Expected: " );
		expected4.print( );
		System.out.println( "\n\n_________________\n\nActual: " );

		actual.print( );

		assertTrue(  expected4.getLines( ).containsAll( actual.getLines( ) ) );
	}
}
