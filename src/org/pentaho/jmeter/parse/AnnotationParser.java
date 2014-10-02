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
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationParser extends Parser {

  public static final String FILE_EXT = ".class";

  public ClassLoader classLoader;

  public <T extends Annotation> void scanPackage( String packagePath, List<T> results, Class<T> annotationClass )
    throws IOException, ClassNotFoundException {
    ClassLoader classLoader = getClassLoader();
    String path = packagePath.replace( ".", "/" );
    Enumeration<URL> packageResources = classLoader.getResources( path );

    // Collect Classnames
    while ( packageResources.hasMoreElements() ) {
      URLWrapper packageResource = getURLWrapper( packageResources.nextElement() );

      if ( !packageResource.getFile().contains( "jar!" ) ) {
        parseDirectoryResources( packageResource.getFile(), results, annotationClass );
      }
    }
  }

  public <T extends Annotation> void parseDirectoryResources( String rootDirPath, List<T> results,
                                                              Class<T> annotationClass )
    throws FileNotFoundException, ClassNotFoundException {
    List<String> classNames = new ArrayList<String>();

    File rootDir = getFile( rootDirPath );
    scanDirectoryForClassname( rootDir, "", classNames, FILE_EXT );

    Map<T, Map<Method, T>> resultsMap = new HashMap<T, Map<Method, T>>();
    for ( String className : classNames ) {
      scanClass( className, resultsMap, annotationClass );
    }

    for ( T annotation : resultsMap.keySet() ) {
      results.add( annotation );
      results.addAll( resultsMap.get( annotation ).values() );
    }
  }

  public <T extends Annotation> void scanClass( String className, Map<T, Map<Method, T>> results,
                                                Class<T> annotationClass )
    throws ClassNotFoundException, FileNotFoundException {
    Class<?> clazz = getClassLoader().loadClass( className );

    T classAnnotation = scanAnnotation( clazz, annotationClass );
    if ( results.get( classAnnotation ) == null ) {
      results.put( classAnnotation, new HashMap<Method, T>() );
    }
    Map<Method, T> array = results.get( classAnnotation );

    Method[] methods = clazz.getMethods();
    for ( Method method : methods ) {
      T annotation = scanAnnotation( method, annotationClass );
      if ( annotation != null ) {
        array.put( method, annotation );
      }
    }
  }

  public <T extends Annotation> void scanMethods( Class clazz, Map<Method, T> results, Class<T> annotationClass )
    throws ClassNotFoundException, FileNotFoundException {
    Method[] methods = clazz.getMethods();
    for ( Method method : methods ) {
      T annotation = scanAnnotation( method, annotationClass );
      if ( annotation != null ) {
        results.put( method, annotation );
      }
    }
  }

  public <T extends Annotation> T scanAnnotation( Class<?> clazz, Class<T> annotationClass ) {
    if ( !clazz.isAnnotationPresent( annotationClass ) ) {
      return null;
    }

    return clazz.getAnnotation( annotationClass );
  }

  public <T extends Annotation> T scanAnnotation( Method method, Class<T> annotationClass ) {
    if ( !method.isAnnotationPresent( annotationClass ) ) {
      return null;
    }

    return method.getAnnotation( annotationClass );
  }

  protected URLWrapper getURLWrapper( URL url ) {
    return new URLWrapper( url );
  }

  protected ClassLoader getClassLoader() {
    if ( this.classLoader == null ) {
      this.classLoader = Thread.currentThread().getContextClassLoader();
    }
    return this.classLoader;
  }

  protected static class URLWrapper {
    private URL url;

    URLWrapper( URL url ) {
      this.url = url;
    }

    public String getFile() {
      return url.getFile();
    }
  }
}
