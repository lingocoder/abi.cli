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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import com.lingocoder.abi.GroupingAbiInspector;
import com.lingocoder.abi.JarGroupingAbiInspector;
import com.lingocoder.classic.Classifier;
import com.lingocoder.file.Lookup;
import com.lingocoder.jar.JarFiler;

public class Abi {

	private static final Path DEFAULT_CLASSES_DIR = Paths.get(
			System.getProperty( "user.dir" ), "build", "classes", "java",
			"main" );

	private final GroupingAbiInspector<Class<?>, JarFile> abi;

	private Path classesDir;

	private String packageToScan;


	private final Classifier classifier;

	private final Lookup<String> finder;

	private final static Configuration conf = new Configuration( );
 
	public Abi( Set<String> dependencies ) {

		this.abi = new JarGroupingAbiInspector<>( );

		this.classesDir = DEFAULT_CLASSES_DIR;

		this.packageToScan = "";

		this.classifier = new Classifier( );

		this.finder = new Lookup<>( dependencies );

		
	}

	public Abi( ) {
		this( null );
	}

	protected Path getClassesDir( ) {
		return classesDir;
	}

	protected void setClassesDir( Path classesDir ) {
		this.classesDir = classesDir;
	}

	protected String getPackageToScan( ) {
		return packageToScan;
	}

	protected void setPackageToScan( String packageToScan ) {
		this.packageToScan = packageToScan;
	}

	protected Map<Class<?>, Set<String>> inspect( String... args ) {

		Set<JarFile> dependencies = this.finder
				.findInCache( conf.configure( args ) ).stream( )
				.map( path -> path.toFile( ) ).map( JarFiler::toJarFile )
				.filter( jar -> jar != null ).collect( Collectors.toSet( ) );

		this.classesDir = conf.getClassesDir( );

		this.packageToScan = conf.getPackageToScan( );

		Set<Class<?>> projectClasses = this.classifier
				.classify( this.classesDir, this.packageToScan );

		return this.abi.inspect( projectClasses, dependencies );
	}
}