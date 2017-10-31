package com.piikl.quester.api

import retrofit2.Call
import retrofit2.http.*

interface QuesterService {

    @GET("/campaign")
    fun listCampaigns(): Call<List<Campaign>>

    @GET("/quest")
    fun listQuests(): Call<List<Quest>>

    @GET("/quest/{id}")
    fun getQuest(@Path("id") questId: Long): Call<Quest>

    @GET("/campaign/{id}")
    fun getCampaign(@Path("id") campaignId: Long): Call<Campaign>

    @PATCH("/campaign/{id}")
    fun editCampaign(@Path("id") campaignId: Long, @Body campaign: Campaign): Call<Campaign>

    @POST("/campaign")
    fun createCampaign(@Body campaign: Campaign): Call<Campaign>

    @POST("/campaign/{id}/createQuest")
    fun createQuest(@Path("id") campaignId: Long, @Body quest: Quest): Call<Quest>

    @PATCH("/quest/{id}")
    fun editQuest(@Path("id") questId: Long, @Body quest: Quest): Call<Quest>

    @DELETE("/campaign/{id}")
    fun deleteCampaign(@Path("id") campaignId: String): Call<Campaign>

    @GET
    fun searchUsers(@Query("name") search: String): Call<List<Campaign>>
}