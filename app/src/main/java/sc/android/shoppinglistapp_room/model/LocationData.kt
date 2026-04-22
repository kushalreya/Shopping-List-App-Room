package sc.android.shoppinglistapp_room.model



data class LocationData (
    val latitude: Double,
    val longitude: Double
)

data class GeocodingResult(
    val formatted_address: String
)

data class GeocodingResponse(
    val results: List<GeocodingResult>
)