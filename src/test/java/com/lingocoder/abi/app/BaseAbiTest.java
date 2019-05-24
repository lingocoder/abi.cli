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

import java.util.ArrayList;
import java.util.List;

import com.lingocoder.reflection.test.BaseAbiInspectorTest;

public class BaseAbiTest extends BaseAbiInspectorTest {

	protected List<String> argList = new ArrayList<>( );

	protected String[ ] args;

	protected BaseAbiTest( ) {

		this.argList.add( 0,
				this.projectClassesRoot.toAbsolutePath( ).toString( ) );

		this.argList.add( 1, this.projectClassesSpecificPackage );

		this.argList.addAll( 2, this.definedDependencies );

		this.args = argList
				.toArray( new String[ this.definedDependencies.size( ) ] );
	}

}