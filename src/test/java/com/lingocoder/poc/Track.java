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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude( JsonInclude.Include.NON_NULL )
@JsonPropertyOrder( { "songTitle", "albumId" } )
public class Track implements Serializable {

	private static final long serialVersionUID = -9183672746111755649L;
	/**
	 * 
	 * (Required)
	 * 
	 */
	@JsonProperty( "songTitle" )
	private String songTitle;
	/**
	 * 
	 * (Required)
	 * 
	 */
	@JsonProperty( "albumId" )
	private String albumId;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>( );

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Track( ) {
	}

	/**
	 * 
	 * @param songTitle
	 * @param albumId
	 */
	public Track( String songTitle, String albumId ) {
		super( );
		this.songTitle = songTitle;
		this.albumId = albumId;
	}

	/**
	 * 
	 * (Required)
	 * 
	 */
	@JsonProperty( "songTitle" )
	public String getSongTitle( ) {
		return songTitle;
	}

	/**
	 * 
	 * (Required)
	 * 
	 */
	@JsonProperty( "songTitle" )
	public void setSongTitle( String songTitle ) {
		this.songTitle = songTitle;
	}

	public Track withSongTitle( String songTitle ) {
		this.songTitle = songTitle;
		return this;
	}

	/**
	 * 
	 * (Required)
	 * 
	 */
	@JsonProperty( "albumId" )
	public String getAlbumId( ) {
		return albumId;
	}

	/**
	 * 
	 * (Required)
	 * 
	 */
	@JsonProperty( "albumId" )
	public void setAlbumId( String albumId ) {
		this.albumId = albumId;
	}

	public Track withAlbumId( String albumId ) {
		this.albumId = albumId;
		return this;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties( ) {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty( String name, Object value ) {
		this.additionalProperties.put( name, value );
	}

	public Track withAdditionalProperty( String name, Object value ) {
		this.additionalProperties.put( name, value );
		return this;
	}

	@Override
	public String toString( ) {
		return new ToStringBuilder( this ).append( "songTitle", songTitle )
				.append( "albumId", albumId )
				.append( "additionalProperties", additionalProperties )
				.toString( );
	}

	@Override
	public int hashCode( ) {
		return new HashCodeBuilder( ).append( songTitle ).append( albumId )
				.append( additionalProperties ).toHashCode( );
	}

	@Override
	public boolean equals( Object other ) {
		if ( other == this ) {
			return true;
		}
		if ( ( other instanceof Track ) == false ) {
			return false;
		}
		Track rhs = ( (Track) other );
		return new EqualsBuilder( ).append( songTitle, rhs.songTitle )
				.append( albumId, rhs.albumId )
				.append( additionalProperties, rhs.additionalProperties )
				.isEquals( );
	}

}
