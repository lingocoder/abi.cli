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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import com.lingocoder.reflection.test.BaseAbiInspectorTest;

public class JarAbiInspectorTest extends BaseAbiInspectorTest {

	private AbiInspector<Set<String>, JarFile> classUnderTest;

	private File aDependency;

	private Set<String> allTypes = Set.of( String.class.getName( ),
			HttpClient.class.getName( ), new byte[ 0 ].getClass( ).getName( ),
			Runnable.class.getName( ), Object.class.getName( ),
			Serializable.class.getName( ) );

	private Set<String> expectedTypes = Set.of( HttpClient.class.getName( ) );

	public JarAbiInspectorTest( ) {
		super( );
	}

	@Before
	public void setUp( ) {

		this.classUnderTest = new JarAbiInspector( );

		this.aDependency = this.artifact1Path.toFile( );
	}

	@Test
	public void testInspectFindsPublicTypes( ) throws IOException {

		Set<String> actualTypes = classUnderTest.inspect( projectClass1,
				new JarFile( this.aDependency ) );

		assertTrue( actualTypes.equals( expectedTypes ) );

		assertTrue( allTypes.containsAll( actualTypes ) );

		assertFalse( actualTypes.containsAll( allTypes ) );
	}

	@Test
	public void testInspectDoesNotFindPublicTypes( ) throws IOException {

		Set<String> actualTypes = classUnderTest.inspect( this.getClass( ),
				new JarFile( this.aDependency ) );

		assertTrue( actualTypes.isEmpty( ) );
	}

	@Test
	public void cachingPOC( ) throws IOException {
		final String[ ] foos = new String[ 3 ];
		assertNull( foos[ 0 ] );
		Set<String> cached = Set.of( "foo" );
		JarFile givenJarFile = new JarFile( this.aDependency );
		String pathName = givenJarFile.getName( );
		final Map<String, Set<String>> mockCache = new ConcurrentHashMap<>( );
		mockCache.put( pathName, cached );
		Set<String> actual = new AbiInspector<Set<String>, JarFile>( ) {
			private Map<String, Set<String>> innerCache = mockCache;

			@Override
			public Set<String> inspect( Class<?> aProjectClass,
					JarFile aDependency ) {
				if ( innerCache.containsKey( aDependency.getName( ) ) ) {
					
				foos[ 0 ] = "SUCCESS";
					return innerCache.get( aDependency.getName( ) );
				} else {
					return classUnderTest.inspect( aProjectClass, aDependency );
				}
			}
		}.inspect(this.getClass(), givenJarFile);
		
		assertEquals( "SUCCESS", foos[ 0 ] );
		assertEquals( cached, actual );
	}
}