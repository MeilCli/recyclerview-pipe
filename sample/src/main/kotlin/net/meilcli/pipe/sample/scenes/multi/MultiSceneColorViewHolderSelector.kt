package net.meilcli.pipe.sample.scenes.multi

import android.view.ViewGroup
import net.meilcli.pipe.IPipeItem
import net.meilcli.pipe.adapter.IPipeViewHolderSelector
import net.meilcli.pipe.sample.R
import net.meilcli.pipe.sample.holders.ColorViewHolder
import net.meilcli.pipe.sample.items.ColorItem

class MultiSceneColorViewHolderSelector : IPipeViewHolderSelector<ColorViewHolder, ColorItem> {

    override val viewType: Int = R.layout.holder_color

    override fun match(index: Int, item: IPipeItem): Boolean {
        return item is ColorItem
    }

    override fun create(parent: ViewGroup): ColorViewHolder {
        return ColorViewHolder(parent)
    }
}