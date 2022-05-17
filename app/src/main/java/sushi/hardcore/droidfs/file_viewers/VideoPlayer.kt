package sushi.hardcore.droidfs.file_viewers

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.widget.RelativeLayout
import com.google.android.exoplayer2.ExoPlayer
import sushi.hardcore.droidfs.databinding.ActivityVideoPlayerBinding

class VideoPlayer: MediaPlayer() {
    private var firstPlay = true
    private val autoFit by lazy {
        sharedPrefs.getBoolean("autoFit", false)
    }
    private lateinit var binding: ActivityVideoPlayerBinding

    override fun viewFile() {
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.videoPlayer.doubleTapOverlay = binding.doubleTapOverlay
        binding.videoPlayer.setControllerVisibilityListener { visibility ->
            binding.topBar.visibility = visibility

            if (visibility == View.VISIBLE) {
                showPartialSystemUi()
            }
            else {
                hideSystemUi()
            }
        }
        binding.rotateButton.setOnClickListener {
            requestedOrientation =
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                }
        }
        super.viewFile()
    }

    override fun bindPlayer(player: ExoPlayer) {
        binding.videoPlayer.player = player
    }

    override fun onNewFileName(fileName: String) {
        binding.textFileName.text = fileName
    }

    override fun getFileType(): String {
        return "video"
    }

    override fun onVideoSizeChanged(width: Int, height: Int) {
        if (firstPlay && autoFit) {
            requestedOrientation = if (width < height)
                ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            else
                ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            firstPlay = false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val navigationBarHeight = getNavigationBarHeight()
            binding.topBar.layoutParams = createTopBarLayoutParams(navigationBarHeight, navigationBarHeight)
        } else {
            binding.topBar.layoutParams = createTopBarLayoutParams(20, 0)
        }
    }

    private fun getNavigationBarHeight() : Int {
        return resources.getDimensionPixelSize(
            resources.getIdentifier(
                "navigation_bar_height",
                "dimen",
                "android"
            )
        )
    }

    private fun createTopBarLayoutParams(marginStart: Int, marginEnd: Int) : RelativeLayout.LayoutParams {
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.marginStart = marginStart
        layoutParams.marginEnd = marginEnd

        return layoutParams
    }
}