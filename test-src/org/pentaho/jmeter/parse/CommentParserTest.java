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

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class CommentParserTest {

  CommentParser parser;

  @Before
  public void setup() {
    parser = spy( new CommentParser() );
  }

  @Test
  public void testScanDirectoryForClassname() throws Exception {
    File mockParentFile = mock( File.class );
    String packagePath = "pack.age.path";
    List<String> mockResult = mock( List.class );
    String fileExt = ".ext";

    File mockChildFile = mock( File.class );

    File[] mockFiles = new File[] { mockChildFile };
    doReturn( mockFiles ).when( mockParentFile ).listFiles();

    String childName = "childName" + fileExt;
    doReturn( childName ).when( mockChildFile ).getName();

    doNothing().when( parser )
      .scanDirectoryForClassname( mockChildFile, packagePath + "." + childName, mockResult, fileExt );

    // Test 1
    doReturn( false ).when( mockParentFile ).isDirectory();

    parser.scanDirectoryForClassname( mockParentFile, packagePath, mockResult, fileExt );

    // Test 2
    doReturn( true ).when( mockParentFile ).isDirectory();
    doReturn( true ).when( mockChildFile ).isDirectory();

    parser.scanDirectoryForClassname( mockParentFile, packagePath, mockResult, fileExt );

    // Test 3
    doReturn( false ).when( mockChildFile ).isDirectory();

    parser.scanDirectoryForClassname( mockParentFile, packagePath, mockResult, fileExt );

    verify( mockResult, times( 1 ) ).add( packagePath + "." + childName.replace( fileExt, "" ) );
    verify( mockParentFile, times( 2 ) ).listFiles();
    verify( mockChildFile, times( 3 ) ).getName();
    verify( parser, times( 1 ) )
      .scanDirectoryForClassname( mockChildFile, packagePath + "." + childName, mockResult, fileExt );
  }

  @Test
  public void testScanDirectoryForFiles() {
    File mockParentFile = mock( File.class );
    List<File> mockResult = mock( List.class );
    String fileExt = ".ext";
    String excludePath = "excludePath";

    File mockChildFile = mock( File.class );
    File[] listFiles = new File[] { mockChildFile };
    doReturn( listFiles ).when( mockParentFile ).listFiles();

    String absolutePath = "absolutePath";
    doReturn( absolutePath ).when( mockChildFile ).getAbsolutePath();

    String fileName = "fileName" + fileExt;
    doReturn( fileName ).when( mockChildFile ).getName();

    doReturn( false ).when( parser ).pathInExcludes( eq( absolutePath ), any( String[].class ) );

    doNothing().when( parser ).scanDirectoryForFiles( mockChildFile, mockResult, fileExt );

    // Test 1
    doReturn( false ).when( mockParentFile ).isDirectory();

    parser.scanDirectoryForFiles( mockParentFile, mockResult, fileExt, excludePath );

    // Test 2
    doReturn( true ).when( mockParentFile ).isDirectory();
    doReturn( true ).when( mockChildFile ).isDirectory();

    parser.scanDirectoryForFiles( mockParentFile, mockResult, fileExt, excludePath );

    // Test 3
    doReturn( false ).when( mockChildFile ).isDirectory();

    parser.scanDirectoryForFiles( mockParentFile, mockResult, fileExt, excludePath );

    verify( mockResult, times( 1 ) ).add( mockChildFile );
    verify( mockParentFile, times( 2 ) ).listFiles();
    verify( mockChildFile, times( 2 ) ).getAbsolutePath();
    verify( mockChildFile, times( 1 ) ).getName();
    verify( parser, times( 2 ) ).pathInExcludes( eq( absolutePath ), any( String[].class ) );
    verify( parser, times( 1 ) ).scanDirectoryForFiles( mockChildFile, mockResult, fileExt );
  }

  @Test
  public void testPathInExcludes() {
    String path = "path";
    String excludePath1 = "excludePath1";
    String excludePath2 = "path";

    // Test 1
    boolean result = parser.pathInExcludes( path );
    assertFalse( result );

    // Test 2
    result = parser.pathInExcludes( path, excludePath1, excludePath2 );
    assertTrue( result );
  }
}
