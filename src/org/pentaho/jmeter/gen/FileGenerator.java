package org.pentaho.jmeter.gen;

import org.pentaho.jmeter.annotation.JMeterTest;

import java.io.File;
import java.util.List;

public interface FileGenerator {
    public File generateFile(String path, List<JMeterTest> annotations);
}
