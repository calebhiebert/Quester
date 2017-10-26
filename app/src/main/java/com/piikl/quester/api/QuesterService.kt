package com.piikl.quester.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuesterService {

    @GET("/campaign")
    fun listCampaigns(): Call<List<Campaign>>

    @GET("/quest")
    fun listQuests(): Call<List<Quest>>

    @GET("/quest/{id}")
    fun getQuest(@Path("id") questId: String): Call<Quest>

    @GET("/campaign/{id}")
    fun getCampaign(@Path("id") campaignId: String): Call<Campaign>

    @POST
    fun createCampaign(@Body campaign: Campaign): Call<Campaign>

    @POST
    fun createQuest(@Body quest: Quest): Call<Quest>
}