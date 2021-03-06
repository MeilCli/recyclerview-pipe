package net.meilcli.pipe

import net.meilcli.pipe.internal.INotifyPipeChangedContainer
import net.meilcli.pipe.internal.OperatedIndex
import net.meilcli.pipe.internal.squashIndices
import net.meilcli.pipe.internal.squashOperatedIndices

class MutableListPipe<T : IPipeItem> : IMutableListPipe<T>, INotifyPipeChangedContainer {

    private val source = mutableListOf<T>()

    override val eventNotifiers = mutableListOf<INotifyPipeChanged>()

    override val size: Int
        get() = source.size

    override fun get(index: Int): T {
        return source[index]
    }

    override fun indexOf(element: IPipeItem): Int {
        return source.indexOf(element)
    }

    override fun toList(): List<T> {
        return source
    }

    override fun add(element: T) {
        source.add(element)
        raiseEvent(PipeEvent.Added(this, source.lastIndex))
    }

    override fun add(index: Int, element: T) {
        source.add(index, element)
        raiseEvent(PipeEvent.Added(this, index))
    }

    override fun addAll(elements: Collection<T>) {
        addAll(source.size, elements)
    }

    override fun addAll(index: Int, elements: Collection<T>) {
        if (elements.isEmpty()) {
            return
        }
        source.addAll(index, elements)
        if (elements.size == 1) {
            raiseEvent(PipeEvent.Added(this, index))
        } else {
            raiseEvent(PipeEvent.RangeAdded(this, index, elements.size))
        }
    }

    override fun remove(element: T) {
        val index = source.indexOf(element)
        if (0 <= index) {
            source.removeAt(index)
            raiseEvent(PipeEvent.Removed(this, index))
        }
    }

    override fun removeAt(index: Int) {
        source.removeAt(index)
        raiseEvent(PipeEvent.Removed(this, index))
    }

    override fun removeAll(selector: (Int, T) -> Boolean) {
        val removalIndices = source.asSequence()
            .mapIndexed { index, item -> Pair(index, selector(index, item)) }
            .filter { it.second }
            .map { it.first }
            .toList()
            .asReversed()

        for (i in removalIndices) {
            source.removeAt(i)
        }

        val rangedIndices = squashIndices(removalIndices)
        for (rangedIndex in rangedIndices) {
            if (rangedIndex.size == 1) {
                raiseEvent(PipeEvent.Removed(this, rangedIndex.startIndex))
            } else {
                raiseEvent(PipeEvent.RangeRemoved(this, rangedIndex.startIndex, rangedIndex.size))
            }
        }
    }

    override fun change(index: Int, element: T) {
        val old = source[index]
        if (old.areItemsTheSame(element).not()) {
            source[index] = element
            raiseEvent(PipeEvent.Removed(this, index))
            raiseEvent(PipeEvent.Added(this, index))
            return
        }
        if (old.areContentsTheSame(element)) {
            return
        }
        source[index] = element
        raiseEvent(PipeEvent.Changed(this, index, old.getPayload(element)))
    }

    override fun changeAll(selector: (Int, T) -> T) {
        val changes = source.asSequence()
            .mapIndexed { index, item -> Pair(index, selector(index, item)) }
            .filter {
                val old = source[it.first]
                old.areItemsTheSame(it.second) && old.areContentsTheSame(it.second)
            }
            .map {
                val old = source[it.first]
                if (old.areItemsTheSame(it.second)) {
                    Pair(OperatedIndex(it.first, OperatedIndex.Operate.Set), it.second)
                } else {
                    Pair(OperatedIndex(it.first, OperatedIndex.Operate.Change, payload = old.getPayload(it.second)), it.second)
                }
            }
            .toList()

        for (change in changes) {
            source[change.first.index] = change.second
        }

        val squashedIndices = squashOperatedIndices(changes.map { it.first })
        for (squashedIndex in squashedIndices) {
            if (squashedIndex.size == 1) {
                if (squashedIndex.operate == OperatedIndex.Operate.Set) {
                    raiseEvent(PipeEvent.Added(this, squashedIndex.index))
                    raiseEvent(PipeEvent.Removed(this, squashedIndex.index))
                } else {
                    raiseEvent(PipeEvent.Changed(this, squashedIndex.index, squashedIndex.payload))
                }
            } else {
                if (squashedIndex.operate == OperatedIndex.Operate.Set) {
                    raiseEvent(PipeEvent.RangeAdded(this, squashedIndex.index, squashedIndex.size))
                    raiseEvent(PipeEvent.RangeRemoved(this, squashedIndex.index, squashedIndex.size))
                } else {
                    raiseEvent(PipeEvent.RangeChanged(this, squashedIndex.index, squashedIndex.size, squashedIndex.payload))
                }
            }
        }
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        val element = source[fromIndex]
        source.removeAt(fromIndex)
        source.add(toIndex, element)
        raiseEvent(PipeEvent.Moved(this, fromIndex, toIndex))
    }

    override fun clear() {
        source.clear()
        raiseEvent(PipeEvent.Reset(this))
    }
}