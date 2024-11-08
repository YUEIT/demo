package cn.yue.base.mvp.photo

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import cn.yue.base.R
import cn.yue.base.activity.BaseFragment
import cn.yue.base.net.observer.WrapperObserver
import cn.yue.base.photo.SelectPhotoActivity
import cn.yue.base.photo.data.MediaData
import cn.yue.base.utils.app.RunTimePermissionUtil.requestPermissions
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.utils.file.AndroidQFileUtils
import cn.yue.base.utils.file.BitmapFileUtils
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Description :
 * Created by yue on 2019/6/18
 */
class PhotoHelper(val fragment: BaseFragment, private val iPhotoView: IPhotoView) {

	private var targetUri: Uri? = null
	private var cachePhotoUri: Uri? = null
	private var cropFilePath: String? = null
	private var selectCache: MutableList<MediaData> = ArrayList()
	private var maxNum: Int = 1

	fun setMaxNum(maxNum: Int): PhotoHelper {
		this.maxNum = maxNum
		return this
	}

	private val selectSystemPhotoLauncher = fragment.registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) {
		if (it.resultCode == Activity.RESULT_OK) {
			it.data?.let { intent ->
				cachePhotoUri = intent.data
				val temp: MutableList<MediaData> = ArrayList()
				val mediaData = MediaData().apply {
					uri = cachePhotoUri
				}
				temp.add(mediaData)
				iPhotoView.selectImageResult(this, temp)
			}
		}
	}

	fun openSystemAlbum() {
		val selectBlock = {
			val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
			selectSystemPhotoLauncher.launch(intent)
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			fragment.mActivity.requestPermissions({
				selectBlock.invoke()
			}, {}, Manifest.permission.READ_MEDIA_IMAGES)
		} else {
			fragment.mActivity.requestPermissions({
				selectBlock.invoke()
			}, {}, Manifest.permission.READ_EXTERNAL_STORAGE)
		}
	}

	private val selectPhotoLauncher = fragment.registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) {
		if (it.resultCode == Activity.RESULT_OK) {
			val selectList = it.data?.getParcelableArrayListExtra<MediaData>("medias")
			if (selectList != null) {
				selectCache.clear()
				selectCache.addAll(selectList)
				if (selectList.size == 1) {
					cachePhotoUri = selectList[0].uri
				}
				iPhotoView.selectImageResult(this, selectList)
			}
		}
	}

	fun openAlbum() {
		val selectBlock = {
			val intent = Intent(fragment.mActivity, SelectPhotoActivity::class.java)
			intent.putParcelableArrayListExtra("medias", selectCache as ArrayList<MediaData>)
			intent.putExtra("maxNum", maxNum)
			selectPhotoLauncher.launch(intent)
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			fragment.mActivity.requestPermissions({
				selectBlock.invoke()
			}, {}, Manifest.permission.READ_MEDIA_IMAGES)
		} else {
			fragment.mActivity.requestPermissions({
				selectBlock.invoke()
			}, {}, Manifest.permission.READ_EXTERNAL_STORAGE)
		}
	}

	fun openAlbum(selectList: List<MediaData>?) {
		selectCache.clear()
		if (selectList != null && selectList.isNotEmpty()) {
			selectCache.addAll(selectList)
		}
		val selectBlock = {
			val intent = Intent(fragment.mActivity, SelectPhotoActivity::class.java)
			intent.putParcelableArrayListExtra("medias", selectCache as ArrayList<MediaData>)
			intent.putExtra("maxNum", maxNum)
			selectPhotoLauncher.launch(intent)
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			fragment.mActivity.requestPermissions({
				selectBlock.invoke()
			}, {}, Manifest.permission.READ_MEDIA_IMAGES)
		} else {
			fragment.mActivity.requestPermissions({
				selectBlock.invoke()
			}, {}, Manifest.permission.READ_EXTERNAL_STORAGE)
		}
	}

	private val cameraLauncher = fragment.registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) {
		if (it.resultCode == Activity.RESULT_OK) {
			val temp: MutableList<MediaData> = ArrayList()
			val mediaData = MediaData().apply {
				uri = cachePhotoUri
			}
			temp.add(mediaData)
			iPhotoView.selectImageResult(this, temp)
		}
	}

	fun openCamera() {
		fragment.mActivity.requestPermissions({
			val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
			val tempFile = BitmapFileUtils.createRandomFile()
			targetUri = AndroidQFileUtils.getUriForFile(tempFile)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
			}
			if (targetUri != null) {
				intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
				cameraLauncher.launch(intent)
			}
			cachePhotoUri = AndroidQFileUtils.getUriForFile(tempFile)
		}, {}, Manifest.permission.CAMERA)
	}

	fun autoCropPhoto() {
		cropPhoto(true, 0, 0)
	}

	fun cropPhoto() {
		cropPhoto(false, 1, 1)
	}

	private val cropPhotoLauncher = fragment.registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) {
		if (it.resultCode == Activity.RESULT_OK) {
			iPhotoView.cropImageResult(this, MediaData().apply {
				uri = cachePhotoUri!!
				url = cropFilePath
			})
		}
	}

	fun cropPhoto(autoCrop: Boolean, aspectX: Int, aspectY: Int) {
		if (cachePhotoUri == null) {
			ToastUtils.showShortToast(R.string.app_no_crop_picture.getString())
			return
		}
		fragment.lifecycleScope.launch {
			val intent = Intent("com.android.camera.action.CROP")
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
			val targetFile = withContext(Dispatchers.IO) {
				AndroidQFileUtils.createNewFile(fragment.mActivity, cachePhotoUri!!, ".jpg")
			}
			targetUri = AndroidQFileUtils.getUriForFile(targetFile)
//            cachePhotoUri = AndroidQFileUtils.grantPermissionUri(cachePhotoUri!!)
//		targetUri = cachePhotoUri
			val tempFile = BitmapFileUtils.createRandomFile()
			val outPutUri = AndroidQFileUtils.getUriForFile(tempFile)
			cropFilePath = tempFile.path
			cachePhotoUri = outPutUri
			intent.setDataAndType(targetUri, "image/*")
			//这里clipData的目的主要是赋予outPutUri给裁剪工具读写权限
			intent.clipData = ClipData.newRawUri("", outPutUri)
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri)
			intent.putExtra("crop", "true")//可裁剪
			if (!autoCrop) {
				intent.putExtra("aspectX", aspectX)
				intent.putExtra("aspectY", aspectY)
			}
			intent.putExtra("scale", false)
			//若为false则表示不返回数据
			intent.putExtra("return-data", false)
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
			intent.putExtra("noFaceDetection", true)
			//将存储图片的uri读写权限授权给剪裁工具应用，这里可能存在检索不出来的情况
//			val resInfoList: List<ResolveInfo> = fragment.mActivity.packageManager
//				.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//			for (resolveInfo in resInfoList) {
//				val packageName = resolveInfo.activityInfo.packageName
//				fragment.mActivity.grantUriPermission(
//					packageName,
//					outPutUri,
//					Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
//				)
//			}
			cropPhotoLauncher.launch(intent)
		}
	}

	private fun <T : Any> runOnWork(work: () -> T, success: (t: T) -> Unit) {
		Single.fromCallable {
			work.invoke()
		}.compose(fragment.getLifecycleProvider()!!.toBindLifecycle())
			.subscribe(
				WrapperObserver<T>(
					successBlock = success
				)
			)
	}
}