package studio.iskaldvind.demomap.view

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import studio.iskaldvind.demomap.R
import studio.iskaldvind.demomap.databinding.MarkerListItemBinding

class MarkerListItem(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val annotation: String
) : BindableItem<MarkerListItemBinding>() {

    override fun bind(viewBinding: MarkerListItemBinding, position: Int) {
        val locationText = "$latitude $longitude"
        viewBinding.location.text = locationText
        if (title.isBlank()) {
            viewBinding.title.visibility = View.GONE
        } else {
            viewBinding.title.text = title
        }
        if (annotation.isBlank()) {
            viewBinding.annotation.visibility = View.GONE
        } else {
            viewBinding.annotation.text = annotation
        }
    }

    override fun getLayout(): Int =
        R.layout.marker_list_item

    override fun initializeViewBinding(view: View): MarkerListItemBinding =
        MarkerListItemBinding.bind(view)
}