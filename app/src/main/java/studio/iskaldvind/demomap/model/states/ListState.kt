package studio.iskaldvind.demomap.model.states

import studio.iskaldvind.demomap.model.entities.Marker

sealed class ListState {
	object Loading : ListState()
	data class Data(val markers: List<Marker>) : ListState()
	data class Error(val error: String) : ListState()
}