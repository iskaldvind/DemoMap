package studio.iskaldvind.demomap.viewmodel.edit

import kotlinx.coroutines.launch
import studio.iskaldvind.demomap.core.base.BaseViewModel
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
}