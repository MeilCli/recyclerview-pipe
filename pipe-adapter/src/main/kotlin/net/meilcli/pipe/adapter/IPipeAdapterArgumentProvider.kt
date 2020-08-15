package net.meilcli.pipe.adapter

import net.meilcli.pipe.IPipeItem

interface IPipeAdapterArgumentProvider<T : IPipeItem> {

    fun provide(index: Int, item: T): IPipeAdapterArgument
}