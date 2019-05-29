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
import java.util.Set;
import com.lingocoder.classic.ClassPathFilter;

public class Configuration {

	public static final Path DEFAULT_CLASSES_DIR = Paths.get(
			System.getProperty( "user.dir" ), "build", "classes", "java",
			"main" );
			
	private final Path classesDir;

	private final Set<String> gavs;

	private final Set<String> dependencies;

	private final String[ ] packagesToScan;

	private final ClassPathFilter cpFilter = new ClassPathFilter( );

	public Configuration( Path classesDir, Set<String> gavs,
			String[ ] packagesToScan ) {
		
		this.classesDir = classesDir;

		this.gavs = gavs;

		this.packagesToScan = packagesToScan;
		
		this.dependencies = this.cpFilter.filterClassPath( gavs );		
	}

	protected Path getClassesDir( ) {
		return this.classesDir;
	}

	public Set<String> getGavs( ) {
		return gavs;
	}

	public String[ ] getPackagesToScan( ) {
		return packagesToScan;
	}

	public Set<String> getDependencies( ) {
		return dependencies;
	}
}