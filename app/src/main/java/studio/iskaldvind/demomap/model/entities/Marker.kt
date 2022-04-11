package studio.iskaldvind.demomap.model.entities

data class Marker(
	val id: Int,
	val latitude: Double,
	val longitude: Double,
	val title: String = "",
	val annotation: String = ""
)