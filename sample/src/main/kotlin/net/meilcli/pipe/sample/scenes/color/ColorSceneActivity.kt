package net.meilcli.pipe.sample.scenes.color

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_scene_color.*
import net.meilcli.pipe.MutableListPipe
import net.meilcli.pipe.adapter.PipeAdapter
import net.meilcli.pipe.operators.combine
import net.meilcli.pipe.operators.linearStack
import net.meilcli.pipe.sample.R
import net.meilcli.pipe.sample.items.ColorItem

class ColorSceneActivity : AppCompatActivity() {

    private enum class State {
        Stack, Combine
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scene_color)

        val pipeAdapter = PipeAdapter(ColorSceneViewHolderCreator())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pipeAdapter

        var redPipe = MutableListPipe<ColorItem>()
        var redNumber = 0
        red.setOnClickListener {
            redPipe.add(0, ColorItem(Color.RED, redNumber))
            redNumber += 1
        }
        var greenPipe = MutableListPipe<ColorItem>()
        var greenNumber = 0
        green.setOnClickListener {
            greenPipe.add(0, ColorItem(Color.GREEN, greenNumber))
            greenNumber += 1
        }
        var bluePipe = MutableListPipe<ColorItem>()
        var blueNumber = 0
        blue.setOnClickListener {
            bluePipe.add(0, ColorItem(Color.BLUE, blueNumber))
            blueNumber += 1
        }

        var state = State.Stack
        pipeAdapter.set(
            linearStack(redPipe, greenPipe, bluePipe)
        )

        stackState.setOnClickListener {
            if (state != State.Stack) {
                redPipe = MutableListPipe()
                redNumber = 0
                greenPipe = MutableListPipe()
                greenNumber = 0
                bluePipe = MutableListPipe()
                blueNumber = 0
                pipeAdapter.set(
                    linearStack(redPipe, greenPipe, bluePipe)
                )
                state = State.Stack
            }
        }

        combineState.setOnClickListener {
            if (state != State.Combine) {
                redPipe = MutableListPipe()
                redNumber = 0
                greenPipe = MutableListPipe()
                greenNumber = 0
                bluePipe = MutableListPipe()
                blueNumber = 0
                pipeAdapter.set(
                    combine(redPipe, greenPipe, bluePipe)
                )
                state = State.Stack
            }
        }
    }
}