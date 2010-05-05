package com.orangelabs.rcs.service.api.server.toip;

import com.orangelabs.rcs.core.Core;
import com.orangelabs.rcs.core.ims.service.ImsServiceSession;
import com.orangelabs.rcs.core.media.MediaPlayer;
import com.orangelabs.rcs.core.media.MediaRenderer;
import com.orangelabs.rcs.service.api.client.IMediaPlayer;
import com.orangelabs.rcs.service.api.client.IMediaRenderer;
import com.orangelabs.rcs.service.api.client.toip.IToIpApi;
import com.orangelabs.rcs.service.api.client.toip.IToIpSession;
import com.orangelabs.rcs.service.api.server.RemoteMediaPlayer;
import com.orangelabs.rcs.service.api.server.RemoteMediaRenderer;
import com.orangelabs.rcs.service.api.server.ServerApiException;
import com.orangelabs.rcs.service.api.server.ServerApiUtils;
import com.orangelabs.rcs.utils.logger.Logger;

/**
 * ToIP API service
 * 
 * @author jexa7410
 */
public class ToIpApiService extends IToIpApi.Stub {
    /**
	 * The logger
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor
	 */
	public ToIpApiService() {
		if (logger.isActivated()) {
			logger.info("ToIP API is loaded");
		}
	}

	/**
	 * Close API
	 */
	public void close() {
	}
	
	/**
	 * Initiate a ToIP call
	 * 
	 * @param contact Contact
	 * @param player Media player
	 * @param renderer Media renderer
	 * @throws ServerApiException
	 */
	public IToIpSession initiateToIpCall(String contact, IMediaPlayer player, IMediaRenderer renderer) throws ServerApiException {
		// Test IMS connection
		ServerApiUtils.testIms();

		try {
			MediaPlayer corePlayer = new RemoteMediaPlayer(player);
			MediaRenderer coreRenderer = new RemoteMediaRenderer(renderer);
			com.orangelabs.rcs.core.ims.service.toip.ToIpSession session = Core.getInstance().getToIpService().initiateCall(contact, corePlayer, coreRenderer);
			return new ToIpSession(session);
		} catch(Exception e) {
			throw new ServerApiException(e.getMessage());
		}
	}

	/**
	 * Get a ToIP session from its session ID
	 *
	 * @param id Session ID
	 * @return Session
	 * @throws ServerApiException
	 */
	public IToIpSession getToIpSession(String id) throws ServerApiException {
		// Test core availability
		ServerApiUtils.testCore();
		
		try {
			ImsServiceSession session = Core.getInstance().getToIpService().getSession(id);
			if ((session != null) && (session instanceof com.orangelabs.rcs.core.ims.service.toip.ToIpSession)) {
				return new ToIpSession((com.orangelabs.rcs.core.ims.service.toip.ToIpSession)session);
			} else {
				return null;
			}
		} catch(Exception e) {
			throw new ServerApiException(e.getMessage());
		}
	}
}