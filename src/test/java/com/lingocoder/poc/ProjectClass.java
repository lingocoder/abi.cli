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
package com.lingocoder.poc;

import com.lingocoder.file.CachedArtifactFinder;
import com.lingocoder.file.RepoResult;

public class ProjectClass {

	public static Class<?> projectClass1 = HttpClientWrapper.class;

	public static Class<?> projectClass2 = new CachedArtifactFinder( ) {

		@Override
		public RepoResult find( String coordinates ) {
			return null;
		}

	}.getClass( );

	public static Class<?> singleImpl = Track.class;

	public static Class<?> defaultExtends = projectClass1;

	public static Class<?> annotated = singleImpl;

	public static Class<?> projectClass3 = annotated;

	public static Class<?> projectClass4 = Frankenstein.class;

}