package com.hendiware.hendienger.models;

import com.google.gson.annotations.SerializedName;

/**
 * hendiware 12 2016
 */

public class User {

    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;
    @SerializedName("email")
    public String email;
}
