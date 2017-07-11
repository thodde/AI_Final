/**
 * Taken directly and completely from Professor Heineman's fusion project Draw2Choose, 
 * from the java project ClientServer 
 */

package xml;

import java.io.*;


public class Parser {

	/** Common id across request/responses. */
	public final static String id = "id";
	
	/** Attributes in responses. */
	public final static String success = "success";
	public final static String reason = "reason";

	/** Connect initial request. */
	public final static String connectRequest = "connectRequest";
	public final static String connectResponse = "connectResponse";

	/** Return extracted request. */
	public static Message extractRequest(BufferedReader in) {
		return extractMessage(in, "</request>");
	}

	/** Return extracted response. */
	public static Message extractResponse(BufferedReader in) {
		return extractMessage(in, "</response>");
	}

	/** 
	 * Extract the XML message and construct validated Message object based on
	 * the terminator string (either "</request>" or "</response>"). Returns 
	 * null if communication is interrupted in any way.
	 */
	static Message extractMessage(BufferedReader in, String terminator) {
		try {
			String line = in.readLine();
			if (line == null) { return null; }
			StringBuilder buf = new StringBuilder(line);
			while (!buf.substring(buf.length()-terminator.length(), buf.length()).equals(terminator)) {
				line = in.readLine();
				if (line == null) { return null; }
				buf.append(line);
			}

			return new Message (buf.toString());
		} catch (IOException ioe) {
			return null;
		}
	}

}

