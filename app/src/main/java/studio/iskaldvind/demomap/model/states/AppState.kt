package studio.iskaldvind.demomap.model.states

@Suppress("PropertyName")
class AppState {
    val LOCATION_CHANGE_THRESHOLD: Float = 0.001F
    val LOCATION_ACQUIRE_INTERVAL_GPS: Long = 10000
    val LOCATION_ACQUIRE_INTERVAL_NET: Long = 2000
    val LOCATION_ACQUIRE_INTERVAL_PAS: Long = 1000
}