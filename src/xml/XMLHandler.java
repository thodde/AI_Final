/**
 * Taken directly and completely from Professor Heineman's fusion project Draw2Choose, 
 * from the java project ClientServer 
 */

package xml;

import java.util.ArrayList;
import org.xml.sax.*;

/** Accumulate all errors and enable retrieval in failFast. */
public class XMLHandler implements ErrorHandler{
  ArrayList<String> errors = new ArrayList<String>();
  
  /** Keep record of all errors and continue until failFast is called. */
  public void error(SAXParseException spe) throws SAXException {
    errors.add(spe.toString());
  }

  /** Fail immediately with fatal errors. */
  public void fatalError(SAXParseException spe) throws SAXException {
    throw spe;
  }

  /** Emit warnings as they come and otherwise ignore. */
  public void warning(SAXParseException spe) throws SAXException {
    System.out.println("WARNING: " + spe.toString());
  }

  /** Terminate immediately upon detecting any XML errors. */
  public void failFast() {
    if (errors.size() == 0) {
      return;
    }
    
    for (String s : errors) {
      System.out.println("ERROR:" + s);
    }
    errors.clear();
    throw new RuntimeException ("Parsing Failed");
  }
}

