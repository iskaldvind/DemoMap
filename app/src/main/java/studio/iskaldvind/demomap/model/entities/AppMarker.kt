package studio.iskaldvind.demomap.model.entities

class AppMarker {
	val id: Int
	val latitude: Double
	val longitude: Double
	val title: String
	val annotation: String

	constructor() {
		this.id = 0
		this.latitude = 0.0
		this.longitude = 0.0
		this.title = ""
		this.annotation = ""
	}

	constructor(id: Int, latitude: Double, longitude: Double) {
		this.id = id
		this.latitude = latitude
		this.longitude = longitude
		this.title = ""
		this.annotation = ""
	}

	constructor(id: Int, latitude: Double, longitude: Double, title: String, annotation: String) {
		this.id = id
		this.latitude = latitude
		this.longitude = longitude
		this.title = title
		this.annotation = annotation
	}
}