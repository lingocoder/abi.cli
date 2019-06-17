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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import com.lingocoder.file.CachedArtifactFinder;
import com.lingocoder.poc.Frankenstein;
import com.lingocoder.poc.HttpClientWrapper;
import com.lingocoder.poc.ProjectClass;
import com.lingocoder.poc.Track;

public class BaseAbiCheckerTest {

	protected Path artifact1Path;

	protected Path artifact2Path;

	protected Path artifact3Path;

	protected Path artifact4Path;

	protected Path artifact5Path;

	protected Path artifact6Path;

	protected Path artifact7Path;

	protected Path artifact8Path;

	protected Path artifact9Path;

	protected Set<Path> allDependencPaths;

	protected Set<Class<?>> expectedProjectClasses = Set.of( ProjectClass.class,
			HttpClientWrapper.class, Frankenstein.class, Track.class,
			CachedArtifactFinder.class );

	protected Path projectClassesRoot = Paths
			.get( System.getProperty( "user.dir" ), "bin", "test" );
	protected String projectClassesBasePackage = "com.lingocoder";
	protected String projectClassesSpecificPackage = "com.lingocoder.poc";

	protected Path scanPath = projectClassesRoot.resolve( "com" )
			.resolve( "lingocoder" );
	protected Path pocPkgPath = scanPath.resolve( "poc" );
	protected Set<Path> expectedProjectClassesPaths = Set.of(
			pocPkgPath.resolve( "ProjectClass.class" ),
			pocPkgPath.resolve( "HttpClientWrapper.class" ),
			pocPkgPath.resolve( "Frankenstein.class" ),
			pocPkgPath.resolve( "Track.class" ),
			pocPkgPath.resolve( "ProjectClass$1.class" ) );

	protected Set<String> definedDependencies = Set.of(
			"org.bitcoinj:bitcoinj-core:0.15-SNAPSHOT",
			"org.apache.commons:commons-math:2.2",
			"jp.dodododo.janerics:janerics:1.0.1",
			"de.huxhorn.sulky:de.huxhorn.sulky.generics:8.2.0",
			"org.apache.httpcomponents:httpclient:4.5.3",
			"com.fasterxml.jackson.core:jackson-annotations:2.9.8",
			/* "org.apache.commons:commons-lang3:3.5", */"com.lingocoder:jarexec.plugin:0.3" );

	protected BaseAbiCheckerTest( ) {

		this.artifact1Path = Paths.get(
				System.getenv("M2_REPO"), "/org/apache/httpcomponents/httpclient/4.5.3/httpclient-4.5.3.jar" );

		this.artifact2Path = Paths.get(
				System.getenv("GRADLE_USER_HOME"), "/caches/modules-2/files-2.1/com.lingocoder/jarexec.plugin/0.3/3ca2b5e7a5657e9bca12ffc4908bd34ccd64f1aa/jarexec.plugin-0.3.jar" );

		this.artifact3Path = Paths.get(
				System.getenv("M2_REPO"), "/com/fasterxml/jackson/core/jackson-annotations/2.9.8/jackson-annotations-2.9.8.jar" );

		this.artifact4Path = Paths.get(
				System.getenv("M2_REPO"), "/org/bitcoinj/bitcoinj-core/0.15-SNAPSHOT/bitcoinj-core-0.15-SNAPSHOT.jar" );

		this.artifact5Path = Paths.get(
				System.getenv("M2_REPO"), "/org/apache/commons/commons-math/2.2/commons-math-2.2.jar" );

		this.artifact6Path = Paths.get(
				System.getenv("GRADLE_USER_HOME"), "/caches/modules-2/files-2.1/de.huxhorn.sulky/de.huxhorn.sulky.generics/8.2.0/6821b3791399d14c3e03aaededa95069feca591/de.huxhorn.sulky.generics-8.2.0.jar" );

		this.artifact7Path = Paths.get(
				System.getenv( "GRADLE_USER_HOME" ), "/caches/modules-2/files-2.1/jp.dodododo.janerics/janerics/1.0.1/bb363a3612c390cc7708b2f33a5133963a2ac486/janerics-1.0.1.jar" );

		this.artifact8Path = Paths.get(
				System.getenv( "GRADLE_USER_HOME" ), "/caches/modules-2/files-2.1/com.squareup.okhttp3/okhttp/3.12.3/4a1c4c7b89298e7a00e83e4b3bc75af9cc6307f1/okhttp-3.12.3.jar" );
		
		this.artifact9Path = Paths.get(
			System.getenv( "GRADLE_USER_HOME" ), "/caches/modules-2/files-2.1/com.google.protobuf/protobuf-java/3.7.1/bce1b6dc9e4531169542ab37a1c8641bcaa8afb/protobuf-java-3.7.1.jar");		
		this.allDependencPaths = Set.of( this.artifact1Path, this.artifact2Path,
				this.artifact3Path, this.artifact4Path, this.artifact5Path,
				this.artifact6Path, this.artifact7Path, this.artifact8Path );
	}
}