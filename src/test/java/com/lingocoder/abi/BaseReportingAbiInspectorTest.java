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

import static com.lingocoder.poc.ProjectClass.projectClass1;
import static com.lingocoder.poc.ProjectClass.projectClass4;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import com.lingocoder.reflection.test.BaseAbiInspectorTest;

import org.apache.commons.math.random.EmpiricalDistribution;
import org.apache.http.client.HttpClient;
import org.bitcoinj.core.Coin;
import org.bitcoinj.wallet.CoinSelection;

import de.huxhorn.sulky.generics.Wrapper;
import jp.dodododo.janerics.exception.UnsupportedTypeException;

public class BaseReportingAbiInspectorTest extends BaseAbiInspectorTest {
    
	protected Reporting expectedParam1 = new ReportEntry("param", HttpClient.class.getName(), Collections.emptySet(), Collections.emptySet());
	protected Reporting expectedConstructor1 = new ReportEntry("constructor", projectClass1.getName(), Set.of(expectedParam1), Collections.emptySet() );
	protected Reporting expected1 = new ReportEntry("class", projectClass1.getName( ), Set.of( expectedConstructor1 ), Set.of("org.apache.httpcomponents:httpclient:4.5.3") );
	
	protected Reporting expectedParam4c = new ReportEntry( "param", EmpiricalDistribution.class.getName( ), Collections.emptySet( ), Collections.emptySet() );	
	protected Reporting expectedException4c = new ReportEntry( "exception", UnsupportedTypeException.class.getName( ), Collections.emptySet( ), Collections.emptySet() );	
	protected Reporting expectedReturn4 = new ReportEntry("return", Wrapper.class.getName(), Collections.emptySet( ), Collections.emptySet() ); 
	protected Reporting expectedMethod4 = new ReportEntry("method", "m", Set.of(expectedParam4c, expectedException4c, expectedReturn4 ), Collections.emptySet() );
	protected Reporting expectedParam4a = new ReportEntry("param", Coin.class.getName(), Collections.emptySet(), Collections.emptySet());
	protected Reporting expectedConstructor4 = new ReportEntry( "constructor", projectClass4.getName( ),  Set.of( expectedParam4a), Collections.emptySet() );
	
	protected Reporting expectedSuperType4 = new ReportEntry( "supertype", CoinSelection.class.getName( ), Collections.emptySet( ), Collections.emptySet() );	
	protected Reporting expected4 = new ReportEntry("class", projectClass4.getName(),  Set.of(expectedSuperType4, expectedConstructor4, expectedMethod4), Set.of("de.huxhorn.sulky:de.huxhorn.sulky.generics:8.2.0", "org.apache.commons:commons-math:2.2", "jp.dodododo.janerics:janerics:1.0.1", "org.bitcoinj:bitcoinj-core:0.15-SNAPSHOT"));
  
	protected File aDependency;

}