package com.example.juanjomz.amazonia.core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://my-api.plantnet.org/v2/identify/all?api-key=2b105W6lnY8BYl7P71d6JoONO")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

}