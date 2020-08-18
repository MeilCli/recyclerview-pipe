package net.meilcli.pipe.internal

internal fun squashIndices(indices: List<Int>): List<RangedIndex> {
    if (indices.isEmpty()) {
        return emptyList()
    }
    if (indices.size == 1) {
        return listOf(RangedIndex(indices[0], 1))
    }
    val direction = if (indices[0] < indices[1]) 1 else -1

    val result = mutableListOf<RangedIndex>()
    var i = 0
    var startIndex = indices[i]
    var size = 1
    while (i < indices.size) {
        if (i + 1 == indices.size) {
            result += RangedIndex(startIndex, size)
            break
        }
        if (indices[i] + direction != indices[i + 1]) {
            result += RangedIndex(startIndex, size)
            i += 1
            startIndex = indices[i]
            size = 1
            continue
        }
        size += 1
        i += 1
        if (direction == -1) {
            startIndex -= 1
        }
    }

    return result
}

internal fun squashOperatedIndices(indices: List<OperatedIndex>): List<OperatedIndex> {
    if (indices.isEmpty()) {
        return emptyList()
    }
    if (indices.size == 1) {
        return indices
    }
    val direction = if (indices[0].index < indices[1].index) 1 else -1

    val result = mutableListOf<OperatedIndex>()
    var i = 0
    var operate = indices[i].operate
    var startIndex = indices[i].index
    var payload = indices[i].payload
    var size = 1
    while (i < indices.size) {
        if (i + 1 == indices.size) {
            result += OperatedIndex(startIndex, operate, size, payload)
            break
        }
        if (indices[i].index + direction != indices[i + 1].index ||
            indices[i].operate != indices[i + 1].operate ||
            indices[i].payload != indices[i + 1].payload
        ) {
            result += OperatedIndex(startIndex, operate, size, payload)
            i += 1
            operate = indices[i].operate
            startIndex = indices[i].index
            payload = indices[i].payload
            size = 1
            continue
        }
        size += 1
        i += 1
        if (direction == -1) {
            startIndex -= 1
        }
    }

    return result
}