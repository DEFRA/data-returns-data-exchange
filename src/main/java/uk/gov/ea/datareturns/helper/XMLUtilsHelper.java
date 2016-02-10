package uk.gov.ea.datareturns.helper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XMLUtilsHelper
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtilsHelper.class);

	/**
	 * Perform XSL transformation using external params
	 * @param xml
	 * @param xslt
	 * @param params
	 * @return
	 */
	public static String transformToString(String xml, String xslt, Map<String, String> params)
	{
		LOGGER.debug("xml = " + xml + ". xslt = " + xslt);

		Transformer transformer = createTransformer(xslt, params);
		
		LOGGER.debug("transformer = " + transformer);

		return transformToString(transformer, xml);
	}

	/**
	 * Perform XSL transformation
	 * @param transformer
	 * @param xml
	 * @return
	 */
	public static String transformToString(Transformer transformer, String xml)
	{
		String output = null;

		try
		{
			StringWriter outWriter = new StringWriter();
			StreamResult result = new StreamResult(outWriter);

			transformer.transform(new StreamSource(xml), result);
			StringBuffer sb = outWriter.getBuffer();

			LOGGER.debug("sb.toString() = " + sb.toString());

			output = sb.toString();
			LOGGER.debug("output = " + output);
		} catch (TransformerFactoryConfigurationError | TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output;
	}

	// TODO need "Result" type
	public static <T> T transformToResult(Transformer transformer, String xml, Class<T> clazz)
	{
		T result = null;

		try
		{
			StringReader reader = new StringReader(xml);
			StringWriter writer = new StringWriter();

			transformer.transform(new StreamSource(reader), new StreamResult(writer));

			result = xmlToResult(writer.toString(), clazz);
		} catch (TransformerFactoryConfigurationError | TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static Document documentFromString(String xml)
	{
		Document doc = null;

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			doc = builder.parse(new InputSource(new StringReader(xml)));
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doc;
	}

	public static String documentToString(Document doc)
	{
		String output = null;

		try
		{
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();

			transformer.transform(domSource, result);
			output = writer.toString();
		} catch (TransformerException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return output;
	}

	// TODO sort Type out for Object
	public static String serializeToXML(Object objIn)
	{
		return serializeToString(new XmlMapper(), objIn);
	}

	// TODO sort Type out for Object
	private static String serializeToString(ObjectMapper mapper, Object objIn)
	{
		String out = null;

		try
		{
			out = mapper.writeValueAsString(objIn);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out;
	}

	public static <T> T deserializeFromXML(String xml, Class<T> clazz)
	{
		return deserialize(new XmlMapper(), xml, clazz);
	}

	private static <T> T deserialize(ObjectMapper mapper, String xml, Class<T> clazz)
	{
		T objOut = null;

		try
		{
			objOut = mapper.readValue(xml, clazz);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return objOut;
	}

	private static <T> T xmlToResult(String xml, Class<T> clazz)
	{
		T result = null;
		ObjectMapper xmlMapper = new XmlMapper();

		try
		{
			result = xmlMapper.readValue(xml, clazz);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private static Transformer createTransformer(String xslt, Map<String, String> params)
	{
		Transformer transformer = createTransformer(xslt);

		setParams(transformer, params);

		return transformer;
	}

	static Transformer createTransformer(String xslt)
	{
		Transformer transformer = null;

		try
		{
			transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslt));
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return transformer;
	}

	private static void setParams(Transformer transformer, Map<String, String> params)
	{
		if (params == null)
		{
			return;
		}

		params.forEach((k, v) -> {
			transformer.setParameter(k, v);
		});
	}

	public static String mergeXML(String xmlTo, String xmlFrom)
	{
		Document docTo = documentFromString(xmlTo);
		Document docFrom = documentFromString(xmlFrom);

		docTo.getDocumentElement().appendChild(docTo.importNode(docFrom.getDocumentElement(), true));

		return documentToString(docTo);
	}

	// TODO move to DebugUtilsHelper class
	public static String serializeToXML(Object objIn, Map<SerializationFeature, Boolean> config)
	{
		XmlMapper mapper = new XmlMapper();

		setConfiguration(mapper, config);

		return serializeToString(mapper, objIn);
	}

	private static void setConfiguration(ObjectMapper mapper, Map<SerializationFeature, Boolean> config)
	{
		if (config == null)
		{
			return;
		}

		config.forEach((k, v) -> mapper.configure(k, v));
	}
	
	public static String serializeToJSON(Object objIn, Map<SerializationFeature, Boolean> config)
	{
		ObjectMapper mapper = new ObjectMapper();

		setConfiguration(mapper, config);

		return serializeToString(mapper, objIn);
	}
}
