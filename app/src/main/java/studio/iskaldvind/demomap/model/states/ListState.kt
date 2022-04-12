package studio.iskaldvind.demomap.model.states

import studio.iskaldvind.demomap.model.entities.AppMarker

sealed class ListState {
	object Loading : ListState()
	data class Data(val markers: List<AppMarker>) : ListState()
	data class Error(val error: String) : ListState()
}