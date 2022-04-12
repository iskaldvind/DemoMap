package studio.iskaldvind.demomap.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<T> : ViewModel() {
	
	abstract val initialState: T
	
	protected val internalState: MutableStateFlow<T> by lazy { MutableStateFlow(initialState) }
	
	val state: StateFlow<T> = internalState
	
	val viewModelCoroutineScope = CoroutineScope(
		Dispatchers.Default + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
			handleError(throwable)
		}
	)
	
	override fun onCleared() {
		super.onCleared()
		cancelJob()
	}
	
	private fun cancelJob() =
		viewModelCoroutineScope.coroutineContext.cancelChildren()
	
	abstract fun handleError(error: Throwable)
}