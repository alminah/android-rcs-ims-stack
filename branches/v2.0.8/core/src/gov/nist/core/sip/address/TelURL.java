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
package gov.nist.core.sip.address;

/**
 * Implementation of the TelURL interface.
 * 
 * @version JAIN-SIP-1.1
 * 
 * @author M. Ranganathan <mranga@nist.gov> <br/>
 * 
 * <a href="{@docRoot}/uncopyright.html">This code is in the public domain.</a>
 * 
 */
public class TelURL extends URI {

	protected String scheme;

	protected TelephoneNumber telephoneNumber;

	/** Creates a new instance of TelURLImpl */
	public TelURL() {
		this.scheme = "tel";
	}

	/**
	 * Set the telephone number.
	 * 
	 * @param telephoneNumber --
	 *            telephone number to set.
	 */

	public void setTelephoneNumber(TelephoneNumber telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	/**
	 * Returns the value of the <code>isdnSubAddress</code> parameter, or null
	 * if it is not set.
	 * 
	 * @return the value of the <code>isdnSubAddress</code> parameter
	 */
	public String getIsdnSubAddress() {
		return telephoneNumber.getIsdnSubaddress();
	}

	/**
	 * Returns the value of the <code>postDial</code> parameter, or null if it
	 * is not set.
	 * 
	 * @return the value of the <code>postDial</code> parameter
	 */
	public String getPostDial() {
		return telephoneNumber.getPostDial();
	}

	/**
	 * Returns the value of the "scheme" of this URI, for example "sip", "sips"
	 * or "tel".
	 * 
	 * @return the scheme paramter of the URI
	 */
	public String getScheme() {
		return this.scheme;
	}

	/**
	 * Returns <code>true</code> if this TelURL is global i.e. if the TelURI
	 * has a global phone user.
	 * 
	 * @return <code>true</code> if this TelURL represents a global phone
	 *         user, and <code>false</code> otherwise.
	 */
	public boolean isGlobal() {
		return telephoneNumber.isGlobal();
	}

	/**
	 * This method determines if this is a URI with a scheme of "sip" or "sips".
	 * 
	 * @return true if the scheme is "sip" or "sips", false otherwise.
	 */
	public boolean isSipURI() {
		return false;
	}

	/**
	 * Sets phone user of this TelURL to be either global or local. The default
	 * value is false, hence the TelURL is defaulted to local.
	 * 
	 * @param global -
	 *            the boolean value indicating if the TelURL has a global phone
	 *            user.
	 */
	public void setGlobal(boolean global) {
		this.telephoneNumber.setGlobal(true);
	}

	/**
	 * Sets ISDN subaddress of this TelURL. If a subaddress is present, it is
	 * appended to the phone number after ";isub=".
	 * 
	 * @param isdnSubAddress -
	 *            new value of the <code>isdnSubAddress</code> parameter
	 */
	public void setIsdnSubAddress(String isdnSubAddress) {
		this.telephoneNumber.setIsdnSubaddress(isdnSubAddress);
	}

	/**
	 * Sets post dial of this TelURL. The post-dial sequence describes what and
	 * when the local entity should send to the phone line.
	 * 
	 * @param postDial -
	 *            new value of the <code>postDial</code> parameter
	 */
	public void setPostDial(String postDial) {
		this.telephoneNumber.setPostDial(postDial);
	}

	/**
	 * Set the telephone number.
	 * 
	 * @param telphoneNumber --
	 *            long phone number to set.
	 */
	public void setPhoneNumber(String telephoneNumber) {
		this.telephoneNumber.setPhoneNumber(telephoneNumber);
	}

	/**
	 * Get the telephone number.
	 * 
	 * @return -- the telephone number.
	 */
	public String getPhoneNumber() {
		return this.telephoneNumber.getPhoneNumber();
	}

	/**
	 * Return the string encoding.
	 * 
	 * @return -- the string encoding.
	 */
	public String toString() {
		return this.scheme + ":" + telephoneNumber.encode();
	}

	public String encode() {
		return this.scheme + ":" + telephoneNumber.encode();
	}

	/**
	 * Deep copy clone operation.
	 * 
	 * @return -- a cloned version of this telephone number.
	 */
	public Object clone() {
		TelURL retval = new TelURL();
		retval.scheme = this.scheme;
		if (this.telephoneNumber != null)
			retval.telephoneNumber = (TelephoneNumber) this.telephoneNumber
					.clone();
		return retval;
	}

}