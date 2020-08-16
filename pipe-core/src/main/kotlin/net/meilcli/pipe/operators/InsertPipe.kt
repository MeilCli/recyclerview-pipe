package net.meilcli.pipe.operators

import net.meilcli.pipe.*
import net.meilcli.pipe.internal.INotifyPipeChangedContainer
import kotlin.math.min

class InsertPipe<T : IPipeItem>(
    private val source: IPipe<T>,
    private val insert: IPipe<T>,
    // plan: adapt multiple count when prepared IPipeItemGroup
    private val insertCondition: (Int) -> Boolean,
    private val insertStrategy: InsertStrategy
) : IPipe<T>, INotifyPipeChangedContainer {

    private inner class NotifyPipeChanged : INotifyPipeChanged {

        override fun eventRaised(event: PipeEvent) = when (event.sender) {
            source -> eventRaisedBySource(event)
            insert -> eventRaisedByInsert(event)
            insertedPipe -> eventRaisedByInsertedPipe(event)
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

    private val insertedPipe = MutableListPipe<WrapItem<T>>()

    override val eventNotifiers = mutableListOf<INotifyPipeChanged>()

    override val size: Int
        get() = insertedPipe.size

    init {
        val notifyPipeChanged = NotifyPipeChanged()
        source.registerNotifyPipeChanged(notifyPipeChanged)
        insert.registerNotifyPipeChanged(notifyPipeChanged)
        insertedPipe.registerNotifyPipeChanged(notifyPipeChanged)
        insertedPipe.addAll(source.toList().map { WrapItem(source, it) })
        constructInsert(0)
    }

    // if 0 < startIndex, must be source item index
    private fun constructInsert(startIndex: Int) {
        insertedPipe.removeAll { i, wrapItem -> wrapItem.pipe == insert && startIndex <= i }
        var insertedCount = 0
        for (i in 0 until startIndex) {
            val item = insertedPipe[i]
            if (item.pipe == insert) {
                insertedCount += 1
            }
        }

        var index = startIndex
        var sourceIndex = index - insertedCount
        var added = false
        while (insertedCount < insert.size && sourceIndex < source.size) {
            val condition = insertCondition(sourceIndex)
            if (condition.not() || added) {
                index += 1
                sourceIndex += 1
                added = false
                continue
            }
            if (insertStrategy == InsertStrategy.Start) {
                if (insertedPipe[index].pipe == insert || (1 <= index && insertedPipe[index - 1].pipe == insert)) {
                    index += 1
                    sourceIndex += 1
                    continue
                }
                insertedPipe.add(index, WrapItem(insert, insert[insertedCount]))
            }
            if (insertStrategy == InsertStrategy.End) {
                insertedPipe.add(index + 1, WrapItem(insert, insert[insertedCount]))
            }
            index += 1
            sourceIndex += 1
            insertedCount += 1
            added = true
        }
    }

    override fun get(index: Int): T {
        return insertedPipe[index].item
    }

    override fun indexOf(element: T): Int {
        for (i in indices()) {
            if (insertedPipe[i].item == element) {
                return i
            }
        }
        return -1
    }

    override fun toList(): List<T> {
        return insertedPipe.toList().map { it.item }
    }

    private fun eventRaisedBySource(pipeEvent: PipeEvent) {
        when (pipeEvent) {
            is PipeEvent.Added -> addEventRaisedBySource(pipeEvent.index, 1)
            is PipeEvent.RangeAdded -> addEventRaisedBySource(pipeEvent.startIndex, pipeEvent.count)
            is PipeEvent.Changed -> changeEventRaisedBySourceOrInsert(source, pipeEvent.index, 1)
            is PipeEvent.RangeChanged -> changeEventRaisedBySourceOrInsert(source, pipeEvent.startIndex, pipeEvent.count)
            is PipeEvent.Removed -> removeEventRaisedBySource(pipeEvent.index)
            is PipeEvent.RangeRemoved -> removeEventRaisedBySource(pipeEvent.startIndex)
            is PipeEvent.Moved -> {
                val movedItem = source[pipeEvent.toIndex]
                val movedFromIndex = indexOf(movedItem)
                if (movedFromIndex < 0) {
                    return
                }
                var moveToIndex = if (pipeEvent.toIndex == 0) 0 else indexOf(source[pipeEvent.toIndex - 1]) + 1
                if (moveToIndex < 0) {
                    return
                }
                if (insertedPipe[moveToIndex].pipe == insert) {
                    moveToIndex += 1
                }
                insertedPipe.move(movedFromIndex, moveToIndex)
                constructInsert(min(moveToIndex, movedFromIndex))
            }
            is PipeEvent.Reset -> insertedPipe.clear()
        }
    }

    private fun addEventRaisedBySource(startIndex: Int, count: Int) {
        val addedItems = source[startIndex until startIndex + count]
        var addIndex = if (startIndex == 0) 0 else indexOf(source[startIndex - 1]) + 1
        if (insertedPipe.isNotEmpty() && addIndex in insertedPipe.indices() && insertedPipe[addIndex].pipe == insert) {
            addIndex += 1
        }
        insertedPipe.addAll(addIndex, addedItems.map { WrapItem(source, it) })
        constructInsert(addIndex)
    }

    private fun changeEventRaisedBySourceOrInsert(pipe: IPipe<T>, startIndex: Int, count: Int) {
        val changedItems = pipe[startIndex until startIndex + count]
        var skipCount = 0
        var currentIndex = 0
        insertedPipe.changeAll { _, wrapItem ->
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

    private fun removeEventRaisedBySource(startIndex: Int) {
        if (source.isEmpty()) {
            insertedPipe.clear()
            return
        }
        if (source.size == 1) {
            val notRemoveItem = source[0]
            insertedPipe.removeAll { _, wrapItem -> wrapItem.pipe == source && wrapItem.item != notRemoveItem }
            constructInsert(0)
            return
        }
        if (startIndex == 0) {
            val startItem = source[0]
            val startItemIndex = indexOf(startItem)
            insertedPipe.removeAll { i, wrapItem -> wrapItem.pipe == source && i < startItemIndex }
            constructInsert(0)
            return
        }
        if (source.lastIndex < startIndex) {
            val endItem = source[source.lastIndex]
            val endItemIndex = indexOf(endItem)
            insertedPipe.removeAll { i, wrapItem -> wrapItem.pipe == source && endItemIndex < i }
            constructInsert(endItemIndex)
            return
        }
        val startItem = source[startIndex - 1]
        val startItemIndex = indexOf(startItem)
        val endItem = source[startIndex]
        val endItemIndex = indexOf(endItem)
        insertedPipe.removeAll { i, wrapItem -> wrapItem.pipe == source && startItemIndex < i && i < endItemIndex }
        constructInsert(startIndex)
    }

    private fun eventRaisedByInsert(pipeEvent: PipeEvent) {
        when (pipeEvent) {
            is PipeEvent.Added -> addEventRaisedByInsert(pipeEvent.index)
            is PipeEvent.RangeAdded -> addEventRaisedByInsert(pipeEvent.startIndex)
            is PipeEvent.Changed -> changeEventRaisedBySourceOrInsert(insert, pipeEvent.index, 1)
            is PipeEvent.RangeChanged -> changeEventRaisedBySourceOrInsert(insert, pipeEvent.startIndex, pipeEvent.count)
            is PipeEvent.Removed -> removeEventRaisedByInsert(pipeEvent.index)
            is PipeEvent.RangeRemoved -> removeEventRaisedByInsert(pipeEvent.startIndex)
            is PipeEvent.Moved -> {
                if (pipeEvent.toIndex == 0 || pipeEvent.fromIndex == 0) {
                    constructInsert(0)
                    return
                }
                val previousIndex = indexOf(insert[min(pipeEvent.toIndex - 1, pipeEvent.fromIndex - 1)])
                if (previousIndex < 0) {
                    return
                }
                constructInsert(previousIndex + 1)
            }
            is PipeEvent.Reset -> constructInsert(0)
        }
    }

    private fun addEventRaisedByInsert(startIndex: Int) {
        if (startIndex == 0) {
            constructInsert(0)
            return
        }
        val previousIndex = indexOf(insert[startIndex - 1])
        if (previousIndex < 0) {
            return
        }
        constructInsert(previousIndex + 1)
    }

    private fun removeEventRaisedByInsert(startIndex: Int) {
        if (insert.isEmpty() || startIndex == 0) {
            constructInsert(0)
            return
        }
        val previousIndex = indexOf(insert[startIndex - 1])
        if (previousIndex < 0) {
            return
        }
        constructInsert(previousIndex + 1)
    }

    private fun eventRaisedByInsertedPipe(pipeEvent: PipeEvent) {
        raiseEvent(pipeEvent.clone(this))
    }
}