package net.meilcli.pipe.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.meilcli.pipe.sample.scenes.color.ColorSceneActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorScene.setOnClickListener {
            startActivity(Intent(this, ColorSceneActivity::class.java))
        }
    }
}