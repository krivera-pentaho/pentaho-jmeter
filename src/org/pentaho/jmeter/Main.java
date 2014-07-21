package org.pentaho.jmeter;

import org.pentaho.jmeter.annotation.JMeterTest;
import org.pentaho.jmeter.gen.CsvGenerator;
import org.pentaho.jmeter.gen.FileGenerator;
import org.pentaho.jmeter.parse.JMeterAnnotationParser;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String ERROR_INSUFFICIENT_ARGS = "Insufficient arguments detected.\n\targ0=base package, " +
            "arg1=path and filename";
    public static final String ERROR_UNKNOWN_EXT = "Unknown file extension";
    public static final String MESSAGE_NO_ANNOTATIONS = "No annotations detected in package ";

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new RuntimeException(ERROR_INSUFFICIENT_ARGS);
        }

        execute(args[0], args[1]);
    }

    public static void execute(String packagePath, String filePath) throws Exception {
        List<JMeterTest> results = new ArrayList<JMeterTest>();
        JMeterAnnotationParser.scanPackage(packagePath, results);

        if (results.size() == 0) {
            System.out.print(MESSAGE_NO_ANNOTATIONS);
            System.out.println(packagePath);
            return;
        }

        FileGenerator generator = null;

        if (filePath.endsWith(".csv")) {
            generator = new CsvGenerator();
        } else {
            throw new RuntimeException(ERROR_UNKNOWN_EXT);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Output file at ");
        sb.append(generator.generateFile(filePath, results).getCanonicalPath());
        System.out.println(sb.toString());
    }
}
