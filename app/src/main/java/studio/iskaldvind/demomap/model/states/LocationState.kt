package studio.iskaldvind.demomap.model.states

sealed class LocationState {
    object Default : LocationState()
    data class Data(val latitude: Double, val longitude: Double) : LocationState()
    data class Error(val error: String) : LocationState()
}