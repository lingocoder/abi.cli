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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import com.lingocoder.abi.GatheringAbiInspector;
import com.lingocoder.abi.GroupingAbiInspector;
import com.lingocoder.abi.JarGatheringAbiInspector;
import com.lingocoder.abi.JarGroupingAbiInspector;
import com.lingocoder.abi.JarSummarizingAbiInspector;
import com.lingocoder.abi.Reporting;
import com.lingocoder.abi.SummarizingAbiInspector;
import com.lingocoder.classic.Classifier;
import com.lingocoder.file.Lookup;
import com.lingocoder.jar.JarFiler;

import static com.lingocoder.abi.app.Configuration.*;

public class Abi {

	private final GroupingAbiInspector<Class<?>, JarFile, Set<String>> abi;
	private final GatheringAbiInspector<Reporting, Class<?>, JarFile> abI;
	private final SummarizingAbiInspector<Map<String, LongAdder>, Class<?>, JarFile> aBi;

	private Path classesDir;

	private String packageToScan;

	private String[] packagesToScan;


	private final Classifier classifier;

	private final Lookup<String> finder;

	private final Configuration conf;

	public Abi( Configuration conf ) {

		this.conf = conf;
	
		this.abi = new JarGroupingAbiInspector<>( );
		this.abI = new JarGatheringAbiInspector<>( );
		this.aBi = new JarSummarizingAbiInspector<>( );

		this.classesDir = DEFAULT_CLASSES_DIR;
		
		this.classifier = new Classifier( );

		this.packagesToScan = conf.getPackagesToScan( );
		
		this.finder = new Lookup<>( this.conf.getDependencies( ) );

	}
 
	public Abi( Set<String> dependencies ) {

		this.abi = new JarGroupingAbiInspector<>( );
		this.abI = new JarGatheringAbiInspector<>( );
		this.aBi = new JarSummarizingAbiInspector<>( );

		this.classesDir = DEFAULT_CLASSES_DIR;

		this.packageToScan = "";

		this.classifier = new Classifier( );

		this.finder = new Lookup<>( dependencies );

		this.conf = new Configuration(classesDir, null, new String[]{this.packageToScan}, false, false );
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

	protected Map<Class<?>, Set<String>> inspect( ) {

		Set<JarFile> dependencies = this.resolveDependencies( );

		this.classesDir = conf.getClassesDir( );

		this.packagesToScan = conf.getPackagesToScan( );

		Set<Class<?>> projectClasses = this.classifier
				.classify( this.classesDir, this.packagesToScan );

		return this.abi.inspect( projectClasses, dependencies );
	}

	protected Set<Reporting> nspect( ) {

		Set<JarFile> dependencies = this.resolveDependencies( );

		this.classesDir = conf.getClassesDir( );

		this.packagesToScan = conf.getPackagesToScan( );

		Set<Class<?>> projectClasses = this.classifier
				.classify( this.classesDir, this.packagesToScan );

		return this.abI.inspect( projectClasses, dependencies );
	}

	protected Map<String, LongAdder> nschpect( ) {

		Set<JarFile> dependencies = this.resolveDependencies( );

		this.classesDir = conf.getClassesDir( );

		this.packagesToScan = conf.getPackagesToScan( );

		Set<Class<?>> projectClasses = this.classifier
				.classify( this.classesDir, this.packagesToScan );

		return this.aBi.inspect( projectClasses, dependencies );
	}

	private Set<JarFile> resolveDependencies( ) {
		return this.finder.findInCache( conf.getGavs( ) ).parallelStream( )
				.map( path -> path.toFile( ) ).map( JarFiler::toJarFile )
				.filter( jar -> jar != null ).collect( Collectors.toSet( ) );
	}	
}