package win.winbasket.ball.winline.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SoundsManager.createInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        SoundsManager.getInstance().start()
        super.onResume()
    }

    override fun onPause() {
        SoundsManager.getInstance().pause()
        super.onPause()
    }

    override fun onDestroy() {
        SoundsManager.getInstance().destroy()
        super.onDestroy()
    }
}