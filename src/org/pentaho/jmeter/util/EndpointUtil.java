/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.jmeter.util;

import org.pentaho.jmeter.parse.AnnotationParser;
import org.pentaho.jmeter.parse.CommentParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EndpointUtil {

  public static <T extends Annotation> boolean compareCommentsAndAnnotations( String scanDirPath, String baseUrl,
                                                                              Class<T> annotationClass )
    throws Exception {
    /*
     * Retrieve comments
     */
    String regex =
      "/\\*\\*[\\S\\s]+?<p>[\\S\\s]*?Example Request[\\S\\s]+?([A-Z]{2,}) (.*?)\\n[\\S\\s]+?</p>[\\S\\s]+?public " +
        "\\S+? (\\S+?)\\(";
    String contentContains = "Example Request:";

    final Map<String, List<String[]>> results = new HashMap<String, List<String[]>>();

    CommentParser commentParser = new CommentParser();

    commentParser.parseRegEx( scanDirPath, contentContains, regex, new CommentParser.MatcherHandler() {
      @Override public void handleResult( String className, Matcher m ) {
        if ( !results.containsKey( className ) ) {
          results.put( className, new ArrayList<String[]>() );
        }

        results.get( className ).add( new String[] {
          m.group( 3 ), // Method Name
          m.group( 1 ), // Request Type
          m.group( 2 )  // Request URL
        } );
      }
    } );

    StringBuffer exceptions = new StringBuffer();

    /*
     * Parse annotations
     */
    AnnotationParser annotationParser = new AnnotationParser();
    Map<String, String> baseUrls = new HashMap<String, String>();
    for ( String className : results.keySet() ) {

      /*
       * Path annotation
       */
      Map<T, Map<Method, T>> annotations = new HashMap<T, Map<Method, T>>();
      annotationParser.scanClass( className, annotations, annotationClass );

      Map<String, Object[]> methodUrls = new HashMap<String, Object[]>();

      for ( T annotation : annotations.keySet() ) {
        String urlBase = annotation == null ? "" : getValue( annotation );
        urlBase = urlBase.endsWith( "/" ) ? urlBase.substring( 0, urlBase.length() - 1 ) : urlBase;
        urlBase = baseUrl + ( urlBase.startsWith( "/" ) || urlBase.isEmpty() ? "" : "/" ) + urlBase;

        baseUrls.put( className, urlBase );

        for ( Method method : annotations.get( annotation ).keySet() ) {
          T methodAnnotation = annotations.get( annotation ).get( method );
          String url = methodAnnotation == null ? "" : getValue( methodAnnotation );
          url = url.startsWith( "/" ) ? url : "/" + url;
          url = urlBase + url;

          // 0 - Method, 1 - URL
          methodUrls.put( method.getName(), new Object[] {
            method, url
          } );
        }
      }

      Map<String, Class<? extends Annotation>> cachedRequestTypeAnnotations = new HashMap<String, Class<? extends
        Annotation>>();

      /*
       * Assert for every comment, the example matches the path
       */
      for ( String[] arr : results.get( className ) ) {
        String methodName = arr[ 0 ];
        String requestType = arr[ 1 ];
        String exampleUrl = arr[ 2 ];

        Object[] methodRegex = methodUrls.get( methodName );

        if ( !cachedRequestTypeAnnotations.containsKey( requestType ) ) {
          cachedRequestTypeAnnotations
            .put( requestType, (Class<? extends Annotation>) Class.forName( "javax.ws.rs." + requestType ) );
        }
        Class<? extends Annotation> annotation = cachedRequestTypeAnnotations.get( requestType );

        if ( methodRegex == null ) {
          for ( Method method : Class.forName( className ).getMethods() ) {
            if ( method.getName().equals( methodName ) && method.isAnnotationPresent( annotation ) ) {
              methodRegex = new Object[] {
                method, baseUrls.get( className )
              };
              break;
            }
          }
        }

        if ( methodRegex == null ) {
          return false;
        }

        Method method = (Method) methodRegex[ 0 ];
        if ( !method.isAnnotationPresent( annotation ) ) {
          return false;
        }

        /*
         Replace { var : regex } with just the regex
         */
        String path = (String) methodRegex[ 1 ];
        Matcher m = Pattern.compile( "\\{.+?:(.+?)\\}" ).matcher( path );

        String pathRegex = path;
        while ( m.find() ) {
          pathRegex = path.replace( m.group(), m.group( 1 ).trim() + "?" );
        }

        /*
         Replace { var } with .*?
         */
        m = Pattern.compile( "\\{.+?\\}" ).matcher( pathRegex );
        while ( m.find() ) {
          pathRegex = pathRegex.replace( m.group(), ".*?" );
        }

        pathRegex += ".*";

        if ( !Pattern.compile( pathRegex ).matcher( exampleUrl ).matches() ) {
          if ( exceptions.length() > 0 ) {
            exceptions.append( "\n" );
          }
          exceptions.append( "'" );
          exceptions.append( className );
          exceptions.append( ":" );
          exceptions.append( methodName );
          exceptions.append( "' " );
          exceptions.append( exampleUrl );
          exceptions.append( " does not match path " );
          exceptions.append( path );
        }
      }
    }

    if ( exceptions.length() > 0 ) {
      throw new RuntimeException( exceptions.toString() );
    }

    return true;
  }

  private static String getValue( Annotation annotation )
    throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = annotation.getClass().getMethod( "value" );
    return (String) method.invoke( annotation );
  }
}
