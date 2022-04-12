package studio.iskaldvind.demomap.viewmodel.edit

import kotlinx.coroutines.launch
import studio.iskaldvind.demomap.core.BaseViewModel
import studio.iskaldvind.demomap.model.states.EditState

class EditViewModel(
	private val interactor: EditInteractor
) : BaseViewModel<EditState>() {
	
	override val initialState: EditState
		get() = EditState.Loading
	
	override fun handleError(error: Throwable) {
		viewModelCoroutineScope.launch {
			internalState.value = EditState.Error(error = error.message ?: "Internal Error")
		}
	}

	fun deleteMarker(id: Int) {
		viewModelCoroutineScope.launch {
			interactor.deleteMarker(id = id) { string ->
				if (string == null) {
					internalState.value = EditState.Success
				} else {
					internalState.value = EditState.Error(error = string)
				}
			}
		}
	}

	fun updateMarker(
		id: Int,
		title: String,
		annotation: String,
		latitude: Double,
		longitude: Double
	) {
		interactor.updateMarker(
			id = id,
			title = title,
			annotation = annotation,
			latitude = latitude,
			longitude = longitude
		) { string ->
			if (string == null) {
				internalState.value = EditState.Success
			} else {
				internalState.value = EditState.Error(error = string)
			}
		}
	}
}