package org.jaxb.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class STAXReader {
	 public static String getRootTag(XMLStreamReader xsr) throws XMLStreamException {
			while (xsr.hasNext() && !xsr.isStartElement()) {
	        	xsr.nextTag();
	        	if (xsr.isStartElement()) {
	        		return xsr.getLocalName();
	        	} 
	        }
			return null;
		}
		
	   public static boolean advance(XMLStreamReader xsr, String rootElement, String tag) throws XMLStreamException {
			while (xsr.hasNext()) {
	        	
	        	if (xsr.isEndElement() && xsr.getLocalName().equals(rootElement)) {
	        		break;
	        	}
	        	
	        	xsr.nextTag();
	        	
	        	if (xsr.isStartElement() && xsr.getLocalName().equals(tag)) {
	        		return true;
	        	}
			} 	
			return false;
		}
}
