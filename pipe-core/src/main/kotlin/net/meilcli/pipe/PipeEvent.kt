package net.meilcli.pipe

sealed class PipeEvent(val sender: IPipe<*>) {

    abstract fun clone(sender: IPipe<*>): PipeEvent

    class Changed(sender: IPipe<*>, val index: Int) : PipeEvent(sender) {

        override fun clone(sender: IPipe<*>): PipeEvent {
            return Changed(sender, index)
        }
    }

    class RangeChanged(sender: IPipe<*>, val startIndex: Int, val count: Int) : PipeEvent(sender) {

        val range: IntRange = startIndex..(startIndex + count)

        override fun clone(sender: IPipe<*>): PipeEvent {
            return RangeChanged(sender, startIndex, count)
        }
    }

    class Added(sender: IPipe<*>, val index: Int) : PipeEvent(sender) {

        override fun clone(sender: IPipe<*>): PipeEvent {
            return Added(sender, index)
        }
    }

    class RangeAdded(sender: IPipe<*>, val startIndex: Int, val count: Int) : PipeEvent(sender) {

        val range: IntRange = startIndex..(startIndex + count)

        override fun clone(sender: IPipe<*>): PipeEvent {
            return RangeAdded(sender, startIndex, count)
        }
    }

    class Removed(sender: IPipe<*>, val index: Int) : PipeEvent(sender) {

        override fun clone(sender: IPipe<*>): PipeEvent {
            return Removed(sender, index)
        }
    }

    class RangeRemoved(sender: IPipe<*>, val startIndex: Int, val count: Int) : PipeEvent(sender) {

        val range: IntRange = startIndex..(startIndex + count)

        override fun clone(sender: IPipe<*>): PipeEvent {
            return RangeRemoved(sender, startIndex, count)
        }
    }

    class Moved(sender: IPipe<*>, val fromIndex: Int, val toIndex: Int) : PipeEvent(sender) {

        override fun clone(sender: IPipe<*>): PipeEvent {
            return Moved(sender, fromIndex, toIndex)
        }
    }

    class Reset(sender: IPipe<*>) : PipeEvent(sender) {

        override fun clone(sender: IPipe<*>): PipeEvent {
            return Reset(sender)
        }
    }
}