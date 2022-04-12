package studio.iskaldvind.demomap.viewmodel.map

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import studio.iskaldvind.demomap.model.entities.AppMarker

class MapInteractor {

    fun addMarker(latitude: Double, longitude: Double, callback: (Int) -> Unit) {
        FirebaseDatabase
            .getInstance()
            .getReference("markers")
            .get()
            .addOnCompleteListener { response ->
                val lastId = response
                    .result
                    .children
                    .map { child -> child.key }
                    .maxOf { id -> id?.toInt() ?: 0 }
                val newId = lastId + 1
                val newMarker = AppMarker(id = newId, latitude = latitude, longitude = longitude)
                FirebaseDatabase
                    .getInstance()
                    .getReference("markers")
                    .child(newId.toString())
                    .setValue(newMarker).addOnCompleteListener {
                        callback.invoke(newId)
                    }.addOnFailureListener {
                        callback.invoke(-1)
                    }
            }.addOnFailureListener {
                callback.invoke(-1)
            }
    }

    fun getMarkers(callback: (List<AppMarker>) -> Unit) {
        FirebaseDatabase
            .getInstance()
            .getReference("markers")
            .get()
            .addOnCompleteListener { response ->
                val markers = response
                    .result
                    .children
                    .mapNotNull { child -> child.getValue<AppMarker>() }
                callback.invoke(markers)
            }.addOnFailureListener {
                callback.invoke(listOf())
            }
    }
}