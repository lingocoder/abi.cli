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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lingocoder.reflection.test.BaseAbiInspectorTest;

public class JarGroupingAbiInspectorTest extends BaseAbiInspectorTest {

	private GroupingAbiInspector<Class<?>, JarFile, Set<String>> classUnderTest;

	public JarGroupingAbiInspectorTest( ) {
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

		this.classUnderTest = new JarGroupingAbiInspector<>( );
	}

	@Test
	public void testInspectGroupsOneClassForOneDependency( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( projectClass3, new JarFile( dependency3 ) );

		print( actualDependenciesMap );

		assertTrue( this.expectedDependenciesForClass3.containsAll(actualDependenciesMap.get( projectClass3 )) || this.altDependenciesForClass3.containsAll(actualDependenciesMap.get( projectClass3 )) );
		
	}

	@Test
	public void testInspectDoesNotGroupOneClassForOneDependency( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( this.getClass( ), new JarFile( dependency1 ) );

		assertTrue( actualDependenciesMap.values( ).isEmpty( ) );
	}

	@Test
	public void testInspectGroupsTwoClassesForTwoDependencies( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( Set.of( projectClass1, projectClass2 ),
						Set.of( new JarFile( dependency1 ),
								new JarFile( dependency2 ) ) );

		print( actualDependenciesMap );

		assertTrue( this.expectedDependenciesForClass1.containsAll(actualDependenciesMap.get( projectClass1 )) || this.altDependenciesForClass1.containsAll(actualDependenciesMap.get( projectClass1 )) );
						
		assertTrue( this.expectedDependenciesForClass2.containsAll(actualDependenciesMap.get( projectClass2 )) || this.altDependenciesForClass2.containsAll(actualDependenciesMap.get( projectClass2 )) );
						
	}

	@Test
	public void testInspectGroupsThreeClassesForThreeDependencies( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( Set.of( projectClass1, projectClass2, projectClass3 ),
						Set.of( new JarFile( dependency1 ),
								new JarFile( dependency2 ),
								new JarFile( dependency3 ) ) );

		print( actualDependenciesMap );

		assertTrue( this.expectedDependenciesForClass1.containsAll(actualDependenciesMap.get( projectClass1 )) || this.altDependenciesForClass1.containsAll(actualDependenciesMap.get( projectClass1 )) );

		assertTrue( this.expectedDependenciesForClass2.containsAll(actualDependenciesMap.get( projectClass2 )) || this.altDependenciesForClass2.containsAll(actualDependenciesMap.get( projectClass2 )) );

		assertTrue( this.expectedDependenciesForClass3.containsAll(actualDependenciesMap.get( projectClass3 )) || this.altDependenciesForClass3.containsAll(actualDependenciesMap.get( projectClass3 )) );

	}

	@Test
	public void testInspectGroupsOneClassForFourDependencies( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( Set.of( projectClass4 ),
						Set.of( new JarFile( dependency4 ),
								new JarFile( dependency5 ),
								new JarFile( dependency6 ),
								new JarFile( dependency7 ) ) );

		print( actualDependenciesMap );

		assertTrue( this.expectedDependenciesForClass4.containsAll(actualDependenciesMap.get( projectClass4 )) || this.altDependenciesForClass4.containsAll(actualDependenciesMap.get( projectClass4 )) );
	}
}