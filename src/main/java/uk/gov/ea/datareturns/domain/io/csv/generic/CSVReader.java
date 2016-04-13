package uk.gov.ea.datareturns.domain.io.csv.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.domain.io.csv.generic.annotations.CSVField;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.InconsistentRowException;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.ValidationException;
import uk.gov.ea.datareturns.domain.io.csv.generic.settings.CSVReaderSettings;

/**
 * Allows a CSV file to be parsed into a Java model that is annotated with the {@link CSVField} annotation.
 *
 * @author Sam Gardner-Dell
 */
public class CSVReader<T> {
	/** Class logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVReader.class);
	/** Default settings used by the reader */
	private static final CSVReaderSettings DEFAULT_SETTINGS = new CSVReaderSettings(',', null);
	/** Javabean class to serialize to */
	private final Class<T> javaBeanClass;
	/** CSV format settings */
	private final CSVReaderSettings settings;

	/**
	 * Set up a new {@link CSVReader} to map to the given JavaBean class.
	 *
	 * @param javaBeanClass the JavaBean class that the reader should map to
	 * @param csvReaderSettings the settings to be used by this reader
	 */
	public CSVReader(final Class<T> javaBeanClass, final CSVReaderSettings csvReaderSettings) {
		this.javaBeanClass = javaBeanClass;
		this.settings = csvReaderSettings != null ? csvReaderSettings : DEFAULT_SETTINGS;
	}

	/**
	 * Parse a CSV file into a Java model
	 *
	 * @param csvFile the CSV File to be parsed
	 * @param javaBeanClass the JavaBean class (annotated with the {@link CSVField} annotation) to map the CSV records
	 * @return a {@link CSVModel} containing the list of parsed records and CSV header information
	 * @throws IOException if the specified file could not be read
	 */
	public CSVModel<T> parseCSV(final File csvFile) throws IOException, ValidationException {
		try (final InputStream fos = new FileInputStream(csvFile);) {
			return parseCSV(fos);
		}
	}

	/**
	 * Parse a CSV file into a Java model
	 *
	 * @param csvStream an {@link InputStream} from which CSV data can be read.
	 * @param javaBeanClass the JavaBean class (annotated with the {@link CSVField} annotation) to map the CSV records
	 * @return a {@link CSVModel} containing the list of parsed records and CSV header information
	 * @throws IOException if the specified file could not be read
	 */
	public CSVModel<T> parseCSV(final InputStream csvStream) throws IOException, ValidationException {
		final CSVModel<T> model = new CSVModel<>();
		final List<T> records = new ArrayList<>();

		try (final BOMInputStream bom = new BOMInputStream(csvStream);
				final Reader reader = new InputStreamReader(bom, "UTF-8");
				final CSVParser parser = new CSVParser(reader, this.settings.getCSVFormat());) {
			// Retrieve a map from the CSV field headers to the Java bean fields
			final Map<String, FieldMethodMapping> pojoMappings = getCSVMethodMapping(this.javaBeanClass);

			final Map<String, Integer> headerMap = parser.getHeaderMap();
			model.setHeaderMap(headerMap);

			final CSVHeaderValidator validator = this.settings.getHeaderValidator();
			if (validator != null) {
				validator.validateHeaders(headerMap);
			}

			// Mappings may exist for entries not in the CSV file being parsed.  Remove these based on the header map returned.
			pojoMappings.keySet().retainAll(model.getHeaderMap().keySet());

			// Store a map of pojo field to csv header name on the model
			pojoMappings.forEach((k, v) -> model.getPojoFieldToHeaderMap().put(v.getFieldName(), k));

			// Iterate the records in the CSV file reading data into the supplied JavaBean class
			try {
				for (final CSVRecord csvRecord : parser) {
					if (!csvRecord.isConsistent()) {
						throw new InconsistentRowException(
								String.format("Record %d contains additional fields not defined in the header.",
										csvRecord.getRecordNumber()));
					}
					records.add(mapToBean(csvRecord, pojoMappings));
				}
			} catch (final RuntimeException e) {
				// The underlying apache commons CSV reader will throw a RuntimeException if a parse error occurs within the iterator
				// interface.  We need to protect against this.
				throw new ValidationException("Invalid data was encountered while attempting to parse the CSV file.");
			}
			model.setRecords(records);
		}
		return model;
	}

	/**
	 * Maps the data provided in the {@link CSVRecord} to the fields of the JavaBean type based on the header->java field mappings
	 *
	 * @param csvRecord the {@link CSVRecord} to map to a Java Object
	 * @param pojoMappings the mappings which describe which fields in the {@link CSVRecord} are mapped to each JavaBean field
	 * @return a JavaBean instance based on the type of {@link CSVReader}
	 */
	private T mapToBean(final CSVRecord csvRecord, final Map<String, FieldMethodMapping> pojoMappings) {
		try {
			final T bean = this.javaBeanClass.newInstance();

			// Set all JavaBean fields mapped to a CSVField
			for (final Map.Entry<String, FieldMethodMapping> entry : pojoMappings.entrySet()) {
				final String csvRecordHeader = entry.getKey();
				final Method beanSetter = entry.getValue().getMethod();

				String value = csvRecord.get(csvRecordHeader);
				if (this.settings.isTrimWhitespace()) {
					value = StringUtils.strip(value);
				}
				beanSetter.invoke(bean, value);
			}
			return bean;
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			final String errorMessage = String.format(
					"Unable to populate values into class %s.  Please ensure this is a valid JavaBean type.", this.javaBeanClass.getName());
			LOGGER.error(errorMessage);
			// Throw this as a runtime exception as it indicates a programming error.
			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * Use reflection to determine the fields in the JavaBean that may be mapped to a CSV file header field.
	 *
	 * @param cls The JavaBean to scan for {@link CSVField} annotations
	 * @return A {@link Map} of CSV Field Header Names (key) to the JavaBean setter method for the field (value)
	 */
	private static Map<String, FieldMethodMapping> getCSVMethodMapping(final Class<?> cls) {
		final Map<String, FieldMethodMapping> fieldMap = new HashMap<>();
		for (final Field f : cls.getDeclaredFields()) {
			final CSVField annotation = f.getAnnotation(CSVField.class);

			if (annotation != null) {
				final String csvFieldName = annotation.value();
				final Method setterMethod = getSetterForField(cls, f);

				fieldMap.put(csvFieldName, new FieldMethodMapping(f.getName(), setterMethod));
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
	private static Method getSetterForField(final Class<?> parentClass, final Field field) {
		final StringBuilder methodName = new StringBuilder("set");
		methodName.append(StringUtils.capitalize(field.getName()));

		Method method = null;
		try {
			method = parentClass.getMethod(methodName.toString(), field.getType());
		} catch (NoSuchMethodException | SecurityException e) {
			final String errorMessage = String.format(
					"Unable to determine setter method for the %s field of the %s class. Please ensure this is a valid JavaBean type.",
					field.getName(), parentClass.getName());
			// Throw this as a runtime exception as it indicates a programming error.
			throw new RuntimeException(errorMessage, e);
		}
		return method;
	}

	/**
	 * Simple class to store a relation between a JavaBean field and the setter method for that field
	 *
	 * @author Sam Gardner-Dell
	 */
	private static class FieldMethodMapping {
		private final String fieldName;
		private final Method method;

		public FieldMethodMapping(final String fieldName, final Method method) {
			super();
			this.fieldName = fieldName;
			this.method = method;
		}

		/**
		 * @return the fieldName
		 */
		public String getFieldName() {
			return this.fieldName;
		}

		/**
		 * @return the method
		 */
		public Method getMethod() {
			return this.method;
		}
	}
}