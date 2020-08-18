package net.meilcli.pipe.adapter

import net.meilcli.pipe.IPipeItem

interface IPipeViewHolder<in T : IPipeItem> {

    fun initialize() {}

    fun bind(item: T, argument: IPipeAdapterArgument, payloads: List<Any>) {}

    fun unbind() {}
}