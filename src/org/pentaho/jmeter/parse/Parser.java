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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Parser {

  protected void scanDirectoryForClassname( File dir, String packagePath, List<String> result, String fileExt )
    throws ClassNotFoundException, FileNotFoundException {
    if ( !dir.isDirectory() ) {
      return;
    }

    File[] files = dir.listFiles();
    for ( File file : files ) {
      if ( file.isDirectory() ) {
        scanDirectoryForClassname( file, packagePath + ( packagePath.isEmpty() ? "" : "." ) + file.getName(), result,
          fileExt );
      }

      // Check to make sure it is a java class
      if ( file.getName().contains( fileExt ) ) {
        String className = ( packagePath + "." + file.getName() ).replace( fileExt, "" );
        result.add( className );
      }
    }
  }

  protected void scanDirectoryForFiles( File dir, List<File> result, String fileExt ) {
    if ( !dir.isDirectory() ) {
      return;
    }

    File[] files = dir.listFiles();
    for ( File file : files ) {
      if ( file.isDirectory() ) {
        scanDirectoryForFiles( file, result, fileExt );
      }

      if ( file.getName().contains( fileExt ) ) {
        result.add( file );
      }
    }
  }

  protected File getFile( String path ) {
    return new File( path );
  }
}
