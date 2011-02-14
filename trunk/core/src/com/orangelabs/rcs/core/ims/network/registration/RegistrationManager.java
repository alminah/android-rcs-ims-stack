/*******************************************************************************
 * Software Name : RCS IMS Stack
 * Version : 2.0
 * 
 * Copyright � 2010 France Telecom S.A.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.orangelabs.rcs.core.ims.network.registration;

import java.util.ListIterator;
import java.util.Vector;

import javax.sip.header.ContactHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.ExtensionHeader;
import javax.sip.header.Header;

import com.orangelabs.rcs.core.ims.ImsError;
import com.orangelabs.rcs.core.ims.ImsModule;
import com.orangelabs.rcs.core.ims.network.ImsNetworkInterface;
import com.orangelabs.rcs.core.ims.network.sip.SipManager;
import com.orangelabs.rcs.core.ims.network.sip.SipMessageFactory;
import com.orangelabs.rcs.core.ims.network.sip.SipUtils;
import com.orangelabs.rcs.core.ims.protocol.sip.SipDialogPath;
import com.orangelabs.rcs.core.ims.protocol.sip.SipRequest;
import com.orangelabs.rcs.core.ims.protocol.sip.SipResponse;
import com.orangelabs.rcs.core.ims.protocol.sip.SipTransactionContext;
import com.orangelabs.rcs.platform.registry.RegistryFactory;
import com.orangelabs.rcs.provider.settings.RcsSettings;
import com.orangelabs.rcs.utils.PeriodicRefresher;
import com.orangelabs.rcs.utils.logger.Logger;

/**
 * Registration manager (register, re-register, un-register)
 *
 * @author JM. Auffret
 */
public class RegistrationManager extends PeriodicRefresher {
	/**
	 * Last min expire period (in seconds)
	 */
	private static final String REGISTRY_MIN_EXPIRE_PERIOD = "MinRegisterExpirePeriod";
	
    /**
     * Expire period
     */
    private int expirePeriod;

    /**
     * Dialog path
     */
    private SipDialogPath dialogPath = null;

    /**
     * IMS network interface
     */
    private ImsNetworkInterface networkInterface;
 
    /**
     * Registration procedure
     */
    private RegistrationProcedure registrationProcedure;
    
	/**
     * Registration flag
     */
    private boolean registered = false;
   
    /**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * Constructor
     * 
     * @param networkInterface IMS network interface
     */
    public RegistrationManager(ImsNetworkInterface networkInterface) {
    	this.networkInterface = networkInterface;

    	int defaultExpirePeriod = RcsSettings.getInstance().getRegisterExpirePeriod();
    	int minExpireValue = RegistryFactory.getFactory().readInteger(REGISTRY_MIN_EXPIRE_PERIOD, -1);
    	if ((minExpireValue != -1) && (defaultExpirePeriod < minExpireValue)) {
        	this.expirePeriod = minExpireValue;
    	} else {
    		this.expirePeriod = defaultExpirePeriod;
    	}
    	
        this.registrationProcedure = loadRegistrationProcedure();
               
        if (logger.isActivated()) {
        	logger.info("Registration manager started");
        }
    }

    /**
     * Is GIBA authentication procedure used
     * 
     * @return Boolean
     */
    public static boolean isGibaAuthent() {
    	return RcsSettings.getInstance().getImsAuhtenticationProcedure().equals("GIBA");
    }    
    
    /**
     * Load the registration procedure
     * 
     * @return Registration procedure instance
     */
    private RegistrationProcedure loadRegistrationProcedure() {
		if (RegistrationManager.isGibaAuthent()) {
	        if (logger.isActivated()) {
	        	logger.debug("Load GIBA authent procedure");
	        }
			return new GibaRegistrationProcedure();
		} else {
	        if (logger.isActivated()) {
	        	logger.debug("Load HTTP Digest MD5 authent procedure");
	        }
			return new HttpDigestRegistrationProcedure();
		}
    }

    /**
     * Is registered
     * 
     * @return Return True if the terminal is registered, else return False
     */
    public boolean isRegistered() {
        return registered;
    }
    
    /**
     * Registration
     * 
     * @return Boolean status
     */
    public synchronized boolean registration() {
        try {
            // Create a dialog path if necessary
            if (dialogPath == null) {
                // Set Call-Id
            	String callId = networkInterface.getSipManager().getSipStack().generateCallId();

            	// Set target
            	String target = "sip:" + registrationProcedure.getHomeDomain();

                // Set local party
            	String localParty = registrationProcedure.getPublicUri();

                // Set remote party
            	String remoteParty = registrationProcedure.getPublicUri();

            	// Set the route path
            	Vector<String> route = networkInterface.getSipManager().getSipStack().getDefaultRoutePath();

            	// Create a dialog path
            	dialogPath = new SipDialogPath(
                		networkInterface.getSipManager().getSipStack(),
                		callId,
                		1,
                		target,
                		localParty,
                		remoteParty,
                		route);
            } else {
    	    	// Increment the Cseq number of the dialog path
    	        dialogPath.incrementCseq();
            }

            // Send REGISTER request
            SipRequest register = SipMessageFactory.createRegister(dialogPath,
            		expirePeriod,
            		networkInterface.getAccessInfo());
            sendRegister(register);
            
        } catch (Exception e) {
        	if (logger.isActivated()) {
        		logger.error("Registration has failed", e);
        	}
        	handleError(new ImsError(ImsError.UNEXPECTED_EXCEPTION, e.getMessage()));
        }

        return registered;
    }
    
    /**
     * Stop the registration manager without unregistering from IMS
     */
    public synchronized void stopRegistration() {
    	if (!registered) {
			// Already unregistered
			return;
    	}    	

    	// Stop periodic registration
        stopTimer();

        // Force registration flag to false
        registered = false;

        // Reset dialog path attributes
        resetDialogPath();
        
        // Notify event listener
        networkInterface.getImsModule().getCore().getListener().handleRegistrationTerminated();
    }
    
    /**
     * Unregistration
     */
    public synchronized void unRegistration() {
    	if (!registered) {
			// Already unregistered
			return;
    	}    	
    	
        try {
            // Stop periodic registration
            stopTimer();

            // Increment the Cseq number of the dialog path
            dialogPath.incrementCseq();
            
            // Send UNREGISTER request
            SipRequest register = SipMessageFactory.createRegister(dialogPath,
            		0,
            		networkInterface.getAccessInfo());
            sendRegister(register);

        } catch (Exception e) {
        	if (logger.isActivated()) {
        		logger.error("Unregistration has failed", e);
        	}
        }

        // Force registration flag to false
        registered = false;

        // Reset dialog path attributes
        resetDialogPath();
        
        // Notify event listener
        networkInterface.getImsModule().getCore().getListener().handleRegistrationTerminated();
    }

	/**
	 * Send REGISTER message
	 * 
	 * @param register SIP REGISTER
	 * @throws Exception
	 */
	private void sendRegister(SipRequest register) throws Exception {
        // Send a REGISTER
        if (logger.isActivated()) {
        	logger.info("Send REGISTER, expire=" + register.getExpires());
        }

        if (registered) {
	        // Set the security header
        	registrationProcedure.writeSecurityHeader(register);
        }
        
        // Send message
        SipTransactionContext ctx = networkInterface.getSipManager().sendSipMessageAndWait(register);

        // Wait response
        if (logger.isActivated()) {
        	logger.info("Wait response");
        }
        ctx.waitResponse(SipManager.TIMEOUT);
        
        // Analyze the received response 
        if (ctx.isSipResponse()) {
        	// A response has been received
            if (ctx.getStatusCode() == 200) {
            	// 200 OK
        		if (register.getExpires() != 0) {
        			handle200OK(ctx);
        		} else {
        			handle200OkUnregister(ctx);
        		}
            } else
            if (ctx.getStatusCode() == 401) {
            	// 401 Unauthorized
            	handle401Unauthorized(ctx);
            } else
            if (ctx.getStatusCode() == 423) {
            	// 423 Interval Too Brief
            	handle423IntervalTooBrief(ctx);
            } else {
            	// Other error response
    			handleError(new ImsError(ImsError.REGISTRATION_FAILED,
    					ctx.getStatusCode() + " " + ctx.getReasonPhrase()));    					
            }
        } else {
        	// No response received: timeout
        	handleError(new ImsError(ImsError.REGISTRATION_FAILED, "timeout"));
        }
	}    

	/**
	 * Handle 200 0K response 
	 * 
	 * @param ctx SIP transaction context
	 * @throws Exception
	 */
	private void handle200OK(SipTransactionContext ctx) throws Exception {
        // 200 OK response received
    	if (logger.isActivated()) {
    		logger.info("200 OK response received");
    	}

    	SipResponse resp = ctx.getSipResponse();
    	
        // Set the associated URI
		ExtensionHeader associatedHeader = (ExtensionHeader)resp.getHeader(SipUtils.HEADER_P_ASSOCIATED_URI);
		String associatedUri = associatedHeader.getValue();
		ImsModule.IMS_USER_PROFILE.setPublicUri(associatedUri);
		
        // Set the service route path
		ListIterator<Header> routes = resp.getHeaders(SipUtils.HEADER_SERVICE_ROUTE);
		networkInterface.getSipManager().getSipStack().setServiceRoutePath(routes);
		
        // Read the security header
    	registrationProcedure.readSecurityHeader(resp);

        // Retrieve the expire value in the response
        retrieveExpirePeriod(resp);
        registered = true;
        
        // Start the periodic registration
        startTimer(expirePeriod, 0.5);
    	
        // Notify event listener
        networkInterface.getImsModule().getCore().getListener().handleRegistrationSuccessful();
	}	
	
	/**
	 * Handle 200 0K response of UNREGISTER
	 * 
	 * @param ctx SIP transaction context
	 */
	private void handle200OkUnregister(SipTransactionContext ctx) {
        // 200 OK response received
        if (logger.isActivated()) {
            logger.info("200 OK response received");
        }
	}

	/**
	 * Handle 401 response 
	 * 
	 * @param ctx SIP transaction context
	 * @throws Exception
	 */
	private void handle401Unauthorized(SipTransactionContext ctx) throws Exception {
		// 401 response received
    	if (logger.isActivated()) {
    		logger.info("401 response received");
    	}

    	SipResponse resp = ctx.getSipResponse();

        // Read the security header
    	registrationProcedure.readSecurityHeader(resp);

        // Increment the Cseq number of the dialog path
        dialogPath.incrementCseq();

        // Send second REGISTER request with security token
        if (logger.isActivated()) {
        	logger.info("Send second REGISTER");
        }
        SipRequest register = SipMessageFactory.createRegister(dialogPath, expirePeriod, networkInterface.getAccessInfo());
        
        // Set the security header
        registrationProcedure.writeSecurityHeader(register);

        // Send request
        sendRegister(register);
	}	

	/**
	 * Handle 423 response 
	 * 
	 * @param ctx SIP transaction context
	 */
	private void handle423IntervalTooBrief(SipTransactionContext ctx) throws Exception {
		// 423 response received
    	if (logger.isActivated()) {
    		logger.info("423 response received");
    	}

    	SipResponse resp = ctx.getSipResponse();

        if (logger.isActivated()) {
        	logger.info("Send second REGISTER");
        }

        // Extract the Min-Expire value
        int minExpire = SipUtils.getMinExpiresPeriod(resp);
        if (minExpire == -1) {
            if (logger.isActivated()) {
            	logger.error("Can't read the Min-Expires value");
            }
        	handleError(new ImsError(ImsError.UNEXPECTED_EXCEPTION, "No Min-Epires value found"));
        	return;
        }
        
        // Save the min expire value in the terminal registry
        RegistryFactory.getFactory().writeInteger(REGISTRY_MIN_EXPIRE_PERIOD, minExpire);
        
        // Set the expire value
    	expirePeriod = minExpire;
        
        // Send a new REGISTER with the right expire period
        if (logger.isActivated()) {
        	logger.info("Send new REGISTER");
        }
        SipRequest register = SipMessageFactory.createRegister(dialogPath, expirePeriod, networkInterface.getAccessInfo());
        
        // Set the security header
        registrationProcedure.writeSecurityHeader(register);

        // Send message
        sendRegister(register);
	}	
	
	/**
	 * Handle error response 
	 * 
	 * @param error Error
	 */
	private void handleError(ImsError error) {
        // Error
    	if (logger.isActivated()) {
    		logger.info("Registration has failed: " + error.getErrorCode() + ", reason=" + error.getMessage());
    	}
        registered = false;
        
        // Registration has failed, stop the periodic registration
		stopTimer();
    	
        // Reset dialog path attributes
        resetDialogPath();
        
        // Notify event listener
        networkInterface.getImsModule().getCore().getListener().handleRegistrationFailed(error);
	}
	
	/**
     * Reset the dialog path
     */
    private void resetDialogPath() {
        dialogPath = null;
    }

    /**
     * Retrieve the expire period in the contact header or in the expire header
     * 
     * @param response SIP response
     */
    private void retrieveExpirePeriod(SipResponse response) {
    	// Extract expire value from Contact header
        ContactHeader contactHeader = (ContactHeader)response.getHeader(ContactHeader.NAME);
	    if (contactHeader != null) {
	    	int expires = contactHeader.getExpires();
		    if (expires != -1) {
	    		expirePeriod = expires;            
	    	}
		    return;
	    }
	    
        // Extract expire value from Expires header
        ExpiresHeader expiresHeader = (ExpiresHeader)response.getHeader(ExpiresHeader.NAME);
    	if (expiresHeader != null) {
    		int expires = expiresHeader.getExpires();
		    if (expires != -1) {
	    		expirePeriod = expires;
	    		return;
	    	}
        }
    }

	/**
     * Registration processing
     */
    public void periodicProcessing() {
        // Make a registration
    	if (logger.isActivated()) {
    		logger.info("Execute re-registration");
    	}
        registration();
    }
}
