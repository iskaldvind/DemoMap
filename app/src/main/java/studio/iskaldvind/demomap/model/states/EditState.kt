package studio.iskaldvind.demomap.model.states

sealed class EditState {
	object Loading : EditState()
	object Success : EditState()
	data class Error(val error: String) : EditState()
}