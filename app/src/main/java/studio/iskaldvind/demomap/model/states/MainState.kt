package studio.iskaldvind.demomap.model.states

sealed class MainState {
	object Loading : MainState()
	object Success : MainState()
	data class Error(val error: String) : MainState()
}