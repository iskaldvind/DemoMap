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
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.iskaldvind.demomap.R
import studio.iskaldvind.demomap.core.BaseFragment
import studio.iskaldvind.demomap.R.layout.edit_fragment
import studio.iskaldvind.demomap.databinding.EditFragmentBinding
import studio.iskaldvind.demomap.model.states.EditState
import studio.iskaldvind.demomap.utils.arguments
import studio.iskaldvind.demomap.viewmodel.edit.EditViewModel

class EditFragment : BaseFragment(edit_fragment) {

    companion object {
        const val TAG = "EDIT"
        private const val ID = "EditFragment.params.id"
        private const val LATITUDE = "EditFragment.params.latitude"
        private const val LONGITUDE = "EditFragment.params.longitude"
        private const val TITLE = "EditFragment.params.title"
        private const val ANNOTATION = "EditFragment.params.annotation"
        fun newInstance(
            id: Int,
            latitude: Double,
            longitude: Double,
            title: String,
            annotation: String
        ): Fragment = EditFragment().arguments(
            ID to id,
            LATITUDE to latitude,
            LONGITUDE to longitude,
            TITLE to title,
            ANNOTATION to annotation
        )
    }

    private val markerId: Int by lazy { arguments?.getInt(ID) ?: 0 }
    private val latitude: Double by lazy { arguments?.getDouble(LATITUDE) ?: 0.0 }
    private val longitude: Double by lazy { arguments?.getDouble(LONGITUDE) ?: 0.0 }
    private val title: String by lazy { arguments?.getString(TITLE) ?: "" }
    private val annotation: String by lazy { arguments?.getString(ANNOTATION) ?: "" }

    private val binding: EditFragmentBinding by viewBinding()
    private val viewModel: EditViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is EditState.Loading -> {}
                        is EditState.Success -> onSuccess()
                        is EditState.Error -> onErrorState(error = state.error)
                    }
                }
            }
        }
        val coordinatesText = "$latitude $longitude"
        binding.markerCoordinates.text = coordinatesText
        if (title.isNotBlank()) binding.title.setText(title)
        if (annotation.isNotBlank()) binding.annotation.setText(annotation)
        binding.buttonDelete.setOnClickListener {
            viewModel.deleteMarker(id = markerId)
        }
        binding.buttonSave.setOnClickListener {
            viewModel.updateMarker(
                id = markerId,
                title = binding.title.text.toString(),
                annotation = binding.annotation.text.toString(),
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    private fun onSuccess() {
        (requireActivity() as MainActivity).showDetails()
    }

    private fun onErrorState(error: String) {
        val snackbar = Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent))
        snackbar.show()
    }
}