package net.meilcli.pipe.sample.scenes.every

import android.view.ViewGroup
import net.meilcli.pipe.adapter.IPipeViewHolderCreator
import net.meilcli.pipe.sample.holders.ColorViewHolder
import net.meilcli.pipe.sample.items.ColorItem

class EverySceneViewHolderCreator : IPipeViewHolderCreator<ColorViewHolder, ColorItem> {

    override fun select(index: Int, item: ColorItem): Int {
        return 0
    }

    override fun create(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(parent)
    }
}