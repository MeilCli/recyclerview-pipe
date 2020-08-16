package net.meilcli.pipe.sample.scenes.multi

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_scene_multi.*
import net.meilcli.pipe.InsertStrategy
import net.meilcli.pipe.MutableListPipe
import net.meilcli.pipe.adapter.PipeAdapter
import net.meilcli.pipe.adapter.PipeViewHolderCreator
import net.meilcli.pipe.operators.combine
import net.meilcli.pipe.sample.R
import net.meilcli.pipe.sample.items.ColorItem
import net.meilcli.pipe.sample.items.TextItem

class MultiSceneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scene_multi)

        val pipeAdapter = PipeAdapter(PipeViewHolderCreator(MultiSceneColorViewHolderSelector(), MultiSceneTextViewHolderSelector()))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pipeAdapter

        val colorPipe = MutableListPipe<ColorItem>()
        var colorNumber = 0
        color.setOnClickListener {
            colorPipe.add(ColorItem(Color.BLUE, colorNumber))
            colorNumber += 1
        }
        val textPipe = MutableListPipe<TextItem>()
        var textNumber = 0
        text.setOnClickListener {
            textPipe.add(TextItem("Number: $textNumber"))
            textNumber += 1
        }

        pipeAdapter.set(combine(colorPipe, textPipe, InsertStrategy.End))
    }
}