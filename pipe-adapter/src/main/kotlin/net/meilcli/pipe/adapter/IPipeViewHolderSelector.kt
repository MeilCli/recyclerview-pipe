package net.meilcli.pipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.meilcli.pipe.IPipeItem

interface IPipeViewHolderSelector<out TViewHolder, in TItem : IPipeItem> where TViewHolder : IPipeViewHolder<TItem>, TViewHolder : RecyclerView.ViewHolder {

    val viewType: Int

    fun match(index: Int, item: IPipeItem): Boolean

    fun create(parent: ViewGroup): TViewHolder
}