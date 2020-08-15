package net.meilcli.pipe

interface IPipeItem {

    fun areItemsTheSame(other: IPipeItem): Boolean {
        return this == other
    }

    fun areContentsTheSame(other: IPipeItem): Boolean {
        return this == other
    }
}