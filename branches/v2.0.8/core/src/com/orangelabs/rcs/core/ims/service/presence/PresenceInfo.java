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
package com.orangelabs.rcs.core.ims.service.presence;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

import com.orangelabs.rcs.core.ims.service.capability.Capabilities;

/**
 * Presence info
 * 
 * @author jexa7410
 */
public class PresenceInfo implements Parcelable {
	/**
	 * Presence status "unknown"
	 */
	public final static String UNKNOWN = "unknown";

	/**
	 * Presence status "online"
	 */
	public final static String ONLINE = "open";
	
	/**
	 * Presence status "offline"
	 */
	public final static String OFFLINE = "closed";

	/**
	 * Presence timestamp
	 */
	private long timestamp = Calendar.getInstance().getTimeInMillis();

	/**
	 * Presence status
	 */
	private String status = PresenceInfo.ONLINE;
	
	/**
	 * Free text
	 */
	private String freetext = null;
	
	/**
	 * Favorite link
	 */
	private FavoriteLink favoriteLink = null;

	/**
	 * Photo icon
	 */
	private PhotoIcon photo = null;

	/**
	 * Hyper-availability status
	 */
	private boolean hyperavailabilityStatus = false;
	
	/**
	 * Capabilities
	 */
	private Capabilities capabilities = new Capabilities();

	/**
	 * Geoloc
	 */
	private Geoloc geoloc = null;
	
	/**
	 * Constructor
	 */
	public PresenceInfo() {
	}

	/**
	 * Constructor
	 * 
	 * @param source Parcelable source
	 */
	public PresenceInfo(Parcel source) {
		this.timestamp = source.readLong();
		this.status = source.readString();
		this.freetext = source.readString();
		
		byte flag = source.readByte();
		if (flag > 0) {
			this.favoriteLink = FavoriteLink.CREATOR.createFromParcel(source);
		} else {
			this.favoriteLink = null;
		}
		
		flag = source.readByte();
		if (flag > 0) {
			this.photo = PhotoIcon.CREATOR.createFromParcel(source);
		} else {
			this.photo = null;
		}
		
		this.hyperavailabilityStatus = source.readInt() != 0;
		
		flag = source.readByte();
		if (flag > 0) {
			this.capabilities = Capabilities.CREATOR.createFromParcel(source);
		} else {
			this.capabilities = null;
		}
		
		flag = source.readByte();
		if (flag > 0) {
			this.geoloc = Geoloc.CREATOR.createFromParcel(source);
		} else {
			this.geoloc = null;
		}
    }
	
	/**
	 * Describe the kinds of special objects contained in this Parcelable's
	 * marshalled representation
	 * 
	 * @return Integer
	 */
	public int describeContents() {
        return 0;
    }

	/**
	 * Write parcelable object
	 * 
	 * @param dest The Parcel in which the object should be written
	 * @param flags Additional flags about how the object should be written
	 */
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeLong(timestamp);    	
    	dest.writeString(status);
    	dest.writeString(freetext);
    	
    	if (favoriteLink != null) {
    		dest.writeByte((byte)1);
    		favoriteLink.writeToParcel(dest, flags);
    	} else {
    		dest.writeByte((byte)0);
    	}
    	
    	if (photo != null) {
    		dest.writeByte((byte)1);
    		photo.writeToParcel(dest, flags);
    	} else {
    		dest.writeByte((byte)0);
    	}
    	
		dest.writeInt(hyperavailabilityStatus ? 1 : 0);
    	
    	if (capabilities != null) {
    		dest.writeByte((byte)1);
	    	capabilities.writeToParcel(dest, flags);
		} else {
			dest.writeByte((byte)0);
		}
    	
    	if (geoloc != null) {
    		dest.writeByte((byte)1);
	    	geoloc.writeToParcel(dest, flags);
		} else {
			dest.writeByte((byte)0);
		}
    }

    /**
     * Parcelable creator
     */
    public static final Parcelable.Creator<PresenceInfo> CREATOR
            = new Parcelable.Creator<PresenceInfo>() {
        public PresenceInfo createFromParcel(Parcel source) {
            return new PresenceInfo(source);
        }

        public PresenceInfo[] newArray(int size) {
            return new PresenceInfo[size];
        }
    };	

    /**
	 * Set the timestamp
	 * 
	 * @param timestamp Timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Returns the timestamp
	 * 
	 * @return Timestamp
	 */
	public long getTimestamp(){
		return timestamp;
	}

	/**
	 * Reset the timestamp
	 */
	public void resetTimestamp() {
		timestamp = PresenceInfo.getNewTimestamp();
	}
	
	/**
	 * Returns a new timestamp
	 * 
	 * @return Timestamp
	 */
	public static long getNewTimestamp() {
		return Calendar.getInstance().getTimeInMillis();
	}
	
	/**
	 * Returns the presence status
	 *  
	 * @return Status
	 */
	public String getPresenceStatus() {
		return status;
	}

	/**
	 * Set the presence status
	 * 
	 * @param status New status
	 */
	public void setPresenceStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Is status online
	 * 
	 * @return Boolean
	 */
	public boolean isOnline() {
		return (status.equals(ONLINE));
	}

	/**
	 * Is status offline
	 * 
	 * @return Boolean
	 */
	public boolean isOffline() {
		return (status.equals(OFFLINE));
	}	

	/**
	 * Returns the free text
	 *  
	 * @return Free text
	 */
	public String getFreetext() {
		return freetext;
	}

	/**
	 * Set the free text
	 * 
	 * @param freetext New free text
	 */
	public void setFreetext(String freetext) {
		this.freetext = freetext;
	}
	
	/**
	 * Get the favorite link
	 * 
	 * @return Favorite link
	 */
	public FavoriteLink getFavoriteLink(){
		return favoriteLink;
	}
	
	/**
	 * Get the favorite link URL
	 * 
	 * @return Favorite link URL
	 */
	public String getFavoriteLinkUrl(){
		String url = null;
		if (favoriteLink != null) {
			url = favoriteLink.getLink();
		}
		return url;
	}
	
	/**
	 * Set the favorite link
	 * 
	 * @param favoriteLink Favorite link
	 */
	public void setFavoriteLink(FavoriteLink favoriteLink){
		this.favoriteLink = favoriteLink;
	}

	/**
	 * Set the favorite link URL
	 * 
	 * @param url Favorite link URL
	 */
	public void setFavoriteLinkUrl(String url){
		if (favoriteLink == null) {
			favoriteLink = new FavoriteLink(url);
		}
		favoriteLink.setLink(url);
	}

	/**
	 * Get the photo-icon
	 */
	public PhotoIcon getPhotoIcon() {
		return photo;
	}

	/**
	 * Set the photo-icon
	 * 
	 * @param photo Photo-icon
	 */
	public void setPhotoIcon(PhotoIcon photo) {
		this.photo = photo;
	}

	/**
	 * Is hyper-available
	 *  
	 * @return Boolean
	 */
	public boolean isHyperavailable() {
		return hyperavailabilityStatus;
	}
	
	/**
	 * Set the hyper-availability status
	 *  
	 * @param hyperavailabilityStatus Hyper-availability status
	 */
	public void setHyperavailabilityStatus(boolean hyperavailabilityStatus) {
		this.hyperavailabilityStatus = hyperavailabilityStatus;
	}
	
	/**
	 * Get the capabilities
	 *  
	 * @return Capabilities
	 */
	public Capabilities getCapabilities() {
		return capabilities;
	}
	
	/**
	 * Set the capabilities
	 *  
	 * @param capabilities Capabilities
	 */
	public void setCapabilities(Capabilities capabilities) {
		this.capabilities = capabilities;
	}
	
	/**
	 * Get the geoloc
	 * 
	 * @return Geoloc
	 */
	public Geoloc getGeoloc() {
		return geoloc;
	}	

	/**
	 * Set the geoloc
	 * 
	 * @param geoloc Geoloc
	 */
	public void setGeoloc(Geoloc geoloc) {
		this.geoloc = geoloc;
	}
	
	/**
	 * Returns a string representation of the object
	 * 
	 * @return String
	 */
	public String toString() {
		String result =  "Timestamp: " + timestamp + "\n" +
			"Status: " + status + "\n" +
			"Freetext: " + freetext + "\n" +
			"Hyper-availability: " + hyperavailabilityStatus + "\n";
		if (favoriteLink != null) {
			result += "favorite link: " + favoriteLink.toString() + "\n";
		}
		if (photo != null) {
			result += "Photo-icon: " + photo.toString() + "\n";
		}
		if (capabilities != null) {
			result += "Capabilities: " + capabilities.toString() + "\n";
		}
		if (geoloc != null) {
			result += "Geoloc: " + geoloc.toString() + "\n";
		}
		return result;
	}
}