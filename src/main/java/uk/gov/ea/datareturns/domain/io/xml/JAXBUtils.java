package uk.gov.ea.datareturns.domain.io.xml;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class JAXBUtils {
	private static Map<String, JAXBContext> contextMap = Collections.synchronizedMap(new HashMap<>());
	
	public static void registerContext(Class<?> cls) throws JAXBException {
		contextMap.put(cls.getName(), JAXBContext.newInstance(cls));
	}
	
	public static void marshal(Object o, OutputStream out) throws JAXBException {
		JAXBContext jaxbContext = contextMap.get(o.getClass().getName());
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(o, out);
	}
}
