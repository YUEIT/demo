package cn.yue.base.photo.helper

import cn.yue.base.photo.data.MediaData


/**
 * Description :
 * Created by yue on 2019/6/18
 */
interface IPhotoView {
    fun selectImageResult(helper: PhotoHelper, selectList: List<MediaData>?)
    fun cropImageResult(helper: PhotoHelper, cropImage: MediaData)
}