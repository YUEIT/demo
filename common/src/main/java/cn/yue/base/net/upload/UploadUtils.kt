package cn.yue.base.net.upload

import cn.yue.base.activity.rx.ILifecycleProvider
import cn.yue.base.net.RetrofitManager
import cn.yue.base.utils.file.BitmapFileUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Description :
 * Created by yue on 2019/6/18
 */
object UploadUtils {
    private val uploadServer = RetrofitManager.instance.getRetrofit("upload").create(UploadServer::class.java)

    fun getUploadServer(): UploadServer {
        return uploadServer
    }

    fun <E: Any> upload(files: List<String>, lifecycleProvider: ILifecycleProvider<E>, uploadObserver: SingleObserver<ImageResultListData>) {
        getCompressFileList(files)
            .subscribeOn(Schedulers.io())
            .flatMap {
                val url = getUploadKey()
                getUploadServer().upload(url, filesToMultipartBodyParts(it))
            }
            .subscribeOn(Schedulers.newThread())
            .compose(lifecycleProvider.toBindLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(uploadObserver)
    }

    private fun filesToMultipartBodyParts(files: List<File>): List<MultipartBody.Part> {
        val parts = ArrayList<MultipartBody.Part>(files.size)
        for (file in files) {
            val requestBody = RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file)
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            parts.add(part)
        }
        return parts
    }

    fun getCompressFileList(list: List<String>): Single<List<File>> {
        return Single.just(list)
                .flatMap { strings ->
                    val files = ArrayList<File>()
                    for (url in strings) {
                        val file = BitmapFileUtils.getCompressBitmapFile(url)
                        files.add(file)
                    }
                    Single.just<List<File>>(files)
                }
    }

    private fun getUploadKey(): String {
        return "LKdCxyqv0tvAuyT2NJmelw"
    }

}