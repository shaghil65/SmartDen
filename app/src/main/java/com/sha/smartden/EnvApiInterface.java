package com.sha.smartden;

import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface EnvApiInterface {
    @GET
    Call<results> getEnvData(@Url String url);
}
