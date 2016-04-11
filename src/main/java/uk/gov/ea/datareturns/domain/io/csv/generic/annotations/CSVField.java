/**
 *
 */
package uk.gov.ea.datareturns.domain.io.csv.generic.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation allows you to specify a mapping between a Java POJO field and a CSV header field
 *
 * @author Sam Gardner-Dell
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CSVField {
	/** The CSV field name for the Java member */
	String value();
}
