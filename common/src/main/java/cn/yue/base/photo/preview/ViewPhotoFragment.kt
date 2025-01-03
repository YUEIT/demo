package cn.yue.base.photo.preview

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import cn.yue.base.R
import cn.yue.base.fragment.BaseFragment
import cn.yue.base.router.Route
import cn.yue.base.view.TitleBarView
import cn.yue.base.view.TopBarView

/**
 * Description :
 * Created by yue on 2019/3/11
 */
@Route(path = "/common/viewPhoto")
class ViewPhotoFragment : BaseFragment() {
    private var photoUriList: MutableList<Uri> = ArrayList()
    private var currentIndex = 0
    override fun getLayoutId(): Int {
        return R.layout.activity_view_photo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getParcelableArrayList<Uri>("uris")?.let {
                photoUriList.addAll(it)
            }
            currentIndex = getInt("position")
        }
    }

    private var titleBar: TitleBarView? = null

    override fun initTopBar(topBarView: TopBarView) {
        super.initTopBar(topBarView)
        titleBar = topBarView.getTitleBar()
        if (photoUriList.isNotEmpty()) {
            titleBar?.setCenterTextStr((currentIndex + 1).toString() + "/" + photoUriList.size)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        val viewPager = requireViewById<PhotoViewPager>(R.id.viewPager)
        viewPager.adapter = photoAdapter
        viewPager.currentItem = currentIndex
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if (photoUriList.isNotEmpty()) {
                    titleBar?.setCenterTextStr((position + 1).toString() + "/" + photoUriList.size)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private val photoAdapter: PagerAdapter = object : PagerAdapter() {
        private val mViewCache = HashMap<Int, PhotoView>()
        override fun getCount(): Int {
            return photoUriList.size
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            val photoView = mViewCache[position]
            if (photoUriList.isNotEmpty()) {
                (container as PhotoViewPager).setCurrentPhotoView(photoView!!, position, null, photoUriList[position])
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var photoView = mViewCache[position]
            if (photoView == null) {
                photoView = PhotoView(mActivity)
                photoView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                if (photoUriList.isNotEmpty()) {
                    photoView.loadImage(photoUriList[position])
                }
                mViewCache[position] = photoView
            }
            container.addView(photoView)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
            val photoView = mViewCache[position]
            if (photoView != null) {
                mViewCache.remove(position)
            }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }
}