package cn.yue.base.photo

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.R
import cn.yue.base.fragment.BaseFragment
import cn.yue.base.image.ImageLoader.Companion.loadImage
import cn.yue.base.photo.data.MediaData
import cn.yue.base.photo.data.MediaType
import cn.yue.base.photo.data.MimeType
import cn.yue.base.photo.preview.ViewMediaActivity
import cn.yue.base.utils.app.RunTimePermissionUtil.requestPermissions
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.LogUtils
import cn.yue.base.utils.variable.TimeUtils
import cn.yue.base.widget.recyclerview.CommonAdapter
import cn.yue.base.widget.recyclerview.CommonViewHolder
import java.util.concurrent.Executors

/**
 * Description :
 * Created by yue on 2019/3/11
 */
class SelectPhotoFragment : BaseFragment() {
    private val pageCount = 50
    private var adapter: CommonAdapter<MediaData>? = null
    private val photoList: MutableList<MediaData> = ArrayList()
    private var page = 0
    private var isCanLoadMore = true

    override fun getLayoutId(): Int {
        return R.layout.fragment_select_photo
    }

    override fun needScaffold(): Boolean {
        return false
    }

    override fun initView(savedInstanceState: Bundle?) {
        val photoRV = requireViewById<RecyclerView>(R.id.photoRV)
        photoRV.layoutManager = GridLayoutManager(mActivity, 3)
        photoRV.adapter = object : CommonAdapter<MediaData>(mActivity, photoList) {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_select_photo
            }

            override fun bindData(holder: CommonViewHolder, position: Int, mediaData: MediaData) {
                val photoIV = holder.getView<ImageView>(R.id.photoIV)
                val checkIV = holder.getView<ImageView>(R.id.checkIV)
                val timeTV = holder.getView<TextView>(R.id.timeTV)
                photoIV?.setBackgroundColor(Color.parseColor("#ffffff"))
                photoIV?.loadImage(mediaData.uri)
                if (MimeType.isVideo(mediaData.mimeType)) {
                    timeTV?.visibility = View.VISIBLE
                    timeTV?.text = TimeUtils.formatDuration(mediaData.duration)
                } else {
                    timeTV?.visibility = View.GONE
                }
                photoIV?.setOnClickListener {
                    if (getIsPreview()) {
                        val intent = Intent(mActivity, ViewMediaActivity::class.java)
                        intent.putExtra("mediaType", mediaData.getMediaType().value)
                        intent.putParcelableArrayListExtra("uris", arrayListOf(mediaData.uri))
                        startActivity(intent)
                    } else {
                        checkIV?.let {
                            if (getSelectList().size < getMaxNum() || it.isSelected) {
                                it.isSelected = !it.isSelected
                                addSelectList(mediaData, it.isSelected)
                                (mActivity as SelectPhotoActivity).setTitleOpText(
                                    if (getSelectList().isEmpty()) {
                                        R.string.app_cancel.getString()
                                    } else {
                                        "${R.string.app_confirm.getString()}(" + getSelectList().size + "/" + getMaxNum() + ")"
                                    }
                                )
                            }
                        }
                    }
                }
                checkIV?.setOnClickListener {
                    checkIV.let {
                        if (getSelectList().size < getMaxNum() || it.isSelected) {
                            it.isSelected = !it.isSelected
                            addSelectList(mediaData, it.isSelected)
                            (mActivity as SelectPhotoActivity).setTitleOpText(
                                if (getSelectList().isEmpty()) {
                                    R.string.app_cancel.getString()
                                } else {
                                    "${R.string.app_confirm.getString()}(" + getSelectList().size + "/" + getMaxNum() + ")"
                                })
                        }
                    }
                }
                checkIV?.isSelected = contains(mediaData)
            }
        }.also { adapter = it }
        photoRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                LogUtils.e("" + layoutManager!!.findLastVisibleItemPosition())
                if (photoList.size - 5 <= layoutManager.findLastVisibleItemPosition() && isCanLoadMore) {
                    isCanLoadMore = false
                    getPhotoList()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        getPhotoList()
    }

    fun refresh(folderId: String?) {
        this.folderId = folderId
        allMedia = null
        adapter!!.clear()
        page = 0
        photoList.clear()
        getPhotoList()
    }

    private var folderId: String? = null
    private fun getPhotoList() {
        val searchBlock = {
            val threadPoolUtils = Executors.newSingleThreadExecutor()
            threadPoolUtils.execute(Runnable {
                if (allMedia == null) {
                    allMedia = when {
                        TextUtils.isEmpty(folderId) -> {
	                        PhotoUtils.getTheLastMedias(mActivity, 100, getMediaType())
                        }
                        folderId!!.toInt() == -1 -> {
	                        PhotoUtils.getMediaByFolder(mActivity, true, folderId, getMediaType())
                        }
                        else -> {
	                        PhotoUtils.getMediaByFolder(mActivity, false, folderId, getMediaType())
                        }
                    }
                }
                val fromIndex = page * pageCount
                if (fromIndex >= allMedia!!.size) {
                    handler.sendMessage(Message.obtain(handler, 101, Pair(page, null)))
                    return@Runnable
                }
                var toIndex = (page + 1) * pageCount
                if (toIndex > allMedia!!.size) {
                    toIndex = allMedia!!.size
                }
                val list = allMedia!!.subList(fromIndex, toIndex)
                handler.sendMessage(Message.obtain(handler, 101, Pair(page, list)))
            })
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mActivity.requestPermissions({
                searchBlock.invoke()
            }, {}, Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            mActivity.requestPermissions({
                searchBlock.invoke()
            }, {}, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private var allMedia: List<MediaData>? = null
    private var handler = Handler(Looper.getMainLooper()) { msg ->
        if (msg.what == 101) {
            val addList = msg.obj as Pair<Int, List<MediaData>?>
            if (addList.second.isNullOrEmpty()) {
                isCanLoadMore = false
            } else {
                isCanLoadMore = true
                if (page == addList.first) {
                    photoList.addAll(addList.second!!)
                    adapter!!.setList(photoList)
                }
                page++
            }
        }
        false
    }

    private fun getSelectList(): MutableList<MediaData> {
        return (mActivity as SelectPhotoActivity).getPhotoList()
    }

    private fun getMaxNum(): Int {
        val num = (mActivity as SelectPhotoActivity).getMaxNum()
        return if ( num <= 0) 1 else num
    }

    private fun getMediaType(): MediaType {
        return (mActivity as SelectPhotoActivity).getMediaType()
    }

    private fun getIsPreview(): Boolean {
        return (mActivity as SelectPhotoActivity).getIsPreview()
    }

    private fun addSelectList(mediaData: MediaData, checked: Boolean) {
        if (checked) {
            for (mediaVO1 in getSelectList()) {
                if (MediaData.equals(mediaData, mediaVO1)) {
                    return
                }
            }
            getSelectList().add(mediaData)
        } else {
            val iterator: MutableIterator<*> = getSelectList().iterator()
            while (iterator.hasNext()) {
                val mediaVO1 = iterator.next() as MediaData
                if (MediaData.equals(mediaData, mediaVO1)) {
                    iterator.remove()
                    return
                }
            }
        }
    }

    private operator fun contains(mediaData: MediaData): Boolean {
        for (mediaVO1 in getSelectList()) {
            if (MediaData.equals(mediaData, mediaVO1)) {
                return true
            }
        }
        return false
    }
}