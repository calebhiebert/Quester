package com.piikl.quester.api

import retrofit2.Call
import retrofit2.http.*

interface QuesterService {

    @GET("/campaign")
    fun listCampaigns(): Call<List<Campaign>>

    @GET("/quest/{id}")
    fun getQuest(@Path("id") questId: Long): Call<Quest>

    @DELETE("/quest/{id}")
    fun deleteQuest(@Path("id") questId: Long) : Call<Quest>

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

    @POST("/campaign/{c_id}/invite/{u_id}")
    fun inviteUser(@Path("c_id") campaignId: Long, @Path("u_id") userId: Long): Call<User>

    @GET("/user/search")
    fun searchUsers(@Query("name") search: String): Call<List<Campaign>>

    @GET("/ping")
    fun ping(): Call<Boolean>
}