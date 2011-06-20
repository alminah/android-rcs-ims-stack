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

package com.orangelabs.rcs.ri.richcall;

import com.orangelabs.rcs.platform.AndroidFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Video sharing invitation receiver
 *
 * @author jexa7410
 */
public class VideoSharingInvitationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// Set application context
		AndroidFactory.setApplicationContext(context); //TODO: use context at player level

		// Display invitation notification
        VisioSharing.addVideoSharingInvitationNotification(context, intent);
    }
}

