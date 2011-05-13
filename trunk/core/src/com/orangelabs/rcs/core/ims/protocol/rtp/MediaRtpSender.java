/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright © 2010 France Telecom S.A.
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

package com.orangelabs.rcs.core.ims.protocol.rtp;



import com.orangelabs.rcs.core.ims.protocol.rtp.codec.Codec;
import com.orangelabs.rcs.core.ims.protocol.rtp.format.Format;
import com.orangelabs.rcs.core.ims.protocol.rtp.media.MediaInput;
import com.orangelabs.rcs.core.ims.protocol.rtp.stream.MediaCaptureStream;
import com.orangelabs.rcs.core.ims.protocol.rtp.stream.RtpOutputStream;
import com.orangelabs.rcs.platform.network.DatagramConnection;
import com.orangelabs.rcs.utils.logger.Logger;

/**
 * Media RTP sender
 */
public class MediaRtpSender {
	/**
	 * Format
	 */
	private Format format;

    /**
     * Media processor
     */
    private Processor processor = null;

    /**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor
     *
     * @param format Media format
     */
    public MediaRtpSender(Format format) {
    	this.format = format;
    }

    /**
     * Prepare the RTP session
     *
     * @param player Media player
     * @param remoteAddress Remote address
     * @param remotePort Remote port
     * @throws RtpException
     */
    public void prepareSession(MediaInput player, String remoteAddress, int remotePort) throws RtpException {
    	try {
    		// Create the input stream
    		MediaCaptureStream inputStream = new MediaCaptureStream(format, player);
    		inputStream.open();
			if (logger.isActivated()) {
				logger.debug("Input stream: " + inputStream.getClass().getName());
			}

            // Create the output stream
        	RtpOutputStream outputStream = new RtpOutputStream(remoteAddress, remotePort);
    		outputStream.open();
			if (logger.isActivated()) {
				logger.debug("Output stream: " + outputStream.getClass().getName());
			}

        	// Create the codec chain
        	Codec[] codecChain = MediaRegistry.generateEncodingCodecChain(format.getCodec());

            // Create the media processor
    		processor = new Processor(inputStream, outputStream, codecChain);

        	if (logger.isActivated()) {
        		logger.debug("Session has been prepared with success");
            }
        } catch(Exception e) {
        	if (logger.isActivated()) {
        		logger.error("Can't prepare resources correctly", e);
        	}
        	throw new RtpException("Can't prepare resources");
        }
    }

    /**
     * Prepare the RTP session
     *
     * @param player Media player
     * @param remoteAddress Remote address
     * @param remotePort Remote port
     * @param connection the connection for Symmetric RTP
     * @throws RtpException
     */
    public void prepareSession(MediaInput player, String remoteAddress, int remotePort,
            DatagramConnection connection) throws RtpException {
        try {
            // Create the input stream
            MediaCaptureStream inputStream = new MediaCaptureStream(format, player);
            inputStream.open();
            if (logger.isActivated()) {
                logger.debug("Input stream: " + inputStream.getClass().getName());
            }

            // Create the output stream
            RtpOutputStream outputStream = new RtpOutputStream(remoteAddress, remotePort);
            outputStream.open(connection);
            if (logger.isActivated()) {
                logger.debug("Output stream: " + outputStream.getClass().getName());
            }

            // Create the codec chain
            Codec[] codecChain = MediaRegistry.generateEncodingCodecChain(format.getCodec());

            // Create the media processor
            processor = new Processor(inputStream, outputStream, codecChain);

            if (logger.isActivated()) {
                logger.debug("Session has been prepared with success");
            }
        } catch (Exception e) {
            if (logger.isActivated()) {
                logger.error("Can't prepare resources correctly", e);
            }
            throw new RtpException("Can't prepare resources");
        }
    }

    /**
     * Start the RTP session
     */
    public void startSession() {
    	if (logger.isActivated()) {
    		logger.debug("Start the session");
    	}

    	// Start the media processor
		if (processor != null) {
			processor.startProcessing();
		}
    }

    /**
     * Stop the RTP session
     */
    public void stopSession() {
    	if (logger.isActivated()) {
    		logger.debug("Stop the session");
    	}

    	// Stop the media processor
		if (processor != null) {
			processor.stopProcessing();
		}
    }
}