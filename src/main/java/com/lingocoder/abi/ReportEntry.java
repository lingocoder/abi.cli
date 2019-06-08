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
/**
 * 
 */
package com.lingocoder.abi;

import java.util.Objects;
import java.util.Set;

/**
 * @author lingocoder
 *
 */
public class ReportEntry implements Reporting {

	private static final ReportingComparator comparer = new ReportingComparator( );

	private final String name;

	private final String type;

	private final Set<Reporting> lines;

	private final Set<String> gavs;

	private int spaces = 1;

	private StringBuffer indent = new StringBuffer( );

	public ReportEntry( String name, String type, Set<Reporting> lines,
			Set<String> gavs ) {

		this.name = name;
		this.type = type;
		this.lines = lines;
		this.gavs = gavs;
	}

	@Override
	public String getName( ) {
		return this.name;
	}

	@Override
	public String getType( ) {
		return this.type;
	}

	@Override
	public Set<Reporting> getLines( ) {
		return this.lines;
	}

	public void print( ) {
		String print = indent.append( "|" ).append( '_' ).append( '_' )
				.append( " " + this.name + " : " ).append( this.type )
				.append( "\n" ).append( "|" ).toString( );

		System.out.printf( print );

		this.lines.stream( ).sequential().sorted(comparer).forEach( ntre -> {
			/* System.out.printf("%c ", '\u2502' ) */;
			ntre.setIndent( spaces + 2 );
			ntre.print( );
		} );

		if ( !this.gavs.isEmpty( ) )
			System.out.println( "\n|" + "\n|"
					+ "_________ Dependencies ________\n|" );
		this.gavs.stream( ).forEach( dep -> System.out.printf( "%s%c %s%n",
				"|", '*'/* '\u257E' */, dep ) );
		if ( !this.gavs.isEmpty( ) )
			System.out.print( "|_______________________________\n" );

	}

	public Set<String> getGAVs( ) {
		return gavs;
	}

	@Override
	public int getIndent( ) {
		return this.spaces;
	}

	@Override
	public void setIndent( int spaces ) {
		for ( int i = 1; i <= spaces; i++ ) {
			indent.append( " " );
		}
		this.spaces = spaces;
	}

	@Override
	public int hashCode( ) {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( gavs == null ) ? 0 : gavs.hashCode( ) );
		result = prime * result + ( ( lines == null ) ? 0 : lines.hashCode( ) );
		result = prime * result + ( ( name == null ) ? 0 : name.hashCode( ) );
		result = prime * result + ( ( type == null ) ? 0 : type.hashCode( ) );
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass( ) != obj.getClass( ) )
			return false;
		ReportEntry other = (ReportEntry) obj;
		if ( gavs == null ) {
			if ( other.gavs != null )
				return false;
		} else if ( !gavs.equals( other.gavs ) )
			return false;
		if ( lines == null ) {
			if ( other.lines != null )
				return false;
		} else if ( !lines.equals( other.lines ) )
			return false;
		if ( name == null ) {
			if ( other.name != null )
				return false;
		} else if ( !name.equals( other.name ) )
			return false;
		if ( type == null ) {
			if ( other.type != null )
				return false;
		} else if ( !type.equals( other.type ) )
			return false;
		return true;
	}

	@Override
	public int compareTo( Reporting that ) {

		return Objects.compare( this, that, comparer );
	}
}
