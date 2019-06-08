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

	protected Set<?> allDependencies = Set.of( "org.apache.httpcomponents:httpclient:4.5.3", "junit:junit:4.12",
			"com.fasterxml.jackson.core:jackson-annotations:2.9.8", "org.apache.commons:commons-lang3:3.5", "com.lingocoder:jarexec.plugin:0.3",
			"org.bitcoinj:bitcoinj-core:0.15-SNAPSHOT", "org.apache.commons:commons-math:2.2", "jp.dodododo.janerics:janerics:1.0.1",
			"de.huxhorn.sulky:de.huxhorn.sulky.generics:8.2.0" );

	protected Set<?> expectedDependenciesForClass1 = Set.of( "org.apache.httpcomponents:httpclient:4.5.3" );

	protected Set<?> expectedDependenciesForClass2 = Set.of( "com.lingocoder:jarexec.plugin:0.3" );

	protected Set<?> expectedDependenciesForClass3 = Set
			.of( "com.fasterxml.jackson.core:jackson-annotations:2.9.8" );

	protected Set<?> expectedDependenciesForClass4 = Set.of(
			"org.bitcoinj:bitcoinj-core:0.15-SNAPSHOT", "org.apache.commons:commons-math:2.2", "jp.dodododo.janerics:janerics:1.0.1",
			"de.huxhorn.sulky:de.huxhorn.sulky.generics:8.2.0" );

	protected Set<?> altDependencies = Set.of( "httpclient", "unit",
	"jackson-annotations", "commons-lang3", "jarexec.plugin",
	"bitcoinj-core-0.15", "commons-math", "janerics",
	"de.huxhorn.sulky.generics" );
	
	protected Set<?> altDependenciesForClass1 = Set.of( "httpclient" );
	
	protected Set<?> altDependenciesForClass2 = Set.of( "jarexec.plugin" );
	
	protected Set<?> altDependenciesForClass3 = Set
	.of( "jackson-annotations" );
	
	protected Set<?> altDependenciesForClass4 = Set.of(
	"bitcoinj-core-0.15", "commons-math", "janerics", "de.huxhorn.sulky.generics" );			

	protected void assertDependenciesGrouping( Set<?> expected,
			Set<?> actual ) {

		assertTrue( actual.equals( expected ) );

		assertTrue( allDependencies.containsAll( actual ) );

		assertFalse( actual.containsAll( allDependencies ) );

	}	
	protected void assertExpectedGAVs( Set<?> expected,Set<?> alternative,
			Set<?> actual ) {

		assertTrue( actual.equals( expected ) || actual.equals(alternative) );

		assertTrue( expected.containsAll( actual ) || alternative.containsAll( actual ) );

		assertFalse( actual.containsAll( expected ) );

		assertFalse( actual.containsAll( alternative ) );
	}
}