package cn.yue.test.main


import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.test.databinding.ActivitySlidingPlanBinding


/**
 * Description:
 * Created by yue on 21/11/24
 */
class SlidingPlanActivity : BaseFragmentActivity() {

    private lateinit var binding: ActivitySlidingPlanBinding

    override fun initView() {
        super.initView()
        binding = ActivitySlidingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,
            TwoPaneOnBackPressedCallback(binding.vSlide))
    }

    //    fun openDetails(itemId: Int) {
//        val arguments = Bundle()
//        arguments.putInt("itemId", itemId)
//        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
//            .setReorderingAllowed(true)
//            .replace(R.id.detail_container, ItemFragment::class.java, arguments)
//
//
//        // If it's already open and the detail pane is visible, crossfade
//        // between the fragments.
//        if (binding.getSlidingPaneLayout().isOpen()) {
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//        }
//        ft.commit()
//        binding.getSlidingPaneLayout().open()
//    }

    class TwoPaneOnBackPressedCallback(
        private val slidingPaneLayout: SlidingPaneLayout
    ) : OnBackPressedCallback(
        // Set the default 'enabled' state to true only if it is slidable, such as
        // when the panes overlap, and open, such as when the detail pane is
        // visible.
        slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
    ), SlidingPaneLayout.PanelSlideListener {

        init {
            slidingPaneLayout.setPanelSlideListener(this)
        }

        override fun handleOnBackPressed() {
            // Return to the list pane when the system back button is tapped.
            slidingPaneLayout.closePane()
        }

        override fun onPanelSlide(panel: View, slideOffset: Float) { }

        override fun onPanelOpened(panel: View) {
            // Intercept the system back button when the detail pane becomes
            // visible.
            isEnabled = true
        }

        override fun onPanelClosed(panel: View) {
            // Disable intercepting the system back button when the user returns to
            // the list pane.
            isEnabled = true
        }
    }
}