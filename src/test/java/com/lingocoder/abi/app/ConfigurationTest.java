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
package com.lingocoder.abi.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationTest extends BaseAbiTest {

	public static final Logger LOG = LoggerFactory
			.getLogger( "ConfigurationTest" );

	private Configuration classUnderTest;

	public ConfigurationTest( ) {
		super( );
	}

	@Before
	public void setUp( ) {
		this.classUnderTest = new Configuration( this.projectClassesRoot,
				this.definedDependencies, new String[ ] { this.projectClassesSpecificPackage } );
	}

	@Test
	public void testConfigureMapsArrayToValidAppInput( ) {

		Set<String> actualDepedencyList = this.classUnderTest.getGavs( );

		assertEquals( this.definedDependencies.size( ),
				actualDepedencyList.size( ) );

		assertEquals( this.definedDependencies, actualDepedencyList );

		assertEquals( this.projectClassesRoot,
				this.classUnderTest.getClassesDir( ) );

		assertEquals( this.projectClassesSpecificPackage,
				this.classUnderTest.getPackagesToScan( )[ 0 ] );
	}
}