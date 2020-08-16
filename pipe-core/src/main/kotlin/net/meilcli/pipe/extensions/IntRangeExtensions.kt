package net.meilcli.pipe.extensions

import kotlin.math.max
import kotlin.math.min

val IntRange.size: Int
    get() = last - first + 1

fun IntRange.intersect(other: IntRange): IntRange {
    return IntRange(max(first, other.first), min(last, other.last))
}

fun IntRange.include(other: IntRange): Boolean {
    return first <= other.first && other.last <= last
}