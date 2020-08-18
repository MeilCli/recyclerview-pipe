package net.meilcli.pipe.sample.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.holder_color.view.*
import net.meilcli.pipe.adapter.IPipeAdapterArgument
import net.meilcli.pipe.adapter.PipeViewHolder
import net.meilcli.pipe.sample.R
import net.meilcli.pipe.sample.items.ColorItem

class ColorViewHolder(parent: ViewGroup) : PipeViewHolder<ColorItem>(LayoutInflater.from(parent.context).inflate(R.layout.holder_color, parent, false)) {

    private val view: TextView = itemView.view

    override fun bind(item: ColorItem, argument: IPipeAdapterArgument, payloads: List<Any>) {
        super.bind(item, argument, payloads)
        view.setBackgroundColor(item.color)
        when (val number = item.number) {
            null -> view.text = ""
            else -> view.text = number.toString()
        }
    }
}