package studio.iskaldvind.demomap.di

import android.location.LocationManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import studio.iskaldvind.demomap.model.states.AppState
import studio.iskaldvind.demomap.utils.Geolocator
import studio.iskaldvind.demomap.viewmodel.edit.EditInteractor
import studio.iskaldvind.demomap.viewmodel.edit.EditViewModel
import studio.iskaldvind.demomap.viewmodel.list.ListInteractor
import studio.iskaldvind.demomap.viewmodel.list.ListViewModel
import studio.iskaldvind.demomap.viewmodel.main.MainInteractor
import studio.iskaldvind.demomap.viewmodel.main.MainViewModel
import studio.iskaldvind.demomap.viewmodel.map.MapInteractor
import studio.iskaldvind.demomap.viewmodel.map.MapViewModel

val application = module {
	single {
		Geolocator(locationManager = androidContext().getSystemService(LocationManager::class.java))
	}
	single { AppState() }
}

val main = module {
	single {
		MainInteractor(geolocator = get(), state = get())
	}
	viewModel { MainViewModel(interactor = get()) }
}

val map = module {
	single { MapInteractor() }
	viewModel { MapViewModel(interactor = get(), geolocator = get()) }
}

val list = module {
	single { ListInteractor() }
	viewModel { ListViewModel(interactor = get()) }
}

val edit = module {
	single { EditInteractor() }
	viewModel { EditViewModel(interactor = get()) }
}