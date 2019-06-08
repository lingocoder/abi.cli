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
package com.lingocoder.abi;

import java.util.Comparator;
import java.util.Objects;

public class ReportingComparator implements Comparator<Reporting> {

    @Override
    public int compare( Reporting o1, Reporting o2 ) {
        
	    if ( Objects.equals( o1, o2 ) ) 
            return 0;
            
        if ( o1.getName( ).equals( "class" ) )
            return 1;
            
    	if (o1.getName().equals("annotations") && ( o2.getName( ).equals( "supertype" ) || o2.getName().equals("constructor")) || o2.getName().equals("method") )
            return -1;

        if (o1.getName().equals("method") && ( o2.getName( ).equals( "annotations" ) || o2.getName( ).equals( "supertype" ) || o2.getName().equals("constructor") ) )
            return 1;

        if (o1.getName().equals("constructor") && ( o2.getName( ).equals( "annotations" ) || o2.getName( ).equals( "supertype" ) ) )
            return 1;

        if (o1.getName().equals("supertype") && ( o2.getName( ).equals( "annotations" ) ) )
            return 1;

        if (o1.getName().equals("supertype") && ( o2.getName( ).equals( "method" ) ) )
            return -1;			
        if (o1.getName().equals("supertype") && ( o2.getName( ).equals( "constructor" ) ) )
            return -1;

        if ( o1.getName().equals("exception") && ( o2.getName().equals("return")  ) )
            return -1;    

        if ( o1.getName().equals("exception") && ( o2.getName().equals("param")  ) )
            return -1;    

        if ( o1.getName().equals("param") && ( o2.getName().equals("exception")  ) )
            return 1;

        if ( o1.getName().equals("param") && ( o2.getName().equals("return")  ) )
            return -1;        

        if ( o1.getName().equals("return") || o2.getName( ).equals( "return" ) )
            return 1;

        return 1;
    }
}