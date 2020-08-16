package net.meilcli.pipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.meilcli.pipe.IPipeItem

class PipeViewHolderCreator<out TViewHolder>(
    private vararg val selectors: IPipeViewHolderSelector<TViewHolder, *>
) : IPipeViewHolderCreator<PipeViewHolder<IPipeItem>, IPipeItem> where TViewHolder : IPipeViewHolder<*>, TViewHolder : RecyclerView.ViewHolder {

    override fun select(index: Int, item: IPipeItem): Int {
        for (selector in selectors) {
            if (selector.match(index, item)) {
                return selector.viewType
            }
        }

        throw UnsupportedOperationException("not found match view holder selector: $index, $item")
    }

    override fun create(parent: ViewGroup, viewType: Int): PipeViewHolder<IPipeItem> {
        for (selector in selectors) {
            if (selector.viewType == viewType) {
                @Suppress("UNCHECKED_CAST")
                return selector.create(parent) as PipeViewHolder<IPipeItem>
            }
        }

        throw UnsupportedOperationException("cannot create view holder by this view type: $viewType")
    }
}