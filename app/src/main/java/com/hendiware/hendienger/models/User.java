package com.hendiware.hendienger.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * hendiware 12 2016
 */

public class User extends RealmObject {

    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;
    @SerializedName("email")
    public String email;
}

