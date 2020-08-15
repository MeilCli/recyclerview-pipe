package net.meilcli.pipe.adapter

import net.meilcli.pipe.IPipeItem

class EmptyPipeAdapterArgumentProvider<T : IPipeItem> : IPipeAdapterArgumentProvider<T> {

    override fun provide(index: Int, item: T): IPipeAdapterArgument {
        return EmptyPipeAdapterArgument
    }
}