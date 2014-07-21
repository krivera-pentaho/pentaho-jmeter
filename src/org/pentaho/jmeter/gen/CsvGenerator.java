package org.pentaho.jmeter.gen;

import org.pentaho.jmeter.annotation.JMeterTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CsvGenerator implements FileGenerator {

    public static final String EXT = ".csv";

    @Override
    public File generateFile(String path, List<JMeterTest> annotations) {
        StringBuilder result = new StringBuilder();

        for (JMeterTest annotation : annotations) {
            genLine(annotation, result);
        }

        String filePath = path + EXT;

        try {
            PrintWriter out = new PrintWriter(path + EXT);
            out.print(result);
            out.close();

            return new File(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void genLine(JMeterTest annotation, StringBuilder result) {
        result.append(annotation.url());
        result.append(",");
        result.append(annotation.requestType());
        result.append(",");
        result.append(annotation.postData());
        result.append(",");
        result.append(annotation.statusCode());
        result.append("\n");
    }


}
