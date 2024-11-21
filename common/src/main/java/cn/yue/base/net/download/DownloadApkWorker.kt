package cn.yue.base.net.download

import android.app.Notification
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkInfo
import androidx.work.WorkerParameters
import cn.yue.base.init.NotificationUtils
import cn.yue.base.init.NotificationUtils.notify
import cn.yue.base.init.NotificationUtils.setIntent
import cn.yue.base.init.NotificationUtils.setShowContent
import cn.yue.base.init.NotificationUtils.setTitle
import cn.yue.base.utils.Utils
import cn.yue.base.utils.app.ActivityUtils

/**
 * Description :
 * Created by yue on 2023/7/26
 */
class DownloadApkWorker(context: Context, workerParameters: WorkerParameters)
	: DownloadWorker(context, workerParameters) {
	
	companion object {
		const val NOTIFICATION_ID = 10
	}
	
	override suspend fun getForegroundInfo(): ForegroundInfo {
		return ForegroundInfo(NOTIFICATION_ID, createNotification())
	}
	
	private fun createNotification(): Notification {
		return NotificationUtils
			.getNotification("应用下载", "", 0, null)
			.build()
	}
	
	private fun notification(content: String, progress: Int?, intent: Intent?) {
		NotificationUtils.getNotificationBuilder()
			.setTitle("应用下载")
			.setShowContent(content, progress)
			.setIntent(intent)
			.notify(NOTIFICATION_ID)
	}
	
	override fun updateWorkState(state: WorkInfo.State, data: Data) {
		super.updateWorkState(state, data)
		when (state) {
			WorkInfo.State.RUNNING -> {
				val progress = data.getInt("progress", 0)
				notification("已下载" , progress, null)
			}
			WorkInfo.State.SUCCEEDED -> {
				val intent = ActivityUtils.installIntent(Utils.getContext(), data.getString("path"))
				notification("下载完成", 100, intent)
			}
			WorkInfo.State.FAILED -> {
				notification("下载失败 " + data.getString("error"), 100, null)
			}
			else -> {}
		}
	}
}