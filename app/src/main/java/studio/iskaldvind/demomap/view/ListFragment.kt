package studio.iskaldvind.demomap.view

import androidx.fragment.app.Fragment
import studio.iskaldvind.demomap.core.base.BaseFragment
import studio.iskaldvind.demomap.R.layout.list_fragment

class ListFragment: BaseFragment(list_fragment) {

    companion object {
        fun newIntance(): Fragment = ListFragment()
    }
}