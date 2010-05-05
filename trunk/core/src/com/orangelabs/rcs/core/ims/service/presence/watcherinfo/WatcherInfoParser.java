package com.orangelabs.rcs.core.ims.service.presence.watcherinfo;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.orangelabs.rcs.utils.logger.Logger;

/**
 * Watcher-info parser
 * 
 * @author jexa7410
 */
public class WatcherInfoParser extends DefaultHandler {
	/* Watcher-Info SAMPLE:
		<?xml version="1.0" encoding="UTF-8" ?>
		<watcherinfo xmlns="urn:ietf:params:xml:ns:watcherinfo" version="0" state="full">
		  <watcher-list resource="sip:+33960810100@sip.ofr.com" package="presence">
		  <watcher status="active" id="-838173480" duration-subscribed="3" event="subscribe">tel:+33960810100</watcher>
		  </watcher-list>
	    </watcherinfo>
	*/
	private StringBuffer accumulator;
	private WatcherInfoDocument watcherInfo = null;
	private Watcher watcher = null;
	
	/**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor
     * 
     * @param inputSource Input source
     * @throws Exception
     */
    public WatcherInfoParser(InputSource inputSource) throws Exception {
    	SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(inputSource, this);
	}

	public void startDocument() {
		if (logger.isActivated()) {
			logger.debug("Start document");
		}
		accumulator = new StringBuffer();
	}

	public void characters(char buffer[], int start, int length) {
		accumulator.append(buffer, start, length);
	}

	public void startElement(String namespaceURL, String localName,	String qname, Attributes attr) {
		accumulator.setLength(0);

		if (localName.equals("watcher-list")) {
			String resource = attr.getValue("resource").trim();
			String packageId = attr.getValue("package").trim();
			watcherInfo = new WatcherInfoDocument(resource, packageId);
		} else
		if (localName.equals("watcher")) {
			String id = attr.getValue("id");
			if (id != null) {
				watcher = new Watcher(id.trim());
	
				String status = attr.getValue("status");
				if (status != null) {
					watcher.setStatus(status.trim());
				}
				
				String event = attr.getValue("event");
				if (event != null) {
					watcher.setEvent(event.trim());
				}
	
				String name = attr.getValue("display-name");
				if (name != null) {
					watcher.setDisplayName(name.trim());
				}
			}
		}
	}

	public void endElement(String namespaceURL, String localName, String qname) {
		if (localName.equals("watcher")) {
			if (watcher != null) {
				watcher.setUri(accumulator.toString());
			}
			
			if (watcherInfo != null) {
				watcherInfo.addWatcher(watcher);
			}
			watcher = null;
		} else
		if (localName.equals("watcher-list")) {
			if (logger.isActivated()) {
				logger.debug("Watcher document is complete");
			}
		}
	}

	public void endDocument() {
		if (logger.isActivated()) {
			logger.debug("End document");
		}
	}

	public void warning(SAXParseException exception) {
		if (logger.isActivated()) {
			logger.error("Warning: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
		}
	}

	public void error(SAXParseException exception) {
		if (logger.isActivated()) {
			logger.error("Error: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
		}
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		if (logger.isActivated()) {
			logger.error("Fatal: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
		}
		throw exception;
	}

	public WatcherInfoDocument getWatcherInfo() {
		return watcherInfo;
	}
}
