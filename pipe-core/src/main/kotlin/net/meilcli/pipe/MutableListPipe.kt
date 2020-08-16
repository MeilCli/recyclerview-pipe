package net.meilcli.pipe

import net.meilcli.pipe.internal.INotifyPipeChangedContainer
import net.meilcli.pipe.internal.OperatedIndex
import net.meilcli.pipe.internal.squashIndices
import net.meilcli.pipe.internal.squashOperatedIndices

class MutableListPipe<T : IPipeItem> : IMutablePipe<T>, INotifyPipeChangedContainer {

    private val source = mutableListOf<T>()

    override val eventNotifiers = mutableListOf<INotifyPipeChanged>()

    override val size: Int
        get() = source.size

    override fun get(index: Int): T {
        return source[index]
    }

    override fun indexOf(element: T): Int {
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
        if (elements.isEmpty()) {
            return
        }
        val startIndex = source.size
        source.addAll(elements)
        if (elements.size == 1) {
            raiseEvent(PipeEvent.Added(this, startIndex))
        } else {
            raiseEvent(PipeEvent.RangeAdded(this, startIndex, elements.size))
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
        raiseEvent(PipeEvent.Changed(this, index))
    }

    override fun changeAll(selector: (Int, T) -> T) {
        val changes = source.asSequence()
            .mapIndexed { index, item -> Pair(index, selector(index, item)) }
            .filter { source[it.first].areItemsTheSame(it.second) && source[it.first].areContentsTheSame(it.second) }
            .map {
                if (source[it.first].areItemsTheSame(it.second)) {
                    Pair(OperatedIndex(it.first, OperatedIndex.Operate.Set), it.second)
                } else {
                    Pair(OperatedIndex(it.first, OperatedIndex.Operate.Change), it.second)
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
                    raiseEvent(PipeEvent.Changed(this, squashedIndex.index))
                }
            } else {
                if (squashedIndex.operate == OperatedIndex.Operate.Set) {
                    raiseEvent(PipeEvent.RangeAdded(this, squashedIndex.index, squashedIndex.size))
                    raiseEvent(PipeEvent.RangeRemoved(this, squashedIndex.index, squashedIndex.size))
                } else {
                    raiseEvent(PipeEvent.RangeChanged(this, squashedIndex.index, squashedIndex.size))
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