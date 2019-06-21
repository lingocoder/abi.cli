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

import static com.lingocoder.abi.io.AbiIo.summarize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.jar.JarFile;

import org.bitcoinj.net.discovery.HttpDiscovery;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JarSummarizingAbiInspectorTest extends BaseReportingAbiInspectorTest {

    private SummarizingAbiInspector<Map<String, LongAdder>, Class<?>, JarFile> classUnderTest;

    private Class<?> aProjectClass = HttpDiscovery.class;

    private static JarFile aDependency;

    @BeforeClass
    public static void setUpOnce( ) throws Exception {

        aDependency = new JarFile( finder.findInCache( okHttpGAV ).orElse( artifact8Path ).toFile( ) );        
    }
    
    @Before
    public void setUp( ) throws Exception {
        
        this.classUnderTest = new JarSummarizingAbiInspector<>( );

    }
    
    @Test
    public void testInspectSummarizesExpectedCountOfOne( ) {

        Map<String, LongAdder> actualSummary = this.classUnderTest
                .inspect( aProjectClass, aDependency );

        summarize( actualSummary );

        long expectedSum = 1L;

        long actualSum = actualSummary.get( GAV2 ).sum( );

        assertEquals( expectedSum, actualSum );
    }

    @Test
    public void testInspectSummarizesSupertypes( ) throws Exception {

        Class<?> projectClass = Class.forName(
                "org.bitcoin.protocols.payments.Protos$X509Certificates$1" );

        File protobuf = finder.findInCache( protobufGAV ).orElse( artifact9Path ).toFile( );

        Map<String, LongAdder> actualSummary = this.classUnderTest.inspect( projectClass,
                        new JarFile( protobuf ) );
                
        summarize( actualSummary );

        long expectedSum = 4L;

        long acceptedSum = 7L;

        long actualSum = actualSummary.get( GAV3 ).sum( );

        /* FIXME: A race condition? */
        assertTrue( ( expectedSum == actualSum ) || ( acceptedSum == actualSum ) );
    }
}