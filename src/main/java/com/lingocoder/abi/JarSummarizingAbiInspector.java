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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.jar.JarFile;

public class JarSummarizingAbiInspector<T, U, V> implements
        SummarizingAbiInspector<Map<String, LongAdder>, Class<?>, JarFile> {


    private AbiInspector<Reporting, Set<JarFile>> jarAbiInspector = new ReportingJarAbiInspector<>( );
            
    private Summarizer summarizer = (Summarizer) jarAbiInspector;

    @Override
    public Map<String, LongAdder> inspect( Set<Class<?>> projectClasses,
            Set<JarFile> dependencies ) {

        projectClasses.parallelStream( ).forEach( prjCls -> {
            this.jarAbiInspector.inspect( prjCls, dependencies );
        } );
                                
        return this.summarizer.summarize( );        
    }

    @Override
    public Map<String, LongAdder> inspect( Class<?> aProjectClass,
            JarFile aDependency ) {
        return this.inspect(Set.of(aProjectClass), Set.of(aDependency));
    }

}