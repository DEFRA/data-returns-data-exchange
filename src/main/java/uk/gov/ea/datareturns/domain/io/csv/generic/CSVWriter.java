package uk.gov.ea.datareturns.domain.io.csv.generic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.domain.io.csv.generic.annotations.CSVField;
import uk.gov.ea.datareturns.domain.io.csv.generic.settings.CSVWriterSettings;

/**
 * Allows a Java model that is annotated with the {@link CSVField} annotation to be serialized to a CSV file.
 *
 * @author Sam Gardner-Dell
 */
public class CSVWriter<T> {
	/** Class logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVWriter.class);
	/** Default settings used by the reader */
	private static final CSVWriterSettings DEFAULT_SETTINGS = new CSVWriterSettings(',', null);

	/** Javabean class to serialize to */
	private final Class<T> javaBeanClass;
	/** CSV format settings */
	private final CSVWriterSettings settings;

	/**
	 * Set up a new {@link CSVWriter} to map from the given JavaBean class.
	 *
	 * @param javaBeanClass the JavaBean class that the writer should map from
	 * @param csvWriterSettings the settings to be used by this writer
	 */
	public CSVWriter(final Class<T> javaBeanClass, final CSVWriterSettings csvWriterSettings) {
		this.javaBeanClass = javaBeanClass;
		this.settings = csvWriterSettings != null ? csvWriterSettings : DEFAULT_SETTINGS;
	}

	/**
	 * Write the {@link List} of records (annotated with the {@link CSVField} annotation) to the specified {@link OutputStream}
	 *
	 * @param records the list of records to write
	 * @param out the {@link OutputStream} to write to
	 * @throws IOException if a problem occurs attempting to write to the given {@link OutputStream}
	 */
	public void write(final List<T> records, final OutputStream out) throws IOException {
		try (
				final PrintStream ps = new PrintStream(out);
				final CSVPrinter printer = this.settings.getCSVFormat().print(ps);) {
			// Retrieve a map from the CSV field headers to the Java bean fields
			final Map<String, Method> pojoMappings = getCSVMethodMapping(this.javaBeanClass);

			/// Iterate through the records we need to write out
			for (final T record : records) {
				final Object[] values = new Object[this.settings.getHeaders().size()];

				for (int i = 0; i < this.settings.getHeaders().size(); i++) {
					final String header = this.settings.getHeaders().get(i);

					// Find the getter for the mapped field that is defined for this CSV header
					final Method beanGetter = pojoMappings.get(header);

					Object value = null;
					if (beanGetter != null) {
						try {
							value = beanGetter.invoke(record);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							LOGGER.debug("Unable to invoke getter method " + beanGetter.getName() + " for bean " + record.getClass());
						}
					}

					if (!this.settings.isWriteNullValues() && value == null) {
						value = "";
					}
					if (this.settings.isTrimWhitespace()) {
						value = StringUtils.strip(Objects.toString(value));
					}

					// Attempt to read the value from the getter method, default to null.
					values[i] = value;
				}
				printer.printRecord(values);
			}
		}
	}

	/**
	 * Use reflection to determine the fields in the JavaBean that may be mapped to a CSV file header field.
	 *
	 * @param cls The JavaBean to scan for {@link CSVField} annotations
	 * @return A {@link Map} of CSV Field Header Names (key) to the JavaBean getter method for the field (value)
	 */
	private static Map<String, Method> getCSVMethodMapping(final Class<?> cls) {
		final Map<String, Method> fieldMap = new HashMap<>();
		for (final Field f : cls.getDeclaredFields()) {
			final CSVField annotation = f.getAnnotation(CSVField.class);

			if (annotation != null) {
				final String csvFieldName = annotation.value();
				final Method getterMethod = getGetterForField(cls, f);

				fieldMap.put(csvFieldName, getterMethod);
			}
		}
		return fieldMap;
	}

	/**
	 * Retrieve the setter method for the given field based on standard JavaBean conventions
	 *
	 * @param field the field to find the setter method for
	 * @return a {@link Method} instance  providing the name of the setter method
	 */
	private static Method getGetterForField(final Class<?> parentClass, final Field field) {
		final StringBuilder methodName = new StringBuilder("get");
		methodName.append(StringUtils.capitalize(field.getName()));

		Method method = null;
		try {
			method = parentClass.getMethod(methodName.toString());
		} catch (NoSuchMethodException | SecurityException e) {
			final String errorMessage = String.format(
					"Unable to determine getter method for the %s field of the %s class. Please ensure this is a valid JavaBean type.",
					field.getName(), parentClass.getName());
			// Throw this as a runtime exception as it indicates a programming error.
			throw new RuntimeException(errorMessage, e);
		}
		return method;
	}
}
