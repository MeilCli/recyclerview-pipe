@file:Suppress("unused")

package net.meilcli.pipe.operators

import net.meilcli.pipe.IPipe
import net.meilcli.pipe.IPipeItem

fun <T : IPipeItem> linearStack(source1: IPipe<T>, source2: IPipe<T>): IPipe<T> {
    return LinearStackPipe(source1, source2)
}

fun <T : IPipeItem> linearStack(source1: IPipe<T>, source2: IPipe<T>, source3: IPipe<T>): IPipe<T> {
    return LinearStackPipe(linearStack(source1, source2), source3)
}

fun <T : IPipeItem> linearStack(source1: IPipe<T>, source2: IPipe<T>, source3: IPipe<T>, source4: IPipe<T>): IPipe<T> {
    return LinearStackPipe(linearStack(source1, source2, source3), source4)
}

fun <T : IPipeItem> linearStack(source1: IPipe<T>, source2: IPipe<T>, source3: IPipe<T>, source4: IPipe<T>, source5: IPipe<T>): IPipe<T> {
    return LinearStackPipe(linearStack(source1, source2, source3, source4), source5)
}

fun <T : IPipeItem> linearStack(source1: IPipe<T>, source2: IPipe<T>, source3: IPipe<T>, source4: IPipe<T>, source5: IPipe<T>, source6: IPipe<T>): IPipe<T> {
    return LinearStackPipe(linearStack(source1, source2, source3, source4, source5), source6)
}

fun <T : IPipeItem> combine(source1: IPipe<T>, source2: IPipe<T>): IPipe<T> {
    return CombinePipe(source1, source2)
}

fun <T : IPipeItem> combine(source1: IPipe<T>, source2: IPipe<T>, source3: IPipe<T>): IPipe<T> {
    return CombinePipe(combine(source1, source2), source3)
}

fun <T : IPipeItem> combine(source1: IPipe<T>, source2: IPipe<T>, source3: IPipe<T>, source4: IPipe<T>): IPipe<T> {
    return CombinePipe(combine(source1, source2, source3), source4)
}

fun <T : IPipeItem> combine(source1: IPipe<T>, source2: IPipe<T>, source3: IPipe<T>, source4: IPipe<T>, source5: IPipe<T>): IPipe<T> {
    return CombinePipe(combine(source1, source2, source3, source4), source5)
}

fun <T : IPipeItem> combine(source1: IPipe<T>, source2: IPipe<T>, source3: IPipe<T>, source4: IPipe<T>, source5: IPipe<T>, source6: IPipe<T>): IPipe<T> {
    return CombinePipe(combine(source1, source2, source3, source4, source5), source6)
}

