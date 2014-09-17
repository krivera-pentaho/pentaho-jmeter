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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentParser extends Parser {
  protected static final String EXT = ".java";

  public void parseRegEx( String rootDirPath, String contentContains, String exp, MatcherHandler handler )
    throws FileNotFoundException, ClassNotFoundException {
    File rootDir = getFile( rootDirPath );
    List<File> files = new ArrayList<File>();

    // Get java files in director
    scanDirectoryForFiles( rootDir, files, EXT );

    for ( File file : files ) {
      Scanner scanner = new Scanner( file );
      String content = scanner.useDelimiter( "\\Z" ).next();

      if ( contentContains == null || content.contains( contentContains ) ) {
        parse( content, exp, handler );
      }

      scanner.close();
    }
  }

  protected void parse( String content, String exp, MatcherHandler handler ) {

    Matcher m1 = Pattern.compile( "package (.*?);[\\S\\s]+?(class|interface) (.+?) " ).matcher( content );
    boolean classNameFound = m1.find();
    String className = !classNameFound ? null : m1.group( 1 ) + "." + m1.group( 3 );

    Matcher m = Pattern.compile( exp ).matcher( content );
    while ( m.find() ) {
      handler.handleResult( className, m );
    }
  }

  public interface MatcherHandler {
    public void handleResult( String clasName, Matcher m );
  }

}
