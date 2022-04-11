package studio.iskaldvind.demomap.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import studio.iskaldvind.demomap.core.base.BaseFragment
import studio.iskaldvind.demomap.R.layout.map_fragment
import studio.iskaldvind.demomap.databinding.MapFragmentBinding
import studio.iskaldvind.demomap.viewmodel.map.MapViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.iskaldvind.demomap.R
import studio.iskaldvind.demomap.model.entities.Marker
import studio.iskaldvind.demomap.model.states.MapState

class MapFragment: BaseFragment(map_fragment) {

    companion object {
        fun newInstance(): Fragment = MapFragment()
    }

    private val binding: MapFragmentBinding by viewBinding()
    private val viewModel: MapViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        markers: List<Marker>,
        latitude: Double,
        longitude: Double
    ) {
        Log.e("MAP", "lat $latitude, lon $longitude")
    }

    private fun onErrorState(error: String) {
        val snackbar = Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent))
        snackbar.show()
    }
}