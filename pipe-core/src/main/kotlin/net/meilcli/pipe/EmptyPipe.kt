package net.meilcli.pipe

class EmptyPipe<T : IPipeItem> : IPipe<T> {

    override val size: Int
        get() = 0

    override fun get(index: Int): T {
        throw UnsupportedOperationException()
    }

    override fun indexOf(element: IPipeItem): Int {
        return -1
    }

    override fun toList(): List<T> {
        return emptyList()
    }

    override fun registerNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged) {
    }

    override fun unregisterNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged) {
    }
}