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
package com.lingocoder.reflection;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;

public class ReflectionHelper {

	public static Set<String> ignoreJdk = Set.of( "java.lang", "sun", "com.sun",
			"javax", "java", "jdk", "java.util", "java.io", "javax.annotation",
			"java.net", "java.nio", "java.math", "java.lang.Object",
			"java.security", "java.sql", "java.text", "javax.net", "[Ljava.lang", "[Lsun", "[Lcom.sun",
			"[Ljavax", "[Ljava", "[Ljdk", "[Ljava.util", "[Ljava.io", "[Ljavax.annotation",
			"[Ljava.net", "[Ljava.nio", "[Ljava.math", "[Ljava.lang.Object",
			"[Ljava.security", "[Ljava.sql", "[Ljava.text", "[Ljavax.net" );

	protected static final Set<String> primitiveArray = Set.of( "char[]", "short[]", "byte[]",
	"int[]", "long[]", "float[]", "double[]", "char", "short", "byte",
	"int", "long", "float", "double", "boolean", "[B", "[C", "[D", "[F", "[I", "[J", "[S", "[Z" );


	protected static final Set<String> projPkgs = new ConcurrentSkipListSet<>( );

	public static boolean notJdk( Class<?> aClass ) {

        return notJdk( aClass.getName( ) );
	}

	public static boolean notJdk(String aClassName ) {

		return !in( aClassName, ignoreJdk ) && !in( aClassName, primitiveArray );

	}
	public static Set<String> permutate( String aClassName ) {

		Set<String> permutations = new ConcurrentSkipListSet<>( );
		permutations.add( aClassName );
		int length = aClassName.split("\\.").length;
		int from = aClassName.contains(".") ? (length > 1 ? 2 : 1) : 1;
		for ( int i = from; i < length; i++ ) {
			permutations.add( permuteate(aClassName, i) );
		}
/* 		int lastDotIdx = aClassName.lastIndexOf( "." );
		while ( lastDotIdx > -1 ) {
			aClassName = aClassName.substring( 0, lastDotIdx );
			permutations.add( aClassName );
			lastDotIdx = aClassName.lastIndexOf( "." );
		}
 */		return permutations;

	}

	static String permuteate( String aClassName, int levels ) {
		String[ ] pkgAry = aClassName.split( "\\." );

		int end = levels - 2;
		if ( end > pkgAry.length ) {
			throw new IllegalArgumentException( "'" + levels
					+ "' levels is more than we got. Try again with something <= '"
					+ pkgAry.length + "'" );
		}
		StringBuffer pkg = new StringBuffer( pkgAry[ 0 ] );

		for ( int i = 1; i < levels; i++ ) {
			pkg.append( "." ).append( pkgAry[ i ] );
		}
		return pkg.toString( ) ;
	}

	public static String packagate( String aClassName ) {
		String packagated = aClassName.contains( "." )
				? aClassName.substring( 0, aClassName.lastIndexOf( "." ) )
				: aClassName;

/* 		System.out.println( "packagating '" + packagated + "'" ); */
		return packagated;

	}

	public static boolean in( Class<?> aClass, Set<String> ignore ) {

		return in( aClass.getName( ), ignore );

	}
	
	public static boolean in( String aClassName, Set<String> ignore ) {

		Set<String> permutations = permutate( aClassName );

		for ( String aPkg : permutations ) {

			if ( ignore.contains( aPkg ) ) {

				return true;
			}
		}
		return false;
	}

	/**
	 * Prepare all the types from the project that will be filtered out of the analysis process.
	 * 
	 * @param projTypes The {@link Set} of types used in the project being analyzed.
	 */
	public static void prime( Set<Class<?>> projTypes ) {
		
		projTypes.parallelStream().forEach( type -> projPkgs.addAll( permutate( type.getName( ) ) ) );
        
		projTypes.parallelStream().forEach( type -> projPkgs.addAll( permutate( "[L" + type.getName( ) ) ) );        
        
	}
}