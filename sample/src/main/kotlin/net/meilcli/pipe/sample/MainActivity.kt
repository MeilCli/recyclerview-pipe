package net.meilcli.pipe.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.meilcli.pipe.sample.scenes.color.ColorSceneActivity
import net.meilcli.pipe.sample.scenes.every.EverySceneActivity
import net.meilcli.pipe.sample.scenes.insert.InsertSceneActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorScene.setOnClickListener {
            startActivity(Intent(this, ColorSceneActivity::class.java))
        }
        insertScene.setOnClickListener {
            startActivity(Intent(this, InsertSceneActivity::class.java))
        }
        everyScene.setOnClickListener {
            startActivity(Intent(this, EverySceneActivity::class.java))
        }
    }
}