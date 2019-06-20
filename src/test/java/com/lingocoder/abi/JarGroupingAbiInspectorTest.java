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

	private GroupingAbiInspector<Class<?>, JarFile, Set<String>> classUnderTest;

	public JarGroupingAbiInspectorTest( ) {
		super( );
	}

	@Before
	public void setUp( ) {

		this.classUnderTest = new JarGroupingAbiInspector<>( );

		this.dependency1 = this.finder.findInCache(  "org.apache.httpcomponents:httpclient:4.5.3" ).orElse( this.artifact1Path ).toFile( );

		this.dependency2 = this.finder.findInCache(  "com.lingocoder:jarexec.plugin:0.3" ).orElse( this.artifact2Path ).toFile( );

		this.dependency3 = this.finder.findInCache(  "com.fasterxml.jackson.core:jackson-annotations:2.9.8" ).orElse( this.artifact3Path ).toFile( );

		this.dependency4 = this.finder.findInCache(  "org.bitcoinj:bitcoinj-core:0.15-SNAPSHOT" ).orElse( this.artifact4Path ).toFile( );

		this.dependency5 = this.finder.findInCache(  "org.apache.commons:commons-math:2.2" ).orElse( this.artifact5Path ).toFile( );

		this.dependency6 = this.finder.findInCache(  "de.huxhorn.sulky:de.huxhorn.sulky.generics:8.2.0" ).orElse( this.artifact6Path ).toFile( );

		this.dependency7 = this.finder.findInCache(  "jp.dodododo.janerics:janerics:1.0.1" ).orElse( this.artifact7Path ).toFile( );

	}

	@Test
	public void testInspectGroupsOneClassForOneDependency( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( projectClass3, new JarFile( this.dependency3 ) );

		print( actualDependenciesMap );

		assertTrue( this.expectedDependenciesForClass3.containsAll(actualDependenciesMap.get( projectClass3 )) || this.altDependenciesForClass3.containsAll(actualDependenciesMap.get( projectClass3 )) );
		
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

		print( actualDependenciesMap );

		assertTrue( this.expectedDependenciesForClass1.containsAll(actualDependenciesMap.get( projectClass1 )) || this.altDependenciesForClass1.containsAll(actualDependenciesMap.get( projectClass1 )) );
						
		assertTrue( this.expectedDependenciesForClass2.containsAll(actualDependenciesMap.get( projectClass2 )) || this.altDependenciesForClass2.containsAll(actualDependenciesMap.get( projectClass2 )) );
						
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
						Set.of( new JarFile( this.dependency4 ),
								new JarFile( this.dependency5 ),
								new JarFile( this.dependency6 ),
								new JarFile( this.dependency7 ) ) );

		print( actualDependenciesMap );

		assertTrue( this.expectedDependenciesForClass4.containsAll(actualDependenciesMap.get( projectClass4 )) || this.altDependenciesForClass4.containsAll(actualDependenciesMap.get( projectClass4 )) );
	}
}