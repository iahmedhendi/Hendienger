package com.hendiware.hendienger.webservices;

import com.hendiware.hendienger.models.MainResponse;
import com.hendiware.hendienger.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 *  hendiware on 2016/12 .
 */

public interface API {
    @POST("login-user.php")
    Call<MainResponse> loginUser(@Body User user);

    @POST("register-user.php")
    Call<MainResponse> registerUser(@Body User user);

}

