package net.meilcli.pipe.sample.scenes.multi

import android.view.ViewGroup
import net.meilcli.pipe.IPipeItem
import net.meilcli.pipe.adapter.IPipeViewHolderSelector
import net.meilcli.pipe.sample.R
import net.meilcli.pipe.sample.holders.TextViewHolder
import net.meilcli.pipe.sample.items.TextItem

class MultiSceneTextViewHolderSelector : IPipeViewHolderSelector<TextViewHolder, TextItem> {

    override val viewType: Int = R.layout.holder_text

    override fun match(index: Int, item: IPipeItem): Boolean {
        return item is TextItem
    }

    override fun create(parent: ViewGroup): TextViewHolder {
        return TextViewHolder(parent)
    }
}