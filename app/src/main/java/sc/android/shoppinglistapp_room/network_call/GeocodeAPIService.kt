package sc.android.shoppinglistapp_room.network_call

import retrofit2.http.GET
import retrofit2.http.Query
import sc.android.shoppinglistapp_room.model.GeocodingResponse

interface GeocodeAPIService {
    @GET("maps/api/geocode/json")
    suspend fun getAddressFromCoordinates(
        @Query("latlng") latlng: String,
        @Query("key") key: String
    ): GeocodingResponse
}