package studio.iskaldvind.demomap.model.states

import studio.iskaldvind.demomap.model.entities.Marker

sealed class EditState {
	object Loading : EditState()
	data class Data(val marker: Marker) : EditState()
	object Success : EditState()
	data class Error(val error: String) : EditState()
}