package sc.android.shoppinglistapp_room.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sc.android.shoppinglistapp_room.BuildConfig
import sc.android.shoppinglistapp_room.model.GeocodingResult
import sc.android.shoppinglistapp_room.model.LocationData
import sc.android.shoppinglistapp_room.network_call.RetrofitClient

class LocationViewModel: ViewModel() {
    //to store location coordinates
    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    //to store the formatted address
    private val _address = mutableStateOf(listOf<GeocodingResult>())
    val address : State<List<GeocodingResult>> =_address

    //to store the last location
    private val _lastLocation = mutableStateOf<LocationData?>(null)
    val lastSavedLocation : State<LocationData?> = _lastLocation

    //to update location
    fun updateLocation(newLocation: LocationData){
        _location.value=newLocation
    }

    //to update the last location fetched with the currently fetched location
    fun saveManualLocation(selected: LocationData){
        _lastLocation.value=selected
    }

    //to fetch the formatted address from the API by giving it latitude longitude
    fun fetchAddress(latlng: String){
        viewModelScope.launch {
            try{
                val result = RetrofitClient.create().getAddressFromCoordinates(
                    latlng,
                    BuildConfig.LOCATION_API_KEY
                )
                _address.value=result.results
            }catch(e: Exception){
                Log.d("res1", "${e.cause} ${e.message}")
            }
        }
    }
}