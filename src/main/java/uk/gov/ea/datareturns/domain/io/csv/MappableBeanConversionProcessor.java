/**
 *
 */
package uk.gov.ea.datareturns.domain.io.csv;

import com.samskivert.mustache.Template;
import com.univocity.parsers.common.CommonSettings;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import uk.gov.ea.datareturns.util.MustacheTemplates;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Allows the output of fields from a bean to be customised based on a hashmap containing mappings.
 * 
 * E.g.
 * 
 * Given this hashmap:
 * 
 * EA_ID={{EA_ID}}
 * Variable={{Parameter}} ({{Unit}})
 * Value={{Value}}
 *
 * Three columns would be output.
 *   - the first column would be headed EA_ID and would contain the EA_ID data from the input without any changes
 *   - the second column would be headed Variable and would contain the Parameter and the Unit data from the input.  Unit would be displayed in brackets, e.g. (mg)
 *   - the third column would be headed Value and would contain the Value data from the input without any changes
 *   
 * @author Sam Gardner-Dell
 * @param <T> the bean class to be serialized to CSV
 */
public class MappableBeanConversionProcessor<T> extends BeanWriterProcessor<T> {
	private final Map<String, String> outputTemplates;

	/** The set of all fields in the bean, based on the values that were read in.  Defines the set of tokens that can be used in the output data */
	private final String[] beanFields;

	/**
	 * Initialises the BeanWriterProcessor with the annotated bean class and mapping String
	 * @param beanType the class annotated with one or more of the annotations provided in {@link com.univocity.parsers.annotations}.
	 * @param outputTemplates the mustache template text for each output heading
	 * @param beanFields the set of all fields contained by the bean (the set of headers used when reading the bean)
	 */
	public MappableBeanConversionProcessor(final Class<T> beanType, final Map<String, String> outputTemplates, final String[] beanFields) {
		super(beanType);
		this.outputTemplates = outputTemplates;
		this.beanFields = beanFields;
	}

	/**
	 * Converts the java bean instance into a sequence of values for writing.
	 *
	 * @param input an instance of the type defined in this class constructor.
	 * @param headers All field names used to produce records in a given destination. May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @return a row of objects containing the values extracted from the java bean
	 */
	@Override
	public Object[] write(final T input, final String[] headers, final int[] indexesToWrite) {
		super.initialize();
		final Object[] reverseMappedValues = reverseConversions(input, this.beanFields, indexesToWrite);
		final Map<String, String> mappings = new HashMap<>();
		for (int i = 0; i < this.beanFields.length; i++) {
			mappings.put(this.beanFields[i], Objects.toString(reverseMappedValues[i], ""));
		}

		final String[] outputValues = new String[outputTemplates.size()];
		int index = 0;
		for (final Map.Entry<String, String> entry : outputTemplates.entrySet()) {
			final Template tmpl = MustacheTemplates.get(entry.getKey(), entry.getValue());
			outputValues[index++] = tmpl.execute(mappings);
		}
		return outputValues;
	}
}