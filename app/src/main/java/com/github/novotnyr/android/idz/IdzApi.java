package com.github.novotnyr.android.idz;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface IdzApi {
    String BASE_URL = "https://ics.upjs.sk/~novotnyr/android/demo/idz/";

    @GET("index.php")
    Call<Direction> getDirection();

    // API Execution Classes
    Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    IdzApi API = RETROFIT.create(IdzApi.class);

    public class Direction {
        private String direction;

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }
}
