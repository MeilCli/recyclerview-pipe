package net.meilcli.pipe.operators

import net.meilcli.pipe.*
import net.meilcli.pipe.internal.INotifyPipeChangedContainer

class CombinePipe<T : IPipeItem>(
    private val source1: IPipe<T>,
    private val source2: IPipe<T>,
    private val insertStrategy: InsertStrategy
) : IPipe<T>, INotifyPipeChangedContainer {

    private inner class NotifyPipeChanged : INotifyPipeChanged {

        override fun eventRaised(event: PipeEvent) = when (event.sender) {
            source1 -> eventRaisedBySource(source1, event)
            source2 -> eventRaisedBySource(source2, event)
            combinedPipe -> eventRaisedByCombinedPipe(event)
            else -> Unit
        }
    }

    private class WrapItem<T : IPipeItem>(val pipe: IPipe<T>, val item: T) : IPipeItem {

        override fun areItemsTheSame(other: IPipeItem): Boolean {
            if (other !is WrapItem<*>) {
                return false
            }
            if (pipe != other.pipe) {
                return false
            }
            return item.areItemsTheSame(other.item)
        }

        override fun areContentsTheSame(other: IPipeItem): Boolean {
            if (other !is WrapItem<*>) {
                return false
            }
            if (pipe != other.pipe) {
                return false
            }
            return item.areContentsTheSame(other.item)
        }
    }

    private val combinedPipe = MutableListPipe<WrapItem<T>>()

    override val eventNotifiers = mutableListOf<INotifyPipeChanged>()

    override val size: Int
        get() = combinedPipe.size

    init {
        val notifyPipeChanged = NotifyPipeChanged()
        source1.registerNotifyPipeChanged(notifyPipeChanged)
        source2.registerNotifyPipeChanged(notifyPipeChanged)
        combinedPipe.registerNotifyPipeChanged(notifyPipeChanged)
        combinedPipe.addAll(source1.toList().map { WrapItem(source1, it) } + source2.toList().map { WrapItem(source2, it) })
    }

    override fun get(index: Int): T {
        return combinedPipe[index].item
    }

    override fun indexOf(element: IPipeItem): Int {
        for (i in indices()) {
            if (combinedPipe[i].item == element) {
                return i
            }
        }
        return -1
    }

    override fun toList(): List<T> {
        return combinedPipe.toList().map { it.item }
    }

    private fun eventRaisedBySource(pipe: IPipe<T>, pipeEvent: PipeEvent) {
        when (pipeEvent) {
            is PipeEvent.Added -> addEventRaisedBySource(pipe, pipeEvent.index, 1)
            is PipeEvent.RangeAdded -> addEventRaisedBySource(pipe, pipeEvent.startIndex, pipeEvent.count)
            is PipeEvent.Changed -> changeEventRaisedBySource(pipe, pipeEvent.index, 1)
            is PipeEvent.RangeChanged -> changeEventRaisedBySource(pipe, pipeEvent.startIndex, pipeEvent.count)
            is PipeEvent.Removed -> removeEventRaisedBySource(pipe, pipeEvent.index)
            is PipeEvent.RangeRemoved -> removeEventRaisedBySource(pipe, pipeEvent.startIndex)
            is PipeEvent.Moved -> {
                val movedItem = pipe[pipeEvent.toIndex]
                val movedFromIndex = indexOf(movedItem)
                if (movedFromIndex < 0) {
                    return
                }
                val moveToIndex = insertIndexFromPipeToCombinedPipe(pipe, pipeEvent.toIndex)
                if (moveToIndex < 0) {
                    return
                }
                combinedPipe.move(movedFromIndex, moveToIndex)
            }
            is PipeEvent.Reset -> {
                combinedPipe.removeAll { _, wrapItem -> wrapItem.pipe == pipe }
            }
        }
    }

    private fun addEventRaisedBySource(pipe: IPipe<T>, startIndex: Int, count: Int) {
        val addedItems = pipe[startIndex until startIndex + count]
        val addIndex = insertIndexFromPipeToCombinedPipe(pipe, startIndex)
        combinedPipe.addAll(addIndex, addedItems.map { WrapItem(pipe, it) })
    }

    private fun changeEventRaisedBySource(pipe: IPipe<T>, startIndex: Int, count: Int) {
        val changedItems = pipe[startIndex until startIndex + count]
        var skipCount = 0
        var currentIndex = 0
        combinedPipe.changeAll { _, wrapItem ->
            when {
                wrapItem.pipe != pipe -> {
                    wrapItem
                }
                skipCount < startIndex -> {
                    skipCount += 1
                    wrapItem
                }
                changedItems.lastIndex < currentIndex -> wrapItem
                else -> {
                    val changedItem = changedItems[currentIndex]
                    currentIndex += 1
                    WrapItem(pipe, changedItem)
                }
            }
        }
    }

    private fun removeEventRaisedBySource(pipe: IPipe<T>, startIndex: Int) {
        if (pipe.isEmpty()) {
            combinedPipe.removeAll { _, wrapItem -> wrapItem.pipe == pipe }
            return
        }
        if (pipe.size == 1) {
            val notRemoveItem = pipe[0]
            combinedPipe.removeAll { _, wrapItem -> wrapItem.pipe == pipe && wrapItem.item != notRemoveItem }
            return
        }
        if (startIndex == 0) {
            val startItem = pipe[0]
            val startItemIndex = indexOf(startItem)
            combinedPipe.removeAll { i, wrapItem -> wrapItem.pipe == pipe && i < startItemIndex }
            return
        }
        if (pipe.lastIndex < startIndex) {
            val endItem = pipe[pipe.lastIndex]
            val endItemIndex = indexOf(endItem)
            combinedPipe.removeAll { i, wrapItem -> wrapItem.pipe == pipe && endItemIndex < i }
            return
        }
        val startItem = pipe[startIndex - 1]
        val startItemIndex = indexOf(startItem)
        val endItem = pipe[startIndex]
        val endItemIndex = indexOf(endItem)
        combinedPipe.removeAll { i, wrapItem -> wrapItem.pipe == pipe && startItemIndex < i && i < endItemIndex }
    }

    // attention: pipe is new index table
    private fun insertIndexFromPipeToCombinedPipe(pipe: IPipe<T>, index: Int): Int {
        if (pipe.size == 1) {
            return when (insertStrategy) {
                InsertStrategy.Start -> 0
                InsertStrategy.End -> size
            }
        }
        if (index == 0) {
            return when (insertStrategy) {
                InsertStrategy.Start -> 0
                InsertStrategy.End -> firstIndex(pipe)
            }
        }
        if (index == pipe.lastIndex) {
            return when (insertStrategy) {
                InsertStrategy.Start -> lastIndex(pipe) + 1
                InsertStrategy.End -> size
            }
        }
        val startItem = pipe[index - 1]
        val startIndex = indexOf(startItem)
        val endItem = pipe[index + 1]
        val endIndex = indexOf(endItem)
        return when (insertStrategy) {
            InsertStrategy.Start -> startIndex + 1
            InsertStrategy.End -> endIndex
        }
    }

    private fun firstIndex(pipe: IPipe<T>): Int {
        for (i in indices()) {
            if (combinedPipe[i].pipe == pipe) {
                return i
            }
        }
        return -1
    }

    private fun lastIndex(pipe: IPipe<T>): Int {
        for (i in indices().reversed()) {
            if (combinedPipe[i].pipe == pipe) {
                return i
            }
        }
        return -1
    }

    private fun eventRaisedByCombinedPipe(pipeEvent: PipeEvent) {
        raiseEvent(pipeEvent.clone(this))
    }
}