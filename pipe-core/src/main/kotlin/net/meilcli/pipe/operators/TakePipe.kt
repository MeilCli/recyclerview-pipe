package net.meilcli.pipe.operators

import net.meilcli.pipe.INotifyPipeChanged
import net.meilcli.pipe.IPipe
import net.meilcli.pipe.IPipeItem
import net.meilcli.pipe.PipeEvent
import net.meilcli.pipe.internal.INotifyPipeChangedContainer
import kotlin.math.min

class TakePipe<T : IPipeItem>(
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

    override var size: Int = 0
        private set

    init {
        source.registerNotifyPipeChanged(NotifyPipeChanged())
        size = if (source.size <= count) source.size else count
        if (isNotEmpty()) {
            if (size == 1) {
                raiseEvent(PipeEvent.Added(this, 0))
            } else {
                raiseEvent(PipeEvent.RangeAdded(this, 0, size))
            }
        }
    }

    override fun get(index: Int): T {
        if (index !in 0 until size) {
            throw IndexOutOfBoundsException("$index is out of bounds 0..${lastIndex}")
        }
        return source[index]
    }

    override fun indexOf(element: T): Int {
        val index = source.indexOf(element)
        if (index !in 0 until size) {
            return -1
        }
        return index
    }

    override fun toList(): List<T> {
        return source.toList().take(count)
    }

    private fun eventRaisedBySource(event: PipeEvent) {
        when (event) {
            is PipeEvent.Added -> addEventRaised(event.index, 1)
            is PipeEvent.RangeAdded -> addEventRaised(event.startIndex, event.count)
            is PipeEvent.Changed -> {
                if (event.index in 0..lastIndex) {
                    raiseEvent(event.clone(this))
                }
            }
            is PipeEvent.RangeChanged -> {
                if (event.startIndex in 0..lastIndex) {
                    if (event.startIndex == lastIndex) {
                        raiseEvent(PipeEvent.Changed(this, lastIndex))
                    } else {
                        raiseEvent(PipeEvent.RangeChanged(this, event.startIndex, min(event.count, size - event.startIndex + 1)))
                    }
                }
            }
            is PipeEvent.Removed -> removeEventRaised(event.index, 1)
            is PipeEvent.RangeRemoved -> removeEventRaised(event.startIndex, event.count)
            is PipeEvent.Moved -> {
                if (event.fromIndex in 0..lastIndex && event.toIndex in 0..lastIndex) {
                    raiseEvent(event.clone(this))
                } else if (event.fromIndex in 0..lastIndex) {
                    raiseEvent(PipeEvent.Removed(this, event.fromIndex))
                    raiseEvent(PipeEvent.Added(this, lastIndex))
                } else if (event.toIndex in 0..lastIndex) {
                    raiseEvent(PipeEvent.Added(this, event.toIndex))
                    raiseEvent(PipeEvent.Removed(this, count))
                }
            }
            is PipeEvent.Reset -> raiseEvent(PipeEvent.Reset(this))
        }
    }

    private fun addEventRaised(startIndex: Int, count: Int) {
        if (startIndex !in 0..min(source.size, this.count)) {
            return
        }
        val addCount = min(count, this.count - startIndex)
        val removeCount = if (this.count < size + addCount) size + addCount - this.count else 0
        size -= removeCount
        if (removeCount == 1) {
            raiseEvent(PipeEvent.Removed(this, lastIndex + 1))
        } else if (2 <= removeCount) {
            raiseEvent(PipeEvent.RangeRemoved(this, lastIndex + 1, removeCount))
        }
        size += addCount
        if (addCount == 1) {
            raiseEvent(PipeEvent.Added(this, startIndex))
        } else if (2 <= addCount) {
            raiseEvent(PipeEvent.RangeAdded(this, startIndex, addCount))
        }
    }

    private fun removeEventRaised(startIndex: Int, count: Int) {
        if (startIndex !in 0..min(source.size, this.count)) {
            return
        }
        val removeCount = min(count, lastIndex - startIndex + 1)
        val removedSize = size - removeCount
        val addCount = if (removedSize < source.size) min(source.size - removedSize, this.count - removedSize) else 0
        size -= removeCount
        if (removeCount == 1) {
            raiseEvent(PipeEvent.Removed(this, startIndex))
        } else if (2 <= removeCount) {
            raiseEvent(PipeEvent.RangeRemoved(this, startIndex, removeCount))
        }
        size += addCount
        if (addCount == 1) {
            raiseEvent(PipeEvent.Added(this, lastIndex))
        } else if (2 <= addCount) {
            raiseEvent(PipeEvent.RangeAdded(this, size - addCount, addCount))
        }
    }
}