package studio.iskaldvind.demomap.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import studio.iskaldvind.demomap.core.BaseActivity
import studio.iskaldvind.demomap.R.layout.main_activity
import studio.iskaldvind.demomap.databinding.MainActivityBinding
import studio.iskaldvind.demomap.viewmodel.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.iskaldvind.demomap.R
import studio.iskaldvind.demomap.model.states.MainState

class MainActivity : BaseActivity(main_activity) {

	companion object {
		private const val PERM_FINE_LOCATION: String = Manifest.permission.ACCESS_FINE_LOCATION
		private const val PERM_COARSE_LOCATION: String = Manifest.permission.ACCESS_COARSE_LOCATION
		private const val LOCATION_PERMISSION_REQUEST_CODE: Int = 10
	}

	private val binding: MainActivityBinding by viewBinding()
	private val viewModel: MainViewModel by viewModel()

	private enum class Navs { Map, Details }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		checkPermissions()
		with(binding) {
			navMap.setOnClickListener { onNavClicked(nav = Navs.Map) }
			navDetails.setOnClickListener { onNavClicked(nav = Navs.Details) }
		}
		setActiveNav(nav = Navs.Map)
		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.state.collect { state ->
					when (state) {
						is MainState.Loading -> {}
						is MainState.Success -> onSuccessState()
						is MainState.Error -> onErrorState(error = state.error)
					}
				}
			}
		}
	}

	private fun onNavClicked(nav: Navs) {
		setActiveNav(nav = nav)
		showFragment(nav = nav)
	}

	private fun showFragment(nav: Navs) {
		when (nav) {
			Navs.Map -> showMap()
			Navs.Details -> showDetails()
		}
	}

	fun showEdit(id: Int, latitude: Double, longitude: Double, title: String, annotation: String) {
		val transaction = supportFragmentManager.beginTransaction()
		val fragment = EditFragment.newInstance(
			id = id,
			latitude = latitude,
			longitude = longitude,
			title = title,
			annotation = annotation
		)
		transaction.replace(R.id.fragment, fragment, EditFragment.TAG).commit()
	}

	fun showDetails() {
		val transaction = supportFragmentManager.beginTransaction()
		val fragment = ListFragment.newInstance()
		transaction.replace(R.id.fragment, fragment, ListFragment.TAG).commit()
	}

	private fun showMap() {
		val transaction = supportFragmentManager.beginTransaction()
		val fragment = MapFragment.newInstance()
		transaction.replace(R.id.fragment, fragment, ListFragment.TAG).commit()
	}

	private fun setActiveNav(nav: Navs) {
		val activeColorId = R.color.secondary_dark
		val activeColor = getColor(activeColorId)
		val inactiveColorId = R.color.shadowed
		val inactiveColor = getColor(inactiveColorId)
		with(binding) {
			navMap.setTextColor(if (nav == Navs.Map) activeColor else inactiveColor)
			navMap.setIconTintResource(if (nav == Navs.Map) activeColorId else inactiveColorId)
			navDetails.setTextColor(if (nav == Navs.Details) activeColor else inactiveColor)
			navDetails.setIconTintResource(if (nav == Navs.Details) activeColorId else inactiveColorId)
		}
	}

	private fun checkPermissions() {
		val isGranted = { perm: String ->
			ActivityCompat
				.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
		}
		val permissionFine = isGranted(PERM_FINE_LOCATION)
		val permissionCoarse = isGranted(PERM_COARSE_LOCATION)
		val rationaleFine = shouldShowRequestPermissionRationale(PERM_FINE_LOCATION)
		val rationaleCoarse = shouldShowRequestPermissionRationale(PERM_COARSE_LOCATION)
		this.let {
			when {
				permissionFine && permissionCoarse ->
					viewModel.initApp(
						permissionFine = true,
						permissionCoarse = true
					)
				rationaleFine || rationaleCoarse ->
					AlertDialog
						.Builder(it)
						.setTitle(getString(R.string.permissions_locations_title))
						.setMessage(getString(R.string.permissions_locations_coarse))
						.setPositiveButton(getString(R.string.permissions_positive)) { _, _ ->
							requestLocationPermissions()
						}.setNegativeButton("Cancel") { dialog, _ ->
							dialog.dismiss()
							viewModel.initApp(
								permissionFine = permissionFine,
								permissionCoarse = permissionCoarse
							)
						}.create().show()
				else -> requestLocationPermissions()
			}
		}
	}

	private fun requestLocationPermissions() =
		ActivityCompat
			.requestPermissions(
				this,
				arrayOf(PERM_FINE_LOCATION, PERM_COARSE_LOCATION),
				LOCATION_PERMISSION_REQUEST_CODE
			)

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode) {
			LOCATION_PERMISSION_REQUEST_CODE -> {
				if (
					grantResults.size == 2
					&& (grantResults[0] == PackageManager.PERMISSION_GRANTED
							|| grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
					viewModel.initApp(
						permissionFine = true,
						permissionCoarse = true
					)
				} else {
					this.let {
						val permissionGranted = { permission: String ->
							grantResults.isNotEmpty()
									&& permissions.isNotEmpty()
									&& permissions[0] == permission
									&& grantResults[0] == PackageManager.PERMISSION_GRANTED
						}
						val permissionFine = permissionGranted(PERM_FINE_LOCATION)
						val permissionCoarse = permissionGranted(PERM_COARSE_LOCATION)
						AlertDialog.Builder(it)
							.setTitle(getString(R.string.permissions_locations_title))
							.setMessage(getString(R.string.permissions_locations_not_granted))
							.setNegativeButton("OK") { dialog, _ ->
								dialog.dismiss()
								viewModel.initApp(
									permissionFine = permissionFine,
									permissionCoarse = permissionCoarse
								)
							}.create().show()
					}
				}
				return
			}
		}
	}

	private fun onSuccessState() {
		with(binding) {
			navigation.visibility = View.VISIBLE
		}
		showFragment(nav = Navs.Map)
	}

	private fun onErrorState(error: String) {
		val snackbar = Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
		snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.accent))
		snackbar.show()
	}
}