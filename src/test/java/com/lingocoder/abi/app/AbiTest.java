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

import static com.lingocoder.abi.io.AbiIo.print;
import static com.lingocoder.poc.ProjectClass.projectClass4;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class AbiTest extends BaseAbiTest {

	private Abi classUnderTest;

	public AbiTest( ) {
		super( );
	}

	@Before
	public void setUp( ) {

		Configuration conf = new Configuration( this.projectClassesRoot,
				this.definedDependencies,
				new String[ ] { this.projectClassesSpecificPackage } );

		this.classUnderTest = new Abi( conf );		

		this.dependency4 = this.artifact4Path.toFile( );
	}

	@Test
	public void testInspectGroupsOneClassForFourDependencies( )
			throws IOException {

		Map<Class<?>, Set<String>> actualDependenciesMap = classUnderTest
				.inspect( /* this.args  */ );

		print( actualDependenciesMap );

		this.assertDependenciesGrouping( this.expectedDependenciesForClass4,

				actualDependenciesMap.get( projectClass4 ) );
	}
}