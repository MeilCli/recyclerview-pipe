package net.meilcli.pipe.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.meilcli.pipe.IPipeItem

open class PipeViewHolder<T : IPipeItem>(itemView: View) : RecyclerView.ViewHolder(itemView), IPipeViewHolder<T>