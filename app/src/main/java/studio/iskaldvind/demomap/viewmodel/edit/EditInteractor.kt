package studio.iskaldvind.demomap.viewmodel.edit

import com.google.firebase.database.FirebaseDatabase
import studio.iskaldvind.demomap.model.entities.AppMarker

class EditInteractor {

    fun deleteMarker(id: Int, callback: (String?) -> Unit) {
        FirebaseDatabase
            .getInstance()
            .getReference("markers/$id")
            .setValue(null)
            .addOnCompleteListener {
                callback.invoke(null)
            }.addOnFailureListener {
                callback.invoke("Marker removal was failed")
            }
    }

    fun updateMarker(
        id: Int,
        latitude: Double,
        longitude: Double,
        title: String,
        annotation: String,
        callback: (String?) -> Unit
    ) {
        FirebaseDatabase
            .getInstance()
            .getReference("markers/$id")
            .setValue(AppMarker(
                id = id,
                latitude = latitude,
                longitude = longitude,
                title = title,
                annotation = annotation
            ))
            .addOnCompleteListener {
                callback.invoke(null)
            }.addOnFailureListener {
                callback.invoke("Marker update was failed")
            }
    }
}