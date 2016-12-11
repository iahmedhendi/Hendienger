package com.hendiware.hendienger.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hendiware on 2016/12 .
 */
public class ChatRoom {

    @SerializedName("id")
    public String id;
    @SerializedName("room_name")
    public String room_name;
    @SerializedName("room_desc")
    public String room_desc;
}

