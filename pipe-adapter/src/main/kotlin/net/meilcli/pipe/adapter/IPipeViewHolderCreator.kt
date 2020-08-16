package net.meilcli.pipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.meilcli.pipe.IPipeItem

interface IPipeViewHolderCreator<out TViewHolder, in TItem : IPipeItem> where TViewHolder : IPipeViewHolder<TItem>, TViewHolder : RecyclerView.ViewHolder {

    fun select(index: Int, item: TItem): Int

    fun create(parent: ViewGroup, viewType: Int): TViewHolder
}