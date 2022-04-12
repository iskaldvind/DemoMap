package studio.iskaldvind.demomap.view

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupieAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.iskaldvind.demomap.R
import studio.iskaldvind.demomap.core.BaseFragment
import studio.iskaldvind.demomap.R.layout.list_fragment
import studio.iskaldvind.demomap.databinding.ListFragmentBinding
import studio.iskaldvind.demomap.model.entities.AppMarker
import studio.iskaldvind.demomap.model.states.ListState
import studio.iskaldvind.demomap.viewmodel.list.ListViewModel

class ListFragment: BaseFragment(list_fragment) {

    companion object {
        const val TAG = "LIST"
        fun newInstance(): Fragment = ListFragment()
    }

    private val binding: ListFragmentBinding by viewBinding()
    private val viewModel: ListViewModel by viewModel()
    private val adapter by lazy { GroupieAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is ListState.Loading -> {}
                        is ListState.Data -> onDataState(markers = state.markers)
                        is ListState.Error -> onErrorState(error = state.error)
                    }
                }
            }
        }
        with(binding) {
            recycler.adapter = adapter
            adapter.setOnItemClickListener { item, _ ->
                (item as MarkerListItem).let { marker ->
                    (requireActivity() as  MainActivity).showEdit(
                        id = marker.id,
                        latitude = marker.latitude,
                        longitude = marker.longitude,
                        title = marker.title,
                        annotation = marker.annotation
                    )
                }
            }
        }
        viewModel.init()
    }

    private fun onDataState(markers: List<AppMarker>) {
        adapter.clear()
        markers.forEach { marker ->
            adapter.add(MarkerListItem(
                id = marker.id,
                latitude = marker.latitude,
                longitude = marker.longitude,
                title = marker.title,
                annotation = marker.annotation
            ))
        }
    }

    private fun onErrorState(error: String) {
        val snackbar = Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent))
        snackbar.show()
    }
}