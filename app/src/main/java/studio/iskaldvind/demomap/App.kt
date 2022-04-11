package studio.iskaldvind.demomap

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import studio.iskaldvind.demomap.di.*

class App : Application() {
	override fun onCreate() {
		super.onCreate()
		startKoin {
			androidContext(applicationContext)
			modules(listOf(
				application,
				main,
				map,
				list,
				edit
			))
		}
	}
}