package net.meilcli.pipe.operators

import net.meilcli.pipe.INotifyPipeChanged
import net.meilcli.pipe.IPipe
import net.meilcli.pipe.IPipeItem
import net.meilcli.pipe.PipeEvent
import net.meilcli.pipe.extensions.include
import net.meilcli.pipe.extensions.intersect
import net.meilcli.pipe.extensions.size
import net.meilcli.pipe.internal.INotifyPipeChangedContainer
import kotlin.math.max
import kotlin.math.min

class SkipPipe<T : IPipeItem>(
    private val source: IPipe<T>,
    private val count: Int
) : IPipe<T>, INotifyPipeChangedContainer {

    private inner class NotifyPipeChanged : INotifyPipeChanged {

        override fun eventRaised(event: PipeEvent) = when (event.sender) {
            source -> eventRaisedBySource(event)
            else -> Unit
        }
    }

    override val eventNotifiers = mutableListOf<INotifyPipeChanged>()

    override val size: Int
        get() = max(source.size - count, 0)

    init {
        source.registerNotifyPipeChanged(NotifyPipeChanged())
        if (size == 1) {
            raiseEvent(PipeEvent.Added(this, 0))
        } else if (2 <= size) {
            raiseEvent(PipeEvent.RangeAdded(this, 0, size))
        }
    }

    override fun get(index: Int): T {
        if (index !in indices()) {
            throw IndexOutOfBoundsException("index: $index out of bound 0..$lastIndex")
        }
        return source[index + count]
    }

    override fun indexOf(element: T): Int {
        val index = source.indexOf(element)
        return min(index - count, -1)
    }

    override fun toList(): List<T> {
        return source.toList().drop(count)
    }

    private fun eventRaisedBySource(event: PipeEvent) {
        when (event) {
            is PipeEvent.Added -> addEventRaised(event.index, 1)
            is PipeEvent.RangeAdded -> addEventRaised(event.startIndex, event.count)
            is PipeEvent.Changed -> changeEventRaised(event.index, 1)
            is PipeEvent.RangeChanged -> changeEventRaised(event.startIndex, event.count)
            is PipeEvent.Removed -> removeEventRaised(event.index, 1)
            is PipeEvent.RangeRemoved -> removeEventRaised(event.startIndex, event.count)
            is PipeEvent.Moved -> {
                if (isEmpty()) {
                    return
                }
                if ((this.count..source.lastIndex).include(event.fromIndex..event.toIndex)) {
                    raiseEvent(PipeEvent.Moved(this, event.fromIndex - this.count, event.toIndex - this.count))
                } else if (event.fromIndex in this.count..source.lastIndex) {
                    raiseEvent(PipeEvent.Added(this, 0))
                    raiseEvent(PipeEvent.Removed(this, event.fromIndex - this.count))
                } else if (event.toIndex in this.count..source.lastIndex) {
                    raiseEvent(PipeEvent.Removed(this, 0))
                    raiseEvent(PipeEvent.Added(this, event.toIndex - this.count))
                }
            }
            is PipeEvent.Reset -> raiseEvent(PipeEvent.Reset(this))
        }
    }

    private fun addEventRaised(startIndex: Int, count: Int) {
        if (isEmpty()) {
            return
        }
        val addIndex = max(startIndex - this.count, 0)
        if (count == 1) {
            raiseEvent(PipeEvent.Added(this, addIndex))
        } else if (2 <= count) {
            raiseEvent(PipeEvent.RangeAdded(this, addIndex, count))
        }
    }

    private fun changeEventRaised(startIndex: Int, count: Int) {
        if (isEmpty()) {
            return
        }
        val changeRange = (this.count..(this.count + size)).intersect(startIndex until (startIndex + count))
        val changeCount = changeRange.size
        val changeIndex = changeRange.first - this.count
        if (changeCount == 1) {
            raiseEvent(PipeEvent.Changed(this, changeIndex))
        } else if (2 <= changeCount) {
            raiseEvent(PipeEvent.RangeChanged(this, changeIndex, changeCount))
        }
    }

    private fun removeEventRaised(startIndex: Int, count: Int) {
        val removeIndex = max(startIndex - this.count, 0)
        if (count == 1) {
            raiseEvent(PipeEvent.Removed(this, removeIndex))
        } else if (2 <= count) {
            raiseEvent(PipeEvent.RangeRemoved(this, removeIndex, count))
        }
    }
}