package sc.android.nearcart.model

//for latitude and longitude of location
data class LatLngData(
    val lat : Double,
    val lng : Double
)

//for geometry (location object: coordinates, viewport(area)
data class Geometry(
    val location : LatLngData
)

//store opening hours
data class OpeningHours(
    val open_now: Boolean?
)

//each nearby place result
data class NearbyPlacesResult(
    val place_id: String,
    val name : String,
    val geometry: Geometry,
    val opening_hours: OpeningHours?
)

//list of nearby places
data class NearbyPlacesResponse(
    val results : List<NearbyPlacesResult>,
    val next_page_token: String?
)