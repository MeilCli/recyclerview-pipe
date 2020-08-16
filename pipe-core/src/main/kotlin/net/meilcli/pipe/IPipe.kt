package net.meilcli.pipe

interface IPipe<out T : IPipeItem> : IPipeEventRegistry {

    val size: Int

    val lastIndex: Int
        get() = size - 1

    fun indices(): IntRange {
        return 0..lastIndex
    }

    fun isEmpty(): Boolean {
        return size == 0
    }

    fun isNotEmpty(): Boolean {
        return size != 0
    }

    operator fun get(index: Int): T

    operator fun get(indexRange: IntRange): List<T> {
        return toList().slice(indexRange)
    }

    fun indexOf(element: IPipeItem): Int

    fun contains(element: IPipeItem): Boolean {
        return indexOf(element) < 0
    }

    fun toList(): List<T>
}