package com.hendiware.hendienger.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by hendiware on 2016/12 .
 */

public class Message implements Parcelable {


    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };


    @SerializedName("id")
    private String id;
    @SerializedName("room_id")
    private String roomId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("user_name")
    private String username;
    @SerializedName("type")
    private String type;
    @SerializedName("content")
    private String content;
    @SerializedName("timestamp")
    private String timestamp;

    public Message() {
    }

    protected Message(Parcel in) {
        id = in.readString();
        roomId = in.readString();
        userId = in.readString();
        username = in.readString();
        type = in.readString();
        content = in.readString();
        timestamp = in.readString();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Method convert time stamp to hourly time ;
     *
     * @return time like 07:20 AM from time 2017 8 1 20:12
     */
    public String getTime() {
        if (timestamp == null) {
            return "now";
        } else {
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

            Date date = null;
            try {
                date = serverFormat.parse(timestamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TimeZone timeZone = TimeZone.getDefault();
            int rawOffset = timeZone.getRawOffset();
            long local = 0;
            if (date != null) {
                local = date.getTime() + rawOffset;
            }
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(local);
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            return format.format(calendar.getTime());
        }


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(roomId);
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(type);
        dest.writeString(content);
        dest.writeString(timestamp);
    }
}
