package com.piikl.quester.api

import retrofit2.Call
import retrofit2.http.*

interface QuesterService {

    @GET("/campaign")
    fun listCampaigns(): Call<List<Campaign>>

    @GET("/quest")
    fun listQuests(): Call<List<Quest>>

    @GET("/quest/{id}")
    fun getQuest(@Path("id") questId: String): Call<Quest>

    @GET("/campaign/{id}")
    fun getCampaign(@Path("id") campaignId: String): Call<Campaign>

    @FormUrlEncoded
    @POST("/campaign")
    fun createCampaign(@Field("name") name: String): Call<Campaign>

    @POST("/quest")
    fun createQuest(@Body quest: Quest): Call<Quest>

    @DELETE("/campaign/{id}")
    fun deleteCampaign(@Path("id") campaignId: String): Call<Campaign>
}