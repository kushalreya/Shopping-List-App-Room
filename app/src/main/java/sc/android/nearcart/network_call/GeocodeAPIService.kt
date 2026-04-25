package sc.android.nearcart.network_call

import retrofit2.http.GET
import retrofit2.http.Query
import sc.android.nearcart.model.GeocodingResponse

interface GeocodeAPIService {
    @GET("maps/api/geocode/json")
    suspend fun getAddressFromCoordinates(
        @Query("latlng") latlng: String,
        @Query("key") key: String
    ): GeocodingResponse
}