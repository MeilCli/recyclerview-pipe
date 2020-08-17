package net.meilcli.pipe

interface IMutableListPipe<T : IPipeItem> : IPipe<T> {

    fun add(element: T)

    fun add(index: Int, element: T)

    fun addAll(elements: Collection<T>)

    fun addAll(index: Int, elements: Collection<T>)

    fun remove(element: T)

    fun removeAt(index: Int)

    fun removeAll(selector: (Int, T) -> Boolean)

    fun change(index: Int, element: T)

    fun changeAll(selector: (Int, T) -> T)

    fun move(fromIndex: Int, toIndex: Int)

    fun clear()
}