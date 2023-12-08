package win.winbasket.ball.winline.game

import android.content.Context
import android.media.MediaPlayer
import kotlin.math.ln

class SoundsManager private constructor(val ctx: Context) {

    private var music: MediaPlayer = MediaPlayer.create(ctx,R.raw.music)
    private var click: MediaPlayer = MediaPlayer.create(ctx,R.raw.sound)


    init {
        music.setOnCompletionListener { it.start() }
        if(ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getBoolean("music",false)) music.start()
    }

    companion object {
        private var manager: SoundsManager? = null
        fun createInstance(ctx: Context) {
            if(manager==null)    manager = SoundsManager(ctx)
        }
        fun getInstance(): SoundsManager {
            return manager!!
        }
    }

    fun changeMusic(b: Boolean) {
        if(b) music.start()
        else music.pause()
    }

    fun startClickSound() {
        if(ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getBoolean("sounds",false)) {
            click.seekTo(0)
            click.start()
        }
    }


    fun destroy() {
        music.stop()
        music.release()
        click.stop()
        click.release()

    }

    fun pause() {
        music.pause()
    }
    fun start() {
        if(ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getBoolean("music",false)) music.start()
    }

}