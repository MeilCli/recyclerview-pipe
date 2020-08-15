package net.meilcli.pipe.adapter

import net.meilcli.pipe.IPipe
import net.meilcli.pipe.IPipeItem

interface IPipeAdapter<TItem : IPipeItem> : IPipe<TItem> {

    fun set(pipe: IPipe<TItem>)
}