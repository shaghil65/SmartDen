package com.sha.smartden;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface HealthApiInterface {
    @GET
    Call<result2> getHealthData(@Url String url);
}
