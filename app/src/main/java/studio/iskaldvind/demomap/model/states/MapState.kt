package studio.iskaldvind.demomap.model.states

import studio.iskaldvind.demomap.model.entities.Marker

sealed class MapState {
	object Loading : MapState()
	data class Data(
		val markers: List<Marker>,
		val latitude: Double,
		val longitude: Double
	) : MapState()
	data class Error(val error: String) : MapState()
}