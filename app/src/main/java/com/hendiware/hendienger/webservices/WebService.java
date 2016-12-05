package com.hendiware.hendienger.webservices;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  hendiware 2016
 */

public class WebService {
    private static WebService instance;
    private API api;

    public WebService() {

        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder().client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Urls.MAIN_URL)
                .build();

        api = retrofit.create(API.class);
    }

    public static WebService getInstance() {
        if (instance == null) {
            instance = new WebService();
        }
        return instance;
    }

    public API getApi() {
        return api;
    }
}
