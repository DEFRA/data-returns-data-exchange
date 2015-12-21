package uk.gov.ea.datareturns.domain;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * @author adrianharrison
 * Records "selected" errors generated during Schema validation
 */
public class SchemaErrors
{
	public static final String ID_PREFIX = "Line_";
	private String regex = "'(.*?)'";
	private Pattern p = Pattern.compile(regex);

	@JacksonXmlElementWrapper(useWrapping = false)
	private Map<String, LineError> lineErrors;

	public SchemaErrors()
	{
		this.lineErrors = new TreeMap<String, LineError>();
	}

	@JacksonXmlProperty(localName = "LineErrors")
	public Map<String, LineError> getLineErrors()
	{
		return lineErrors;
	}

	/**
	 * Stores a single XML Element error generated from Xerces. 
	 * Multiple messages can be generated, ones beginning "cvc-complex-type" contain the source column name.
	 * @param lineNo
	 * @param errorLevel
	 * @param errorMessage
	 */
	public void addLineError(int lineNo, String errorLevel, String errorMessage)
	{
		if (errorMessage.startsWith("cvc-complex-type"))
		{
			String key = null;
			Matcher m = p.matcher(errorMessage);

			// Column name found
			if (!m.find())
			{
				// TODO Auto-generated catch block
				throw new RuntimeException("can't find column name!");
			}

			String columnName = m.group(1);
			key = makeKey(Integer.toString(lineNo));

			LineError err = new LineError(columnName, Integer.toString(lineNo), errorLevel, errorMessage);

			this.lineErrors.put(key, err);
		}
	}

	/** 
	 * Return total error count
	 * @return
	 */
	@JacksonXmlProperty(localName = "ErrorCount")
	public int getErrorCount()
	{
		return lineErrors.size();
	}

	/**
	 * Returns a comma separated list of XML line numbers.
	 * @return
	 */
	@JsonIgnore
	public String getErrorLineNosAsString()
	{
		StringBuilder lineNos = new StringBuilder();

		lineErrors.forEach((k, v) -> {
			lineNos.append(v.getInputLineNo() + ",");

		});

		return lineNos.toString().substring(0, lineNos.toString().length() - 1);
	}

	/**
	 * Make Map key value in the format "Line_999" where '999' is XML line no
	 * @param id
	 * @return
	 */
	private String makeKey(String id)
	{
		return ID_PREFIX + id;
	}
}