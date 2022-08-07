package cn.devmeteor.devicemanagement

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface DeviceApi {
    @GET("/v2/device/list")
    fun get():Call<ArrayList<Device>>

    @PUT("/v2/device/allow")
    fun allow(@Query("deviceId")deviceId: String, @Query("allow")allow:Boolean):Call<Void>
}