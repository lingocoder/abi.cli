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
package net.me.consumer;

import org.apache.commons.csv.CSVFormat;

public class Main {

    public static void main(String[] args){

        // transitive dependency commons-csv is visible , as expected
        CSVFormat format = CSVFormat.DEFAULT;
        format.getHeader( );
        // commons-codec lib has not leaked, as expected:

        // cannot use Decoder class in compile scope
        //org.apache.commons.codec.Decoder decoder;

        // but available in runtime scope
        String decoderClazzName = "org.apache.commons.codec.Decoder";
        try{
            Class.forName(decoderClazzName);
            System.out.println("all fine");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
