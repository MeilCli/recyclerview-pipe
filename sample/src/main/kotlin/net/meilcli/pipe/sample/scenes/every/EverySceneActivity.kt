package net.meilcli.pipe.sample.scenes.every

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_scene_every.*
import net.meilcli.pipe.MutableListPipe
import net.meilcli.pipe.adapter.PipeAdapter
import net.meilcli.pipe.operators.insertEveryAt
import net.meilcli.pipe.sample.R
import net.meilcli.pipe.sample.items.ColorItem

class EverySceneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scene_every)

        val pipeAdapter = PipeAdapter(EverySceneViewHolderCreator())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pipeAdapter

        val redPipe = MutableListPipe<ColorItem>()
        var redNumber = 0
        red.setOnClickListener {
            redPipe.add(ColorItem(Color.RED, redNumber))
            redNumber += 1
        }
        val greenPipe = MutableListPipe<ColorItem>()
        var greenNumber = 0
        green.setOnClickListener {
            greenPipe.add(ColorItem(Color.GREEN, greenNumber))
            greenNumber += 1
        }

        pipeAdapter.set(redPipe.insertEveryAt(greenPipe, 3))
    }
}