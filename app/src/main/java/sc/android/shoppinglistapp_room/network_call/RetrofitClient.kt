package sc.android.shoppinglistapp_room.network_call

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitClient {
    private const val BASE_URL = "https://maps.googleapis.com/"

    fun create() : GeocodeAPIService{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(GeocodeAPIService::class.java)
    }
}