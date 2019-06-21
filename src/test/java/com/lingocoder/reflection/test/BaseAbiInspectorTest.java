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
import java.util.HashSet;
import com.lingocoder.classic.ClassPathFilter;
import com.lingocoder.file.Lookup;

public class BaseAbiInspectorTest extends BaseAbiCheckerTest {

	protected static File dependency1;

	protected static File dependency2;

	protected static File dependency3;

	protected static File dependency4;

	protected static File dependency5;

	protected static File dependency6;

	protected static File dependency7;

	protected static Set<String> allDependencies = Set.of( httpClientGAV, junitGAV,
			jacksonGAV, commonsLang3GAV, jarexecGAV,
			bitcoinjGAV, cmnsMathGAV, janericsGAV,
			genericsGAV, okHttpGAV, 
			protobufGAV,guavaGAV, bitcoinj16GAV );

	protected Set<?> expectedDependenciesForClass1 = Set.of( httpClientGAV );

	protected Set<?> expectedDependenciesForClass2 = Set.of( jarexecGAV );

	protected Set<?> expectedDependenciesForClass3 = Set
			.of( jacksonGAV );

	protected Set<?> expectedDependenciesForClass4 = Set.of(
			bitcoinjGAV, cmnsMathGAV, janericsGAV,
			genericsGAV );

	protected Set<?> altDependencies = Set.of( "httpclient", "unit",
	"jackson-annotations", "commons-lang3", "jarexec.plugin",
	"bitcoinj-core-0.15", "commons-math", "janerics",
	"de.huxhorn.sulky.generics" );
	
	protected Set<?> altDependenciesForClass1 = Set.of( "httpclient" );
	
	protected Set<?> altDependenciesForClass2 = Set.of( "jarexec.plugin" );
	
	protected Set<?> altDependenciesForClass3 = Set
	.of( "jackson-annotations" );
	
	protected Set<?> altDependenciesForClass4 = Set.of(
			"bitcoinj-core-0.15", "commons-math", "janerics",
			"de.huxhorn.sulky.generics" );

	protected static boolean isOnUnix = !System.getProperty("os.name").contains("Windows");  		

    protected static final String GAV1;
    protected static final String GAV2;    
    protected static final String GAV3;
	protected static final String GAV4;	   
	protected static final String GAV5;
	protected static final String GAV6;

    protected static ClassPathFilter cpFilter;
    protected static Set<String> gavs;
	protected static Set<String> dependencies;
	protected static Lookup<String> finder;
	
	static { 

		GAV1 = isOnUnix ? "commons-math" : cmnsMathGAV;
		GAV2 = isOnUnix ? "okhttp" : okHttpGAV;    
		GAV3 = isOnUnix ? "protobuf-java" : protobufGAV;
		GAV4 = isOnUnix ? "guava-27.1" : guavaGAV;	   
		GAV5 = isOnUnix ? "bitcoinj-core" : bitcoinjGAV;
		GAV6 = isOnUnix ? GAV5 : bitcoinj16GAV;
		
		gavs = new HashSet<>( );		
		gavs.addAll( allDependencies );
		cpFilter = new ClassPathFilter( );
		dependencies = cpFilter.filterClassPath( gavs );
		finder = new Lookup<>( dependencies );
	}	

	protected BaseAbiInspectorTest( ) {
				
		this.allDependencPaths = Set.of( artifact1Path, artifact2Path,
		artifact3Path, artifact4Path, artifact5Path,
		artifact6Path, artifact7Path, artifact8Path );
	}
	
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