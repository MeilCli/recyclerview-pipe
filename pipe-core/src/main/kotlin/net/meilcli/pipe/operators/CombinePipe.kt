package net.meilcli.pipe.operators

import net.meilcli.pipe.*

class CombinePipe<T : IPipeItem>(
    private val source1: IPipe<T>,
    private val source2: IPipe<T>
) : IPipe<T> {

    private inner class NotifyPipeChanged : INotifyPipeChanged {

        override fun eventRaised(event: PipeEvent) = when (event.sender) {
            source1 -> eventRaisedBySource(source1, event)
            source2 -> eventRaisedBySource(source2, event)
            combinedPipe -> eventRaisedByCombinedPipe(event)
            else -> Unit
        }
    }

    private val combinedPipe = MutableListPipe<T>()
    private val combinedIndexed = mutableListOf<Pair<IPipe<T>, Int>>()
    private val eventNotifiers = mutableListOf<INotifyPipeChanged>()

    override val size: Int
        get() = combinedPipe.size

    init {
        val notifyPipeChanged = NotifyPipeChanged()
        source1.registerNotifyPipeChanged(notifyPipeChanged)
        source2.registerNotifyPipeChanged(notifyPipeChanged)
        combinedPipe.registerNotifyPipeChanged(notifyPipeChanged)
        combinedPipe.addAll(source1.toList() + source2.toList())
    }

    override fun get(index: Int): T {
        return combinedPipe[index]
    }

    override fun indexOf(element: T): Int {
        return combinedPipe.indexOf(element)
    }

    override fun toList(): List<T> {
        return combinedPipe.toList()
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

    private fun eventRaisedBySource(pipe: IPipe<T>, pipeEvent: PipeEvent) {
        when (pipeEvent) {
            is PipeEvent.Added -> {
                combinedIndexed.add(Pair(pipe, pipeEvent.index))
                combinedPipe.add(pipe[pipeEvent.index])
            }
            is PipeEvent.RangeAdded -> {
                for (i in pipeEvent.range) {
                    combinedIndexed.add(Pair(pipe, i))
                }
                combinedPipe.addAll(pipe.toList().slice(pipeEvent.range))
            }
            is PipeEvent.Changed -> {
                val index = combinedIndexed.indexOfFirst { it == Pair(pipe, pipeEvent.index) }
                if (index < 0) {
                    return
                }
                combinedPipe.change(index, pipe[pipeEvent.index])
            }
            is PipeEvent.RangeChanged -> {
                for (i in pipeEvent.range) {
                    val index = combinedIndexed.indexOfFirst { it == Pair(pipe, i) }
                    if (index < 0) {
                        return
                    }
                    combinedPipe.change(index, pipe[i])
                }
            }
            is PipeEvent.Removed -> {
                val index = combinedIndexed.indexOfFirst { it == Pair(pipe, pipeEvent.index) }
                if (index < 0) {
                    return
                }

                combinedIndexed.removeAt(index)
                combinedPipe.removeAt(index)
            }
            is PipeEvent.RangeRemoved -> {
                val removalIndices = combinedIndexed.asSequence()
                    .mapIndexed { index, pair -> Pair(index, pair) }
                    .filter { it.second.first == pipe }
                    .filter { it.second.second in pipeEvent.range }
                    .map { it.first }
                    .toList()
                    .asReversed()

                removalIndices.forEach { combinedIndexed.removeAt(it) }
                combinedPipe.removeAll { index, _ -> removalIndices.contains(index) }
            }
            is PipeEvent.Moved -> {
                val fromPosition = combinedIndexed.indexOfFirst { it == Pair(pipe, pipeEvent.fromIndex) }
                if (fromPosition < 0) {
                    return
                }
                val toPosition = combinedIndexed.indexOfFirst { it == Pair(pipe, pipeEvent.toIndex) }
                if (toPosition < 0) {
                    return
                }
                if (fromPosition < toPosition) {
                    for (i in fromPosition..toPosition) {
                        val old = combinedIndexed[i]
                        combinedIndexed[i] = Pair(old.first, old.second - 1)
                    }
                    combinedIndexed.removeAt(fromPosition)
                    combinedIndexed.add(toPosition, Pair(pipe, pipeEvent.toIndex))
                }
                if (toPosition < fromPosition) {
                    for (i in toPosition..fromPosition) {
                        val old = combinedIndexed[i]
                        combinedIndexed[i] = Pair(old.first, old.second + 1)
                    }
                    combinedIndexed.removeAt(fromPosition)
                    combinedIndexed.add(toPosition, Pair(pipe, pipeEvent.toIndex))
                }
                combinedPipe.move(fromPosition, toPosition)
            }
            is PipeEvent.Reset -> {
                combinedIndexed.clear()
                combinedPipe.clear()
            }
        }
    }

    private fun eventRaisedByCombinedPipe(pipeEvent: PipeEvent) {
        raiseEvent(pipeEvent.clone(this))
    }
}