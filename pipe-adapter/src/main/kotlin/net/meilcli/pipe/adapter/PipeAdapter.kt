package net.meilcli.pipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.meilcli.pipe.*

open class PipeAdapter<TViewHolder, TItem : IPipeItem>(
    private val pipeViewHolderCreator: IPipeViewHolderCreator<TViewHolder, TItem>,
    private val pipeAdapterArgumentProvider: IPipeAdapterArgumentProvider<TItem> = EmptyPipeAdapterArgumentProvider()
) : RecyclerView.Adapter<TViewHolder>(), IPipeAdapter<TItem> where  TViewHolder : IPipeViewHolder<TItem>, TViewHolder : RecyclerView.ViewHolder {

    private inner class NotifyPipeChanged : INotifyPipeChanged {

        override fun eventRaised(event: PipeEvent) = when (event.sender) {
            source -> eventRaisedBySource(event)
            else -> Unit
        }
    }

    private var source: IPipe<TItem> = EmptyPipe()
    private val notifyPipeChanged = NotifyPipeChanged()
    private val eventNotifiers = mutableListOf<INotifyPipeChanged>()

    override val size: Int
        get() = source.size

    override fun get(index: Int): TItem {
        return source[index]
    }

    override fun indexOf(element: IPipeItem): Int {
        return source.indexOf(element)
    }

    override fun set(pipe: IPipe<TItem>) {
        source.unregisterNotifyPipeChanged(notifyPipeChanged)
        raiseEvent(PipeEvent.Reset(this))
        source = pipe
        notifyDataSetChanged()
        source.registerNotifyPipeChanged(notifyPipeChanged)
        if (source.isNotEmpty()) {
            if (source.size == 1) {
                raiseEvent(PipeEvent.Added(this, 0))
            } else {
                raiseEvent(PipeEvent.RangeAdded(this, 0, source.size))
            }
        }
    }

    override fun toList(): List<TItem> {
        return source.toList()
    }

    override fun getItemViewType(position: Int): Int {
        return pipeViewHolderCreator.select(position, source[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TViewHolder {
        return pipeViewHolderCreator.create(parent, viewType).also {
            it.initialize()
        }
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onBindViewHolder(holder: TViewHolder, position: Int) {
        val item = source[position]
        val argument = pipeAdapterArgumentProvider.provide(position, item)
        holder.bind(item, argument)
    }

    override fun onViewRecycled(holder: TViewHolder) {
        holder.unbind()
        super.onViewRecycled(holder)
    }

    override fun registerNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged) {
        if (eventNotifiers.contains(notifyPipeChanged)) {
            return
        }
        eventNotifiers.add(notifyPipeChanged)
    }

    override fun unregisterNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged) {
        eventNotifiers.remove(notifyPipeChanged)
    }

    private fun raiseEvent(event: PipeEvent) {
        for (eventNotifier in eventNotifiers) {
            eventNotifier.eventRaised(event)
        }
    }

    private fun eventRaisedBySource(event: PipeEvent) {
        raiseEvent(event.clone(this))
        when (event) {
            is PipeEvent.Added -> notifyItemInserted(event.index)
            is PipeEvent.RangeAdded -> notifyItemRangeInserted(event.startIndex, event.count)
            is PipeEvent.Changed -> notifyItemChanged(event.index)
            is PipeEvent.RangeChanged -> notifyItemRangeChanged(event.startIndex, event.count)
            is PipeEvent.Removed -> notifyItemRemoved(event.index)
            is PipeEvent.RangeRemoved -> notifyItemRangeRemoved(event.startIndex, event.count)
            is PipeEvent.Moved -> notifyItemMoved(event.fromIndex, event.toIndex)
            is PipeEvent.Reset -> notifyDataSetChanged()
        }
    }
}