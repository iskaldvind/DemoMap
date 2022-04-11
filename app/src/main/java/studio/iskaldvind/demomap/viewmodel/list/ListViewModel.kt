package studio.iskaldvind.demomap.viewmodel.list

import kotlinx.coroutines.launch
import studio.iskaldvind.demomap.core.base.BaseViewModel
import studio.iskaldvind.demomap.model.states.ListState

class ListViewModel(
	private val interactor: ListInteractor
) : BaseViewModel<ListState>() {
	
	override val initialState: ListState
		get() = ListState.Loading
	
	override fun handleError(error: Throwable) {
		viewModelCoroutineScope.launch {
			internalState.value = ListState.Error(error = error.message ?: "Internal Error")
		}
	}
}