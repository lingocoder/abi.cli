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

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

// The following types can appear anywhere in the code
// but say nothing about API or implementation usage
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public class HttpClientWrapper implements Serializable, Runnable {

	private static final long serialVersionUID = -2372368351090625424L;
	private final HttpClient client; // private member: implementation details

	// HttpClient is used as a parameter of a public method
	// so "leaks" into the public API of this component
	public HttpClientWrapper( HttpClient client ) {
		this.client = client;
	}

	// public methods belongs to your API
	public byte[ ] doRawGet( String url ) {
		HttpGet request = new HttpGet( url );
		try {
			HttpEntity entity = doGet( request );
			ByteArrayOutputStream baos = new ByteArrayOutputStream( );
			entity.writeTo( baos );
			return baos.toByteArray( );
		} catch ( Exception e ) {
			ExceptionUtils.rethrow( e ); // this dependency is internal only
		} finally {
			request.releaseConnection( );
		}
		return null;
	}

	// HttpGet and HttpEntity are used in a private method, so they don't belong
	// to the API
	private HttpEntity doGet( HttpGet get ) throws Exception {
		HttpResponse response = client.execute( get );
		if ( response.getStatusLine( ).getStatusCode( ) != HttpStatus.SC_OK ) {
			System.err.println( "Method failed: " + response.getStatusLine( ) );
		}
		return response.getEntity( );
	}

	@Override
	public void run( ) {

	}
}
