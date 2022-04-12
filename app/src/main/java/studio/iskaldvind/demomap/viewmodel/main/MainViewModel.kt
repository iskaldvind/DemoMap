package studio.iskaldvind.demomap.viewmodel.main

import kotlinx.coroutines.launch
import studio.iskaldvind.demomap.core.BaseViewModel
import studio.iskaldvind.demomap.model.states.MainState

class MainViewModel(
	private val interactor: MainInteractor
) : BaseViewModel<MainState>() {
	
	override val initialState: MainState
		get() = MainState.Loading
	
	fun initApp(permissionFine: Boolean, permissionCoarse: Boolean) {
		stopGeolocator()
		interactor.startGeolocator(permissionFine, permissionCoarse)
		viewModelCoroutineScope.launch {
			internalState.value = MainState.Success
		}
	}

	private fun stopGeolocator() {
		interactor.stopGeolocator()
	}
	
	override fun handleError(error: Throwable) {
		viewModelCoroutineScope.launch {
			internalState.value = MainState.Error(error = error.message ?: "Internal Error")
		}
	}

}