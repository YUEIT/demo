package cn.yue.base.video

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import cn.yue.base.R
import cn.yue.base.activity.BaseFragment
import cn.yue.base.router.Route
import cn.yue.base.utils.code.getParcelableArrayListExt

@Route(path = "/common/viewVideo")
class ViewVideoFragment: BaseFragment() {

    lateinit var exoPlayer: ExoPlayer

    override fun getLayoutId(): Int {
        return R.layout.fragment_view_video
    }

    override fun initView(savedInstanceState: Bundle?) {
        val playerView = requireViewById<PlayerView>(R.id.playerView)
        exoPlayer = ExoPlayer.Builder(mActivity).build()
        val uris = arguments?.getParcelableArrayListExt("uris", Uri::class)
        uris?.let {
            playerView.player = exoPlayer
            for (uri in uris) {
                val mediaItem: MediaItem = MediaItem.fromUri(uri)
                exoPlayer.addMediaItem(mediaItem)
            }
            exoPlayer.prepare()
            exoPlayer.play()
        }

    }

    override fun onStart() {
        super.onStart()
        if (!exoPlayer.isPlaying) {
            exoPlayer.play()
        }
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}