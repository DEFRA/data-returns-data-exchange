package uk.gov.ea.datareturns.validate;

import static uk.gov.ea.datareturns.helper.FileUtilsHelper.loadFileAsString;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.makeFullPath;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.deserializeFromXML;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.mergeXML;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.serializeToXML;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.transformToString;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.SerializationFeature;

import uk.gov.ea.datareturns.domain.LineError;
import uk.gov.ea.datareturns.domain.SchemaErrorHandler;
import uk.gov.ea.datareturns.domain.result.ValidationResult;
import uk.gov.ea.datareturns.helper.DataExchangeHelper;

public class ValidateXML implements Validate
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidateXML.class);

	private final String XSLT_GET_SOURCE_ROW_NUMS = "get_src_row_id.xslt";
	private final String XSLT_TRANSLATE_FAILURE_MESSAGES = "translate_schema_messages.xslt";

	private String schemaFile;
	private String xmlFile;
	private String translationsFile;
	private String xsltLocation;

	public ValidateXML(String schemaFile, String xmlFile, String translationsFile, String xsltLocation)
	{
		this.schemaFile = schemaFile;
		this.xmlFile = xmlFile;
		this.translationsFile = translationsFile;
		this.xsltLocation = xsltLocation;
	}

	@Override
	public ValidationResult validate()
	{
		LOGGER.debug("Validating file '" + xmlFile + "' against schema '" + schemaFile + "'");

		ValidationResult result = validate(schemaFile, xmlFile);

//		// TODO DEBUG
//		Map<SerializationFeature, Boolean> config = new HashMap<SerializationFeature, Boolean>();
//		config.put(SerializationFeature.INDENT_OUTPUT, true);
//		System.out.println(serializeToXML(result, config));

		// If validation successful, add user friendly info to result object
		if (!result.isValid())
		{
			LOGGER.debug("File '" + xmlFile + "' is INVALID");

			// TODO exit if error count too many? - the next steps are slow
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("lineNos", result.getSchemaErrors().getErrorLineNosAsString());

			// Get source line nos
			LOGGER.debug("Getting source line nos");

			// TODO XLST needs to be made more efficient, slow with large files 
			String xsltFileLocation = makeFullPath(xsltLocation, XSLT_GET_SOURCE_ROW_NUMS);
			String lineNos = transformToString(xmlFile, xsltFileLocation, params);
			ValidationResult validationResult = (ValidationResult) deserializeFromXML(lineNos, ValidationResult.class);

			// TODO DEBUG
//			System.out.println(serializeToXML(validationResult, config));
			
			// Get user-friendly message translations
			LOGGER.debug("Getting user-friendly error message translations");
			String fullXml = mergeXML(serializeToXML(result), loadFileAsString(translationsFile));
			xsltFileLocation = makeFullPath(xsltLocation, XSLT_TRANSLATE_FAILURE_MESSAGES);
			ValidationResult translationResult = DataExchangeHelper.transformToResult(fullXml, xsltFileLocation, ValidationResult.class);

			// TODO DEBUG
//			System.out.println(serializeToXML(translationResult, config));
			
			/* NOTE : The following methods add additional info programatically despite Jackson providing
			 * "update" functionality for existing objects.
			 * Unable to use this currently as current release does not provide a "preserve" existing values 
			 * option but is due in a future release making these 2 methods redundant.
			 */

			mergeSourceLineNos(result, validationResult);
			mergeUserFriendlyMessages(result, translationResult);
		}

		// TODO DEBUG
//		System.out.println(serializeToXML(result, config));

		LOGGER.debug("File validated successfully with " + result.getSchemaErrors().getErrorCount() + " error(s)");

		return result;
	}

	/**
	 * Validates supplied XML file against it's schema.
	 * @param xsd
	 * @param xml
	 * @return
	 */
	public ValidationResult validate(String xsd, String xml)
	{
		ValidationResult transformResult = null;

		try
		{
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			Schema schema = schemaFactory.newSchema(new StreamSource(xsd));

			Validator validator = schema.newValidator();
			SchemaErrorHandler errorHandler = new SchemaErrorHandler();

			validator.setErrorHandler(errorHandler);

			StreamSource xmlFile = new StreamSource(xml);
			validator.validate(xmlFile);

			transformResult = new ValidationResult();
			transformResult.setSchemaErrors(errorHandler.getSchemaErrors());
		} catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return transformResult;
	}

	/**
	 * Merges source line nos into target object
	 * @param target
	 * @param source
	 */
	private void mergeSourceLineNos(ValidationResult target, ValidationResult source)
	{
		LOGGER.debug("Merging source line nos");

		Map<String, LineError> targetErrors = target.getSchemaErrors().getLineErrors();

		if (targetErrors.size() > 0)
		{
			Map<String, LineError> sourceErrors = source.getSchemaErrors().getLineErrors();

			// May not be all validation errors so won't have output line no
			if (sourceErrors != null)
			{
				targetErrors.forEach((key, targetError) -> {
					targetError.setOutputLineNo(sourceErrors.get(key).getOutputLineNo());
					targetError.setErrorValue(sourceErrors.get(key).getErrorValue());
				});
			}
		}
	}

	/**
	 * Merges user-friendly error messages into target object
	 * @param target
	 * @param source
	 */
	private static void mergeUserFriendlyMessages(ValidationResult target, ValidationResult source)
	{
		LOGGER.debug("Merging user-friendly error message translations");

		Map<String, LineError> targetErrors = target.getSchemaErrors().getLineErrors();

		if (targetErrors.size() > 0)
		{
			Map<String, LineError> sourceErrors = source.getSchemaErrors().getLineErrors();

			targetErrors.forEach((key, targetError) -> {
				targetError.getErrorDetail().setOutputMessage(sourceErrors.get(key).getErrorDetail().getOutputMessage());
			});
		}
	}
}
