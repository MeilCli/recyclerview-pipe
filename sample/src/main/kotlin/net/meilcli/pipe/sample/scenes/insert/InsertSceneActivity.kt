package net.meilcli.pipe.sample.scenes.insert

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_scene_insert.*
import net.meilcli.pipe.MutableListPipe
import net.meilcli.pipe.adapter.PipeAdapter
import net.meilcli.pipe.operators.linearStack
import net.meilcli.pipe.operators.skip
import net.meilcli.pipe.operators.take
import net.meilcli.pipe.sample.R
import net.meilcli.pipe.sample.items.ColorItem

class InsertSceneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scene_insert)

        val pipeAdapter = PipeAdapter(InsertSceneViewHolderCreator())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pipeAdapter

        val redPipe = MutableListPipe<ColorItem>()
        var redNumber = 0
        red.setOnClickListener {
            redPipe.add(0, ColorItem(Color.RED, redNumber))
            redNumber += 1
        }
        val greenPipe = MutableListPipe<ColorItem>()
        var greenNumber = 0
        green.setOnClickListener {
            greenPipe.add(0, ColorItem(Color.GREEN, greenNumber))
            greenNumber += 1
        }
        val bluePipe = MutableListPipe<ColorItem>()
        var blueNumber = 0
        blue.setOnClickListener {
            bluePipe.add(0, ColorItem(Color.BLUE, blueNumber))
            blueNumber += 1
        }

        pipeAdapter.set(
            linearStack(
                greenPipe.take(3),
                redPipe.take(2),
                bluePipe.skip(3),
                greenPipe.skip(3)
            )
        )
    }
}