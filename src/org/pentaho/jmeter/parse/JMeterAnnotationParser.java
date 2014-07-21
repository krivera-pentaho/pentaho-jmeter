package org.pentaho.jmeter.parse;

import org.pentaho.jmeter.annotation.JMeterTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class JMeterAnnotationParser {

    public static final String FILE_EXT = ".class";

    public static void scanPackage(String packagePath, List<JMeterTest> results) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packagePath.replace(".", "/");
        Enumeration<URL> packageResources = classLoader.getResources(path);

        List<String> classNames = new ArrayList<String>();

        // Collect Classnames
        while(packageResources.hasMoreElements()) {
            URL packageResource = packageResources.nextElement();

            if (!packageResource.getFile().contains("jar!")) {
                File dir = new File(packageResource.getFile());
                scanDirectory(dir, packagePath, classNames);
            }
        }

        // Traverse the class names
        for (String className : classNames) {
            scanClass(className, classLoader, results);
        }
    }

    private static void scanDirectory(File dir, String packagePath, List<String> result) throws ClassNotFoundException, FileNotFoundException {
        if (!dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packagePath + "." + file.getName(), result);
            }

            // Check to make sure it is a java class
            if (file.getName().contains(FILE_EXT)) {
                String className = (packagePath + "." + file.getName()).replace(FILE_EXT, "");
                result.add(className);
            }
        }
    }

    private static void scanClass(String className, ClassLoader classLoader, List<JMeterTest> results) throws ClassNotFoundException, FileNotFoundException {
        Method[] methods = classLoader.loadClass(className).getMethods();
        for (Method method : methods) {
            scanAnnotation(method, results);
        }
    }


    private static void scanAnnotation(Method method, List<JMeterTest> results) {
        if (!method.isAnnotationPresent(JMeterTest.class)) {
            return;
        }

        JMeterTest jMeterAnnotation = method.getAnnotation(JMeterTest.class);
        results.add(jMeterAnnotation);
    }


}