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
import org.junit.Test;

import com.lingocoder.reflection.test.BaseAbiInspectorTest;

public class JarGroupingAbiInspectorTest extends BaseAbiInspectorTest {

	private GroupingAbiInspector<Class<?>, JarFile> classUnderTest;

	public JarGroupingAbiInspectorTest( ) {
		super( );
	}

	@Before
	public void setUp( ) {

		this.classUnderTest = new JarGroupingAbiInspector<>( );

		this.dependency1 = this.artifact1Path.toFile( );

		this.dependency2 = this.artifact2Path.toFile( );

		this.dependency3 = this.artifact3Path.toFile( );

		this.dependency4 = this.artifact4Path.toFile( );

		this.dependency5 = this.artifact5Path.toFile( );

		this.dependency6 = this.artifact6Path.toFile( );

		this.dependency7 = this.artifact7Path.toFile( );

	}

	@Test
	public void testInspectGroupsOneClassForOneDependency( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( projectClass3, new JarFile( this.dependency3 ) );

		this.assertDependenciesGrouping( this.expectedDependenciesForClass3,
				actualDependenciesMap.get( projectClass3 ) );
	}

	@Test
	public void testInspectDoesNotGroupOneClassForOneDependency( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( this.getClass( ), new JarFile( this.dependency1 ) );

		assertTrue( actualDependenciesMap.values( ).isEmpty( ) );
	}

	@Test
	public void testInspectGroupsTwoClassesForTwoDependencies( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( Set.of( projectClass1, projectClass2 ),
						Set.of( new JarFile( this.dependency1 ),
								new JarFile( this.dependency2 ) ) );

		this.assertDependenciesGrouping( this.expectedDependenciesForClass1,
				actualDependenciesMap.get( projectClass1 ) );

		this.assertDependenciesGrouping( this.expectedDependenciesForClass2,
				actualDependenciesMap.get( projectClass2 ) );

		print( actualDependenciesMap );
	}

	@Test
	public void testInspectGroupsThreeClassesForThreeDependencies( )
			throws IOException {

		/*
		 * GroupingAbiInspector<Class<?>, JarFile> classUnderTest =
		 * (GroupingAbiInspector<Class<?>, JarFile>) this.classUnderTest;
		 */

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( Set.of( projectClass1, projectClass2, projectClass3 ),
						Set.of( new JarFile( this.dependency1 ),
								new JarFile( this.dependency2 ),
								new JarFile( this.dependency3 ) ) );

		this.assertDependenciesGrouping( this.expectedDependenciesForClass1,
				actualDependenciesMap.get( projectClass1 ) );

		this.assertDependenciesGrouping( this.expectedDependenciesForClass2,
				actualDependenciesMap.get( projectClass2 ) );

		this.assertDependenciesGrouping( this.expectedDependenciesForClass3,
				actualDependenciesMap.get( projectClass3 ) );

		print( actualDependenciesMap );
	}

	@Test
	public void testInspectGroupsOneClassForFourDependencies( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( Set.of( projectClass4 ),
						Set.of( new JarFile( this.dependency4 ),
								new JarFile( this.dependency5 ),
								new JarFile( this.dependency6 ),
								new JarFile( this.dependency7 ) ) );

		print( actualDependenciesMap );
		this.assertDependenciesGrouping( this.expectedDependenciesForClass4,
				actualDependenciesMap.get( projectClass4 ) );
	}
}