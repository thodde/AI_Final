/**
 * Taken directly and completely from Professor Heineman's fusion project Draw2Choose, 
 * from the java project ClientServer 
 */

package xml;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import javax.xml.validation.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import xml.XMLSegment.SegmentType;


/** Support class for XML parsing of requests and responses. */
public class Message {
	static DocumentBuilder builder = null;              // builder for parsing XML strings
	static XMLHandler errorHandler = new XMLHandler();  // we must provide an error handler
	static Transformer transformer;                     // to convert to string
	public final Node contents;                         // root of DOM containing parsed XML

	/** Configure builder at first use. */
	public static boolean configure (String schema) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try { 
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.setSchema(sf.newSchema(new Source[] {new StreamSource(schema)}));
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(errorHandler);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/** Parse XML and construct Message object only if it succeeds. */
	public Message (String xmlSource) throws IllegalArgumentException {
		if (builder == null) {
			throw new RuntimeException ("XML Protocol not configured.");
		}

		try {
			String normalizedInput = normalizeInput(xmlSource);
			InputSource is = new InputSource (new StringReader (normalizedInput));
			//InputSource is = new InputSource (new StringReader (xmlSource));

			// parse method in builder is not thread safe.
			Document d = null;
			synchronized (builder) {
				d = builder.parse(is);
				errorHandler.failFast();
			}

			// Grab first (and only) child (either request or response)
			NodeList children = d.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node n = children.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					contents = n;
					return;
				}
			}
			throw new IllegalArgumentException ("XML document has no child node");
		} catch (Exception e) { 
			errorHandler.failFast();
			throw new IllegalArgumentException (e.getMessage());
		} 
	}

	/** Represent message as a string. */
	public String toString() {
		DOMSource domSource = new DOMSource(contents);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (Exception e) {
			return "";
		}
	}  

	/**  
	 * Custom function to remove stray characters that throw off the building of the node structure
	 * 
	 * @param input - the string to be normalized from valid characters that disrupt the XML Message architecture
	 * @return a normalized string with formatting closer to the desired XML Message architecture
	 */
	public static String normalizeInput(String input) {
		ArrayList<XMLSegment> mySegments = new ArrayList<XMLSegment>();
		int curIndex = 0;

		try {
			XMLSegment newSegment;
			
			while (curIndex < input.length()) {
				newSegment = new XMLSegment();
				curIndex = newSegment.readSegment(input, curIndex);
				mySegments.add(newSegment);
			}
		} catch (Exception e) {
			System.out.println("error while normalizing string.");
			return input;
		}

		String output = "";
		XMLSegment previousSegment;
		XMLSegment currentSegment = null;
		for(int i = 0; i < mySegments.size(); i++) {
			previousSegment = currentSegment;
			currentSegment = mySegments.get(i);
			
			if (previousSegment == null)
				output += currentSegment.formattedString;
			else {
				if (previousSegment.myType == SegmentType.NODECLOSE || previousSegment.myType == SegmentType.NODEINLINECLOSE)
					output += currentSegment.formattedString;
				else if ((previousSegment.myType == SegmentType.NODEDECLARATION || previousSegment.myType == SegmentType.ATTRIBUTE) && 
						(currentSegment.myType == SegmentType.ATTRIBUTE))
					output += " " + currentSegment.formattedString;
				else if ((previousSegment.myType == SegmentType.NODEDECLARATION || previousSegment.myType == SegmentType.ATTRIBUTE) && 
						(currentSegment.myType == SegmentType.NODECLOSE || currentSegment.myType == SegmentType.NODEINLINECLOSE))
					output += currentSegment.formattedString;
				else if (currentSegment.myType == SegmentType.UNKNOWN || previousSegment.myType == SegmentType.UNKNOWN)
					output += " " + currentSegment.rawString;
			}
		}
		
		return output;
	}

	/** Determine the success of the given message. */
	public boolean success() {
		return Boolean.valueOf(contents.getAttributes().getNamedItem(Parser.success).getNodeValue());
	}

	/** Determine the reason for failure of the message. */
	public String reason() {
		Node r = contents.getAttributes().getNamedItem(Parser.reason);
		if (r == null) { return ""; }
		return r.getNodeValue();
	}

	/** Determine the id for the message. */
	public String id() {
		Node r = contents.getAttributes().getNamedItem(Parser.id);
		if (r == null) { return ""; }
		return r.getNodeValue();
	}
	
	/** Create standard request XML header string with built-in (statistically) unique id. */
	public static String requestHeader() {
		String id = UUID.randomUUID().toString();
		return "<request id='" + id + "'>";
	}
	
	/** Create standard response XML header string for a successful response. */
	public static String responseHeader(String id) {
		return "<response id = '" + id + "' success='true'>";
	}
	
	/** Create standard response XML header string for a failed response. */
	public static String responseHeader(String id, String reason) {
		return "<response id='" + id + "' success='false' reason='" + reason + "'>";
	}
	
}
