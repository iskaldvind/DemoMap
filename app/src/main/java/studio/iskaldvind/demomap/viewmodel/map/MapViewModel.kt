package studio.iskaldvind.demomap.viewmodel.map

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import studio.iskaldvind.demomap.core.base.BaseViewModel
import studio.iskaldvind.demomap.model.states.LocationState
import studio.iskaldvind.demomap.model.states.MainState
import studio.iskaldvind.demomap.model.states.MapState
import studio.iskaldvind.demomap.utils.Geolocator

class MapViewModel(
	private val interactor: MapInteractor,
	private val geolocator: Geolocator
) : BaseViewModel<MapState>() {

	var mapData: MapState.Data? = null
		private set

	override val initialState: MapState
		get() = MapState.Loading
	
	override fun handleError(error: Throwable) {
		viewModelCoroutineScope.launch {
			internalState.value = MapState.Error(error = error.message ?: "Internal Error")
		}
	}

	fun init() {
		viewModelCoroutineScope.launch {
			geolocator.state.collect { locationState ->
				if (locationState is LocationState.Data) {
					val data = mapData ?: MapState.Data(listOf(), 0.0, 0.0)
					data.copy(
						latitude = locationState.latitude,
						longitude = locationState.longitude
					).also { newData ->
						mapData = newData
						internalState.value = newData
					}
				}
			}
		}
		getData()
	}

	private fun getData() {

	}
}