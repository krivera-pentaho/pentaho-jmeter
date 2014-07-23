package org.pentaho.jmeter.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JMeterTest {
    String url();
    String requestType();
    String postData() default "";
    String statusCode() default "200";
    String result() default "";
}
