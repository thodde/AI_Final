package xml;


public class XMLSegment {
	static enum SegmentType { UNKNOWN, NODEDECLARATION, NODEINLINECLOSE, NODECLOSE, ATTRIBUTE; } 

	public SegmentType myType;
	public String rawString;
	public String formattedString;
	
	public XMLSegment() {
		myType = SegmentType.UNKNOWN;
		rawString = "";
		formattedString = "";
	}
	
	/**
	 * This method reads and parses individual XML segments
	 * 
	 * @param inString - the message to be read in
	 * @param index - the character that has been read up until
	 * @return false if end of file is reached.  True otherwise.
	 */
	public int readSegment(String inString, int index) {
		char charIn;
		boolean inQuotes = false;
		boolean done = false;
		
		while (!done) {
			charIn = inString.charAt(index++);
			
			if (index == inString.length())
				done = true;
			
			if (inQuotes) 
				rawString += charIn;
			else if ((charIn == '<') && (rawString.length() > 0)) {
				done = true;
				index--;
			}
			else if ((charIn == '\n') || (charIn == '\r') || (charIn == '\t'))
				; //ignore
			else if (charIn == ' ') {
				if (rawString.length() > 0)
					done = true;
			}
			else if ((charIn == '>') && (rawString.length() > 0)) {
				index--;
				done = true;
			}
			else if ((charIn == '/' && inString.charAt(index) == '>')) {
				if (rawString.length() > 0) {
					index--;
					done = true;
				}
				else {
					rawString += "/>";
					index++;
					done = true;
				}
			}
			else
				rawString += charIn;
				
			if ((charIn == '"') || (charIn == '\''))
				inQuotes = !inQuotes;
		}
		
		formatInput();
		
		return index;
	}

	private boolean formatInput() {
		if (rawString.length() == 0) {
			return false;
		}
		if (rawString.length() == 1) {
			if (rawString.equals(">")) {
				myType = SegmentType.NODECLOSE;
				formattedString = rawString;
				return true;
			}
		}
		else if (rawString.length() == 2) {
			if (rawString.equals("/>")) {
				myType = SegmentType.NODEINLINECLOSE;
				formattedString = rawString;
				return true;
			}
		}
		
		if (rawString.charAt(0) == '<') {
			myType = SegmentType.NODEDECLARATION;
			formattedString = rawString;
			return true;
		}
		
		for (int i = 0; i < rawString.length(); i++) {
			if ((rawString.charAt(i) == '\'') || (rawString.charAt(i) == '"')) {
				myType = SegmentType.ATTRIBUTE;
				formattedString = rawString;
				return true;
			}
		}
		
		//error unknown type
		return true;
	}

}
