package studio.iskaldvind.demomap.utils

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import studio.iskaldvind.demomap.model.states.LocationState
import kotlin.math.abs

class Geolocator(
	private val locationManager: LocationManager
) {

	companion object {
		private const val DEFAULT_COORDINATE = 0.0
	}

	private var locationThreshold = 0.0F
	private var lastLatitude = DEFAULT_COORDINATE
	private var lastLongitude = DEFAULT_COORDINATE
	private var lastKnownLocation: Location? = null

	private val internalState = MutableStateFlow<LocationState>(LocationState.Default)
	val state: StateFlow<LocationState> = internalState

	private val geolocatorCoroutineScope = CoroutineScope(
		Dispatchers.Default + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
			handleError(throwable)
		}
	)

	private fun isCoordinateChanged(oldCoordinate: Double, newCoordinate: Double): Boolean =
		newCoordinate != DEFAULT_COORDINATE
				&& abs(oldCoordinate - newCoordinate) > locationThreshold

	private val locationListener = object : LocationListener {
		override fun onLocationChanged(location: Location) {
			postLocation(location)
		}

		override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
	}

	private fun postLocation(location: Location) {
		if (
			isCoordinateChanged(lastLatitude, location.latitude)
			|| isCoordinateChanged(lastLongitude, location.longitude)
		) {
			lastLatitude = location.latitude
			lastLongitude = location.longitude
			Log.e("GEO", "Lat $lastLatitude, Lon $lastLongitude")
			geolocatorCoroutineScope.launch {
				internalState.value = LocationState.Data(
					latitude = lastLatitude,
					longitude = lastLongitude
				)
			}
		}
	}

	fun startLocationUpdates(
		locationThresholdGrad: Float,
		intervalGPSMsec: Long,
		intervalNetMsec: Long,
		intervalPassiveMsec: Long
	) {
		locationThreshold = locationThresholdGrad
		try {
			lastKnownLocation = lastKnownLocation
				?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
			if (
				locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
				&& intervalNetMsec > 0
			) {
				locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER,
					intervalNetMsec,
					locationThresholdGrad,
					locationListener
				)
			}
			if (
				locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& intervalGPSMsec > 0
			) {
				locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					intervalGPSMsec,
					locationThresholdGrad,
					locationListener)
			}
			if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
				locationManager.requestLocationUpdates(
					LocationManager.PASSIVE_PROVIDER,
					intervalPassiveMsec,
					locationThresholdGrad,
					locationListener
				)
			}
		} catch (e: SecurityException) {
			geolocatorCoroutineScope.launch {
				internalState.value = LocationState.Error(error = "Geolocation Permissions Error")
			}
		}
	}

	/**
	 * Removes listener from location Manager
	 */
	fun stopLocationUpdates() {
		locationManager.removeUpdates(locationListener)
		geolocatorCoroutineScope.coroutineContext.cancelChildren()
	}

	private fun handleError(error: Throwable) {
		geolocatorCoroutineScope.launch {
			internalState.value = LocationState.Error(error = error.message ?: "Geolocation Error")
		}
	}
}