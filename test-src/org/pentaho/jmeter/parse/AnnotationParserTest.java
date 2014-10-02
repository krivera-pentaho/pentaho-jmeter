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

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AnnotationParserTest {

  AnnotationParser parser;
  Class<Test> testClass = Test.class;

  @Before
  public void setUp() {
    parser = spy( new AnnotationParser() );
  }

  @Test
  public void testScanPackage() throws Exception {
    String packagePath = "package.path";
    List<Test> mockResults = mock( List.class );

    ClassLoader mockClassLoader = mock( ClassLoader.class );
    doReturn( mockClassLoader ).when( parser ).getClassLoader();

    Enumeration<URL> mockEnum = mock( Enumeration.class );
    doReturn( mockEnum ).when( mockClassLoader ).getResources( packagePath.replace( ".", "/" ) );

    final Map<String, Boolean> hasMoreElementsMap = new HashMap<String, Boolean>();

    final String key = "hasMoreElements";
    hasMoreElementsMap.put( key, true );

    final AnnotationParser.URLWrapper mockUrlWrapper = mock( AnnotationParser.URLWrapper.class );
    doAnswer( new Answer() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        hasMoreElementsMap.put( key, false );
        return mockUrlWrapper;
      }
    } ).when( parser ).getURLWrapper( any( URL.class ) );

    doAnswer( new Answer() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return hasMoreElementsMap.get( key );
      }
    } ).when( mockEnum ).hasMoreElements();


    String resourceFile = "test";
    doReturn( resourceFile ).when( mockUrlWrapper ).getFile();

    doNothing().when( parser ).parseDirectoryResources( resourceFile, mockResults, testClass );

    parser.scanPackage( packagePath, mockResults, testClass );

    verify( parser, times( 1 ) ).getClassLoader();
    verify( mockClassLoader, times( 1 ) ).getResources( packagePath.replace( ".", "/" ) );
    verify( mockEnum, times( 2 ) ).hasMoreElements();
    verify( parser, times( 1 ) ).getURLWrapper( any( URL.class ) );
    verify( mockUrlWrapper, times( 2 ) ).getFile();
    verify( parser, times( 1 ) ).parseDirectoryResources( resourceFile, mockResults, testClass );
  }

  @Test
  public void testParseDirectoryResources() throws Exception {
    String rootDirPath = "rootDirPath";
    List<Test> mockResults = mock( List.class );

    File mockFile = mock( File.class );
    doReturn( mockFile ).when( parser ).getFile( rootDirPath );

    final String className = "class.Name";
    doAnswer( new Answer() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        List<String> classNames = (List<String>) invocation.getArguments()[ 2 ];
        classNames.add( className );
        return null;
      }
    } ).when( parser )
      .scanDirectoryForClassname( any( File.class ), eq( "" ), anyList(), eq( AnnotationParser.FILE_EXT ) );

    final Test annotation = mock( testClass );
    doAnswer( new Answer() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        Map<Test, Map<Method, Test>> map = (Map<Test, Map<Method, Test>>) invocation.getArguments()[ 1 ];
        map.put( annotation, new HashMap<Method, Test>() );
        return null;
      }
    } ).when( parser ).scanClass( eq( className ), anyMap(), eq( testClass ) );

    parser.parseDirectoryResources( rootDirPath, mockResults, testClass );

    verify( parser, times( 1 ) ).getFile( rootDirPath );
    verify( parser, times( 1 ) ).scanDirectoryForClassname( any( File.class ), eq( "" ), anyList(),
      eq( AnnotationParser.FILE_EXT ) );
    verify( parser, times( 1 ) ).scanClass( eq( className ), anyMap(), eq( testClass ) );
    verify( mockResults ).add( annotation );
  }

  @Test
  public void testScanClass() throws Exception {
    String className = "className";
    final Map<Test, Map<Method, Test>> mockResults = mock( Map.class );

    ClassLoader mockClassLoader = mock( ClassLoader.class );
    doReturn( mockClassLoader ).when( parser ).getClassLoader();

    Class clazz = this.getClass();
    doReturn( clazz ).when( mockClassLoader ).loadClass( className );

    final Test mockTestClassAnnotation = mock( testClass );
    doReturn( mockTestClassAnnotation ).when( parser ).scanAnnotation( clazz, testClass );

    final Map<Method, Test> mockMap = mock( Map.class );
    doAnswer( new Answer() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        doReturn( mockMap ).when( mockResults ).get( mockTestClassAnnotation );
        return null;
      }
    } ).when( mockResults ).get( mockTestClassAnnotation );

    Method method = this.getClass().getMethods()[ 0 ];

    Test mockTestMethodAnnotation = mock( testClass );
    doReturn( mockTestMethodAnnotation ).when( parser ).scanAnnotation( method, testClass );

    parser.scanClass( className, mockResults, testClass );

    verify( mockResults, times( 1 ) ).put( eq( mockTestClassAnnotation ), anyMap() );
    verify( mockMap, times( 1 ) ).put( method, mockTestMethodAnnotation );
    verify( parser, times( 1 ) ).getClassLoader();
    verify( mockClassLoader, times( 1 ) ).loadClass( className );
    verify( parser, times( 1 ) ).scanAnnotation( clazz, testClass );
    verify( mockResults, times( 2 ) ).get( mockTestClassAnnotation );
    verify( parser, times( 1 ) ).scanAnnotation( method, testClass );
  }

  @Test
  public void testScanMethods() throws Exception {
    Class clazz = this.getClass();
    Map<Method, Test> mockResults = mock( Map.class );

    Method thisTestMethod = this.getClass().getMethod( "testScanMethods" );

    Test mockMethodAnnotation = mock( testClass );
    doReturn( mockMethodAnnotation ).when( parser ).scanAnnotation( thisTestMethod, testClass );

    parser.scanMethods( clazz, mockResults, testClass );

    verify( mockResults, times( 1 ) ).put( thisTestMethod, mockMethodAnnotation );
    verify( parser, times( 1 ) ).scanAnnotation( thisTestMethod, testClass );
  }

}
