@file:Suppress("unused")

package net.meilcli.pipe.operators

import net.meilcli.pipe.IPipe
import net.meilcli.pipe.IPipeItem
import net.meilcli.pipe.InsertStrategy

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

fun <T : IPipeItem> combine(source1: IPipe<T>, source2: IPipe<T>, insertStrategy: InsertStrategy = InsertStrategy.Start): IPipe<T> {
    return CombinePipe(source1, source2, insertStrategy)
}

fun <T : IPipeItem> combine(source1: IPipe<T>, source2: IPipe<T>, source3: IPipe<T>, insertStrategy: InsertStrategy = InsertStrategy.Start): IPipe<T> {
    return CombinePipe(combine(source1, source2), source3, insertStrategy)
}

fun <T : IPipeItem> combine(
    source1: IPipe<T>,
    source2: IPipe<T>,
    source3: IPipe<T>,
    source4: IPipe<T>,
    insertStrategy: InsertStrategy = InsertStrategy.Start
): IPipe<T> {
    return CombinePipe(combine(source1, source2, source3), source4, insertStrategy)
}

fun <T : IPipeItem> combine(
    source1: IPipe<T>,
    source2: IPipe<T>,
    source3: IPipe<T>,
    source4: IPipe<T>,
    source5: IPipe<T>,
    insertStrategy: InsertStrategy = InsertStrategy.Start
): IPipe<T> {
    return CombinePipe(combine(source1, source2, source3, source4), source5, insertStrategy)
}

fun <T : IPipeItem> combine(
    source1: IPipe<T>,
    source2: IPipe<T>,
    source3: IPipe<T>,
    source4: IPipe<T>,
    source5: IPipe<T>,
    source6: IPipe<T>,
    insertStrategy: InsertStrategy = InsertStrategy.Start
): IPipe<T> {
    return CombinePipe(combine(source1, source2, source3, source4, source5), source6, insertStrategy)
}

fun <T : IPipeItem> IPipe<T>.take(count: Int): IPipe<T> {
    return TakePipe(this, count)
}

fun <T : IPipeItem> IPipe<T>.skip(count: Int): IPipe<T> {
    return SkipPipe(this, count)
}

fun <T : IPipeItem> IPipe<T>.insertAt(pipe: IPipe<T>, index: Int, insertStrategy: InsertStrategy = InsertStrategy.Start): IPipe<T> {
    return InsertPipe(this, pipe, { it == index }, insertStrategy)
}

fun <T : IPipeItem> IPipe<T>.insertEveryAt(pipe: IPipe<T>, every: Int, insertStrategy: InsertStrategy = InsertStrategy.Start): IPipe<T> {
    if (every < 1) {
        throw IllegalArgumentException("every must over 0")
    }
    return InsertPipe(this, pipe, { it % every == 0 }, insertStrategy)
}