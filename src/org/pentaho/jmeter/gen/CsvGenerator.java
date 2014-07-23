package org.pentaho.jmeter.gen;

import org.pentaho.jmeter.annotation.JMeterTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CsvGenerator implements FileGenerator {

    public static final String BLANK_VALUE = "pen:ignore";

    @Override
    public File generateFile(String path, List<JMeterTest> annotations) {
        StringBuilder result = new StringBuilder();

        for (JMeterTest annotation : annotations) {
            genLine(annotation, result);
        }

        try {
            PrintWriter out = new PrintWriter(path);
            out.print(result);
            out.close();

            return new File(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void genLine(JMeterTest annotation, StringBuilder result) {
        result.append(getValue(annotation.url()));
        result.append(",");
        result.append(getValue(annotation.requestType()));
        result.append(",");
        result.append(getValue(annotation.postData()));
        result.append(",");
        result.append(getValue(annotation.statusCode()));
        result.append(",");
        result.append(getValue(annotation.result()));
        result.append("\n");
    }

    private String getValue(String val) {
        return (val == null || val.isEmpty()) ? BLANK_VALUE : val;
    }
}
