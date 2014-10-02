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

public class ParserTest {

  Parser parser;

  @Before
  public void setup() {
    parser = spy( new Parser() );
  }

  @Test
  public void testScanDirectoryForClassname() throws Exception {
    File mockFile = mock( File.class );
    String packagePath = "packagePath";
    List<String> mockResults = mock( List.class );
    String fileExt = ".ext";

    File mockChildFile = mock( File.class );
    File[] files = new File[] { mockChildFile };
    doReturn( files ).when( mockFile ).listFiles();

    String childFileName = "childFileName" + fileExt;
    doReturn( childFileName ).when( mockChildFile ).getName();

    doNothing().when( parser )
      .scanDirectoryForClassname( mockChildFile, packagePath + "." + childFileName, mockResults, fileExt );

    doNothing().when( parser )
      .scanDirectoryForClassname( mockChildFile, childFileName, mockResults, fileExt );

    // Test 1
    doReturn( false ).when( mockFile ).isDirectory();
    parser.scanDirectoryForClassname( mockFile, packagePath, mockResults, fileExt );

    // Test 2
    doReturn( true ).when( mockFile ).isDirectory();
    doReturn( true ).when( mockChildFile ).isDirectory();
    parser.scanDirectoryForClassname( mockFile, packagePath, mockResults, fileExt );

    // Test 3
    packagePath = "";
    parser.scanDirectoryForClassname( mockFile, packagePath, mockResults, fileExt );

    // Test 4
    packagePath = "packagePath";
    doReturn( false ).when( mockChildFile ).isDirectory();
    parser.scanDirectoryForClassname( mockFile, packagePath, mockResults, fileExt );

    verify( mockResults, times( 1 ) ).add( packagePath + "." + childFileName.replace( fileExt, "" ) );
    verify( mockFile, times( 3 ) ).listFiles();
    verify( mockChildFile, times( 4 ) ).getName();
    verify( parser, times( 1 ) ).scanDirectoryForClassname( mockChildFile, packagePath + "." + childFileName,
      mockResults, fileExt );
    verify( parser, times( 1 ) ).scanDirectoryForClassname( mockChildFile, childFileName, mockResults, fileExt );
  }

  @Test
  public void testScanDirectoryForFiles() throws Exception {
    File mockFile = mock( File.class );
    List<File> mockResults = mock( List.class );
    String fileExt = ".ext";

    File mockChildFile = mock( File.class );
    File[] mockChildren = new File[] { mockChildFile };
    doReturn( mockChildren ).when( mockFile ).listFiles();

    String absolutePath = "absolutePath";
    doReturn( absolutePath ).when( mockChildFile ).getAbsolutePath();

    doReturn( false ).when( parser ).pathInExcludes( absolutePath );

    doNothing().when( parser ).scanDirectoryForFiles( mockChildFile, mockResults, fileExt );

    String fileName = "name" + fileExt;
    doReturn( fileName ).when( mockChildFile ).getName();

    // Test 1
    doReturn( false ).when( mockFile ).isDirectory();
    parser.scanDirectoryForFiles( mockFile, mockResults, fileExt );

    // Test 2
    doReturn( true ).when( mockFile ).isDirectory();
    doReturn( true ).when( mockChildFile ).isDirectory();
    parser.scanDirectoryForFiles( mockFile, mockResults, fileExt );

    // Test 3
    doReturn( false ).when( mockChildFile ).isDirectory();
    parser.scanDirectoryForFiles( mockFile, mockResults, fileExt );

    verify( mockResults, times( 1 ) ).add( mockChildFile );
    verify( mockFile, times( 2 ) ).listFiles();
    verify( mockChildFile, times( 2 ) ).getAbsolutePath();
    verify( parser, times( 2 ) ).pathInExcludes( absolutePath );
    verify( parser, times( 1 ) ).scanDirectoryForFiles( mockChildFile, mockResults, fileExt );
  }
}