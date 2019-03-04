package com.example.chat.network;

import com.example.chat.models.Friend;
import com.example.chat.models.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {
    @GET("bUFCFadauW?indent=2")
    Call<List<Friend>> getAllFriends();

    @GET("cqJDvjiCgi?indent=2")
    Call<List<Message>> getAllMessages();
}
