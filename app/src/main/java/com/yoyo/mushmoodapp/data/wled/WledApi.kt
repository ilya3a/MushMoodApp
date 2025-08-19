package com.yoyo.mushmoodapp.data.wled

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import com.google.gson.annotations.SerializedName

data class PresetRequest(
    @SerializedName("ps") val presetId: Int
)

interface WledApi {
    @POST
    suspend fun setPreset(@Url url: String, @Body preset: PresetRequest): Response<Unit>

    @GET
    suspend fun ping(@Url url: String): Response<Unit>
}
