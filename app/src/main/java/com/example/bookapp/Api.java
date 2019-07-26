package com.example.bookapp;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("v1/volumes")
    Call<ResponseBody> getbooks(@Query("q") String str);
}
