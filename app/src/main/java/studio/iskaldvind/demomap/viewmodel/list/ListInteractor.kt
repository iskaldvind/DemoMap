package studio.iskaldvind.demomap.viewmodel.list

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import studio.iskaldvind.demomap.model.entities.AppMarker

class ListInteractor {

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