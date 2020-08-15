package net.meilcli.pipe

interface IPipe<T : IPipeItem> {

    val size: Int

    val lastIndex: Int
        get() = size - 1

    fun isEmpty(): Boolean {
        return size == 0
    }

    fun isNotEmpty(): Boolean {
        return size != 0
    }

    operator fun get(index: Int): T

    fun indexOf(element: T): Int

    fun contains(element: T): Boolean {
        return indexOf(element) < 0
    }

    fun toList(): List<T>

    fun registerNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged)

    fun unregisterNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged)
}