package com.hendiware.hendienger.webservices;

import com.hendiware.hendienger.models.ChatRoom;
import com.hendiware.hendienger.models.LoginResponse;
import com.hendiware.hendienger.models.MainResponse;
import com.hendiware.hendienger.models.Message;
import com.hendiware.hendienger.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * hendiware on 2016/12 .
 */

public interface API {

    @POST("login-user.php")
    Call<LoginResponse> loginUser(@Body User user);

    @POST("register-user.php")
    Call<MainResponse> registerUser(@Body User user);

    @POST("add-chat-room.php")
    Call<MainResponse> addChatRoom(@Body ChatRoom chatRoom);

    @FormUrlEncoded
    @POST("delete-chat-room.php")
    Call<MainResponse> deleteChatRoom(@Field("id") int roomID);

    @POST("get-all-chat-rooms.php")
    Call<List<ChatRoom>> getAllChatRooms();

    @POST("add-message.php")
    Call<MainResponse> addMessage(@Body Message message);

    @FormUrlEncoded
    @POST("get-messages.php")
    Call<List<Message>> getMessages(@Field("room_id") int roomId);

}

