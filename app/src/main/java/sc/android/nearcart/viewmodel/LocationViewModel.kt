package sc.android.nearcart.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sc.android.nearcart.BuildConfig
import sc.android.nearcart.model.GeocodingResult
import sc.android.nearcart.model.LocationData
import sc.android.nearcart.model.NearbyPlacesResult
import sc.android.nearcart.network_call.RetrofitClient
import kotlin.collections.listOf

class LocationViewModel : ViewModel() {

    //to store the location coordinates
    private val _location = mutableStateOf<LocationData?>(null)
    val location : State<LocationData?> = _location

    //to store list of formatted addresses
    private val _addresses = mutableStateOf(listOf<GeocodingResult>())
    val addresses : State<List<GeocodingResult>> = _addresses

    //to store the last selected location
    private val _lastLocation = mutableStateOf<LocationData?>(null)
    val lastSavedLocation : State<LocationData?> = _lastLocation

    //to store list of nearby places
    private val _places = mutableStateOf(listOf<NearbyPlacesResult>())
    val places : State<List<NearbyPlacesResult>> = _places


    //to update the location
    fun updateLocation(newLocation : LocationData){
        _location.value = newLocation
    }

    //to update the last location fetched with the currently fetched location
    fun saveManualLocation(selected : LocationData){
        _lastLocation.value = selected
    }

    //to fetch the formatted address from the API by giving its lat-long
    fun fetchAddress(latLng : String){
        viewModelScope.launch {
            try {
                val result = RetrofitClient.addressCreate().getAddressFromCoordinates(
                    latLng,
                    BuildConfig.LOCATION_API_KEY
                )
                _addresses.value = result.results
            } catch (e : Exception) {
                Log.d("addresses", "${e.cause} ${e.message}")
            }
        }
    }

    //to fetch the nearby places from API using location coordinates
    fun fetchNearbyPlaces(location: LocationData) {

        viewModelScope.launch {
            try {

                val allPlaces = mutableListOf<NearbyPlacesResult>()
                val api = RetrofitClient.nearbyPlacesCreate()

// Page 1 (INSTANT UI)
                val response1 = api.getNearbyPlaces(
                    location = "${location.latitude},${location.longitude}",
                    radius = 5000,
                    keyword = "grocery OR supermarket OR store OR mall",
                    key = BuildConfig.LOCATION_API_KEY
                )

                allPlaces.addAll(response1.results)
                _places.value = allPlaces.distinctBy { it.place_id }

                Log.d("PLACES", "Page1: ${response1.results.size}")

// Page 2 (background)
                response1.next_page_token?.let { token1 ->
                    delay(2000)

                    val response2 = api.getNearbyPlaces(
                        pagetoken = token1,
                        key = BuildConfig.LOCATION_API_KEY
                    )

                    allPlaces.addAll(response2.results)
                    _places.value = allPlaces.distinctBy { it.place_id }

                    Log.d("PLACES", "Page2: ${response2.results.size}")

                    // Page 3 (background)
                    response2.next_page_token?.let { token2 ->
                        delay(2000)

                        val response3 = api.getNearbyPlaces(
                            pagetoken = token2,
                            key = BuildConfig.LOCATION_API_KEY
                        )

                        allPlaces.addAll(response3.results)
                        _places.value = allPlaces.distinctBy { it.place_id }

                        Log.d("PLACES", "Page3: ${response3.results.size}")
                    }
                }

                Log.d("PLACES_TOTAL", "Final: ${allPlaces.size}")

            } catch (e: Exception) {
                Log.d("places", "${e.cause} ${e.message}")
            }
        }
    }

}