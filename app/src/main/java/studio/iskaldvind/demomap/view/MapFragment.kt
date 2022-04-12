package studio.iskaldvind.demomap.view

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import studio.iskaldvind.demomap.core.BaseFragment
import studio.iskaldvind.demomap.R.layout.map_fragment
import studio.iskaldvind.demomap.databinding.MapFragmentBinding
import studio.iskaldvind.demomap.viewmodel.map.MapViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.iskaldvind.demomap.R
import studio.iskaldvind.demomap.model.entities.AppMarker
import studio.iskaldvind.demomap.model.states.MapState

class MapFragment: BaseFragment(map_fragment),
    OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener {

    companion object {
        const val TAG = "MAP"
        fun newInstance(): Fragment = MapFragment()
        const val DEFAULT_ZOOM = 16.0f
    }

    private val binding: MapFragmentBinding by viewBinding()
    private val viewModel: MapViewModel by viewModel()
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapFragment)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is MapState.Loading -> {}
                        is MapState.Data -> onDataState(
                            markers = state.markers,
                            latitude = state.latitude,
                            longitude = state.longitude
                        )
                        is MapState.Error -> onErrorState(error = state.error)
                    }
                }
            }
        }
        viewModel.init()
    }

    private fun onDataState(
        markers: List<AppMarker>,
        latitude: Double,
        longitude: Double
    ) {
        googleMap?.let { map ->
            map.clear()
            markers.forEach { appMarker ->
                val info = when {
                    appMarker.title.isNotBlank() && appMarker.annotation.isNotBlank() ->
                        "${appMarker.title}\n${appMarker.annotation}"
                    appMarker.title.isNotBlank() -> appMarker.title
                    appMarker.annotation.isNotBlank() -> appMarker.annotation
                    else -> ""
                }
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(appMarker.latitude, appMarker.longitude))
                        .title(info)
                )
            }
            if (latitude != 0.0 && longitude != 0.0) {
                currentLatitude = latitude
                currentLongitude = longitude
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(currentLatitude, currentLongitude),
                        DEFAULT_ZOOM
                    )
                )
            }
        }
    }

    private fun onErrorState(error: String) {
        val snackbar = Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent))
        snackbar.show()
    }

    override fun onMapReady(map: GoogleMap) {
        map.setMaxZoomPreference(DEFAULT_ZOOM)
        map.setMinZoomPreference(DEFAULT_ZOOM)
        map.setOnMapLongClickListener(this)
        googleMap = map
        if (currentLatitude != 0.0 && currentLongitude != 0.0) {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(currentLatitude, currentLongitude),
                    DEFAULT_ZOOM
                )
            )
        }
    }

    override fun onMapLongClick(p0: LatLng) =
        viewModel.newMarker(latitude = p0.latitude, longitude = p0.longitude)
}