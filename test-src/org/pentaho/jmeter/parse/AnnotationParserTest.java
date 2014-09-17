/*
 * Copyright 2002 - 2013 Pentaho Corporation.  All rights reserved.
 *
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. TThe Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

package org.pentaho.jmeter.parse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.mockito.Mockito.*;

public class AnnotationParserTest {

  AnnotationParser parser;

  @Before
  public void setUp() {
    parser = spy( new AnnotationParser() );
  }

  @After
  public void tearDown() {
    parser = null;
  }

  @Test
  public void testScanPackage() throws Exception {
    ClassLoader mockClassLoader = mock( ClassLoader.class );
    doReturn( mockClassLoader ).when( parser ).getClassLoader();

    String packagePath = "package.path.replace.me";
    String resultingPackagePath = "package/path/replace/me";

    String urlStr = "test.class";
    AnnotationParser.URLWrapper mockUrlWrapper = mock( AnnotationParser.URLWrapper.class );
    doReturn( urlStr ).when( mockUrlWrapper ).getFile();

    doReturn( mockUrlWrapper ).when( parser ).getURLWrapper( any( URL.class ) );

    Enumeration<URL> mockUrls = mock( Enumeration.class );

    final List<Object> calls = new ArrayList<Object>();
    calls.add( mock( Object.class ) );

    doAnswer( new Answer() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        boolean hasMore = !calls.isEmpty();
        if ( hasMore ) {
          calls.remove( calls.size() - 1 );
        }

        return hasMore;
      }
    } ).when( mockUrls ).hasMoreElements();

    doReturn( mockUrls ).when( mockClassLoader ).getResources( resultingPackagePath );

    //    List<JMeterTest> results = null;
    //
    //    doNothing().when( parser ).parseDirectoryResources( eq( urlStr ), eq( results ), eq( JMeterTest.class ) );
    //
    //    parser.scanPackage( packagePath, results, JMeterTest.class );
    //
    //    verify( parser, times( 1 ) ).getClassLoader();
    //    verify( mockClassLoader, times( 1 ) ).getResources( eq( resultingPackagePath ) );
    //    verify( mockUrls, times( 2 ) ).hasMoreElements();
    //    verify( mockUrls, times( 1 ) ).nextElement();
    //    verify( parser, times( 1 ) ).parseDirectoryResources( eq( urlStr ), eq( results ), eq( JMeterTest.class ) );
  }

  @Test
  public void testParseDirectoryResources() throws Exception {
    //    String rootPath = "rootPath";
    //    List<JMeterTest> results = new ArrayList<JMeterTest>();
    //    final List<String> classNames = new ArrayList<String>();
    //    final String className = "className";
    //
    //    File mockFile = mock( File.class );
    //    doReturn( mockFile ).when( parser ).getFile( rootPath );

    //    doAnswer( new Answer() {
    //      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
    //        classNames.add( className );
    //        return null;
    //      }
    //    } ).when( parser ).scanDirectoryForClassname( eq( mockFile ), anyString(), eq( classNames ) );
    //
    //    doNothing().when( parser ).scanClass( eq( className ), eq( results ), eq( JMeterTest.class ) );
    //
    //    parser.parseDirectoryResources( rootPath, results, JMeterTest.class );
    //
    //    verify( parser, times( 1 ) ).getFile( rootPath );
    //    verify( parser, times( 1 ) ).scanDirectoryForClassname( eq( mockFile ), anyString(), eq( classNames ) );
    //    verify( parser, times( 1 ) ).scanClass( eq( className ), eq( results ), eq( JMeterTest.class ) );

  }
}
