package net.meilcli.pipe.sample.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.holder_text.view.*
import net.meilcli.pipe.adapter.IPipeAdapterArgument
import net.meilcli.pipe.adapter.PipeViewHolder
import net.meilcli.pipe.sample.R
import net.meilcli.pipe.sample.items.TextItem

class TextViewHolder(parent: ViewGroup) : PipeViewHolder<TextItem>(LayoutInflater.from(parent.context).inflate(R.layout.holder_text, parent, false)) {

    private val view: TextView = itemView.view

    override fun bind(item: TextItem, argument: IPipeAdapterArgument, payloads: List<Any>) {
        super.bind(item, argument, payloads)
        view.text = item.text
    }
}