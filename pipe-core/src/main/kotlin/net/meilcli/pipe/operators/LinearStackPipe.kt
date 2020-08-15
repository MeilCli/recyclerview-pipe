package net.meilcli.pipe.operators

import net.meilcli.pipe.INotifyPipeChanged
import net.meilcli.pipe.IPipe
import net.meilcli.pipe.IPipeItem
import net.meilcli.pipe.PipeEvent

class LinearStackPipe<T : IPipeItem>(
    private val source1: IPipe<T>,
    private val source2: IPipe<T>
) : IPipe<T> {

    private inner class NotifyPipeChanged : INotifyPipeChanged {

        override fun eventRaised(event: PipeEvent) = when (event.sender) {
            source1 -> eventRaisedBySource1(event)
            source2 -> eventRaisedBySource2(event)
            else -> Unit
        }
    }

    private val eventNotifiers = mutableListOf<INotifyPipeChanged>()

    override val size: Int
        get() = source1.size + source2.size

    init {
        val notifyPipeChanged = NotifyPipeChanged()
        source1.registerNotifyPipeChanged(notifyPipeChanged)
        source2.registerNotifyPipeChanged(notifyPipeChanged)
        if (isNotEmpty()) {
            if (size == 1) {
                raiseEvent(PipeEvent.Added(this, 0))
            } else {
                raiseEvent(PipeEvent.RangeAdded(this, 0, size))
            }
        }
    }

    override fun get(index: Int): T {
        return when (index) {
            in 0 until source1.size -> source1[index]
            in source1.size until size -> source2[index - source1.size]
            else -> throw IndexOutOfBoundsException("index $index not in 0 until $size")
        }
    }

    override fun indexOf(element: T): Int {
        var index = source1.indexOf(element)
        if (index < 0) {
            index = source2.indexOf(element)
            if (0 < index) {
                index + source1.size
            }
        }
        return index
    }

    override fun toList(): List<T> {
        return source1.toList() + source2.toList()
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

    private fun eventRaisedBySource1(event: PipeEvent) {
        raiseEvent(event.clone(this))
    }

    private fun eventRaisedBySource2(event: PipeEvent) = when (event) {
        is PipeEvent.Added -> raiseEvent(PipeEvent.Added(this, source1.size + event.index))
        is PipeEvent.RangeAdded -> raiseEvent(PipeEvent.RangeAdded(this, source1.size + event.startIndex, event.count))
        is PipeEvent.Changed -> raiseEvent(PipeEvent.Changed(this, source1.size + event.index))
        is PipeEvent.RangeChanged -> raiseEvent(PipeEvent.RangeChanged(this, source1.size + event.startIndex, event.count))
        is PipeEvent.Removed -> raiseEvent(PipeEvent.Removed(this, source1.size + event.index))
        is PipeEvent.RangeRemoved -> raiseEvent(PipeEvent.RangeRemoved(this, source1.size + event.startIndex, event.count))
        is PipeEvent.Moved -> raiseEvent(PipeEvent.Moved(this, source1.size + event.fromIndex, source1.size + event.toIndex))
        is PipeEvent.Reset -> raiseEvent(PipeEvent.Reset(this))
    }
}