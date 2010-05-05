/*******************************************************************************
 * Conditions Of Use
 * 
 * This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 Untied States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 * 
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS."  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof, including but
 * not limited to the correctness, accuracy, reliability or usefulness of
 * the software.
 * 
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement
 ******************************************************************************/
package gov.nist.core.sip.parser;

import gov.nist.core.ParseException;
import gov.nist.core.sip.header.CallIdHeader;
import gov.nist.core.sip.header.Header;



/**
 * Parser for CALL ID header.
 * 
 * @version JAIN-SIP-1.1
 * 
 * @author M. Ranganathan <mranga@nist.gov> <br/>
 * @author Olivier Deeruelle <deruelle@nist.gov> <br/>
 * 
 * <a href="{@docRoot}/uncopyright.html">This code is in the public domain.</a>
 * 
 */
public class CallIDParser extends HeaderParser {

	CallIDParser() {
	}

	/**
	 * Creates new CallIdHeaderParser
	 * 
	 * @param String
	 *            callID message to parse to set
	 */
	public CallIDParser(String callID) {
		super(callID);
	}

	/**
	 * Constructor
	 * 
	 * @param Lexer
	 *            lexer to set
	 */
	protected CallIDParser(Lexer lexer) {
		super(lexer);
	}

	/**
	 * parse the String message
	 * 
	 * @return Header (CallIdHeader object)
	 * @throws ParseException
	 *             if the message does not respect the spec.
	 */
	public Header parse() throws ParseException {
		this.lexer.match(TokenTypes.CALL_ID);
		this.lexer.SPorHT();
		this.lexer.match(':');
		this.lexer.SPorHT();

		CallIdHeader callID = new CallIdHeader();

		this.lexer.SPorHT();
		String rest = lexer.getRest();
		callID.setCallId(rest.trim());
		return callID;
	}
}
