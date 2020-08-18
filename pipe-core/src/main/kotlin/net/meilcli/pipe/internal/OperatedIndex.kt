package net.meilcli.pipe.internal

internal data class OperatedIndex(val index: Int, val operate: Operate, val size: Int = 1, val payload: Any? = null) {

    enum class Operate {
        Set, Change
    }
}