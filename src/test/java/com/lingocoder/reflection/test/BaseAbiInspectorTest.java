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
package com.lingocoder.reflection.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

public class BaseAbiInspectorTest extends BaseAbiCheckerTest {

	protected File dependency1;

	protected File dependency2;

	protected File dependency3;

	protected File dependency4;

	protected File dependency5;

	protected File dependency6;

	protected File dependency7;

	protected Set<?> allDependencies = Set.of( "httpclient", "unit",
			"jackson-annotations", "commons-lang3", "jarexec.plugin",
			"bitcoinj-core-0.15", "commons-math", "janerics",
			"de.huxhorn.sulky.generics" );

	protected Set<?> expectedDependenciesForClass1 = Set.of( "httpclient" );

	protected Set<?> expectedDependenciesForClass2 = Set.of( "jarexec.plugin" );

	protected Set<?> expectedDependenciesForClass3 = Set
			.of( "jackson-annotations" );

	protected Set<?> expectedDependenciesForClass4 = Set.of(
			"bitcoinj-core-0.15", "commons-math", "janerics",
			"de.huxhorn.sulky.generics" );

	protected void assertDependenciesGrouping( Set<?> expected,
			Set<?> actual ) {

		assertTrue( actual.equals( expected ) );

		assertTrue( allDependencies.containsAll( actual ) );

		assertFalse( actual.containsAll( allDependencies ) );

	}
}