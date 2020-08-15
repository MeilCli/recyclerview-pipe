package net.meilcli.pipe.internal

internal data class OperatedIndex(val index: Int, val operate: Operate, val size: Int = 1) {

    enum class Operate {
        Set, Change
    }
}