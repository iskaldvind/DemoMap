package studio.iskaldvind.demomap.viewmodel.main

import studio.iskaldvind.demomap.model.states.AppState
import studio.iskaldvind.demomap.utils.Geolocator

class MainInteractor(
	private val geolocator: Geolocator,
	private val state: AppState
) {

	fun startGeolocator(
		permissionFineLocation: Boolean,
		permissionCoarseLocation: Boolean
	) {
		geolocator.startLocationUpdates(
			intervalPassiveMsec = state.LOCATION_ACQUIRE_INTERVAL_PAS,
			intervalNetMsec = if (permissionCoarseLocation)
				state.LOCATION_ACQUIRE_INTERVAL_NET else 0,
			intervalGPSMsec = if (permissionFineLocation)
				state.LOCATION_ACQUIRE_INTERVAL_GPS else 0,
			locationThresholdGrad = state.LOCATION_CHANGE_THRESHOLD
		)
	}

	fun stopGeolocator() {
		geolocator.stopLocationUpdates()
	}
}