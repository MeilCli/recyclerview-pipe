package net.meilcli.pipe.internal

import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test

internal class SquashIndicesTest {

    @Test
    fun testSquashIndices() {
        fun assert(indices: List<Int>, expect: List<RangedIndex>) {
            assertIterableEquals(expect, squashIndices(indices))
        }

        assert(
            listOf(0, 1),
            listOf(RangedIndex(0, 2))
        )
        assert(
            listOf(0, 3, 4, 5),
            listOf(RangedIndex(0, 1), RangedIndex(3, 3))
        )
        assert(
            listOf(1, 2, 4, 8, 10, 11),
            listOf(
                RangedIndex(1, 2),
                RangedIndex(4, 1),
                RangedIndex(8, 1),
                RangedIndex(10, 2)
            )
        )

        assert(
            listOf(1, 0),
            listOf(RangedIndex(0, 2))
        )
        assert(
            listOf(5, 4, 3, 0),
            listOf(RangedIndex(3, 3), RangedIndex(0, 1))
        )
        assert(
            listOf(11, 10, 8, 4, 2, 1),
            listOf(
                RangedIndex(10, 2),
                RangedIndex(8, 1),
                RangedIndex(4, 1),
                RangedIndex(1, 2)
            )
        )
    }

    @Test
    fun testSquashOperatedIndices() {
        fun assert(indices: List<OperatedIndex>, expect: List<OperatedIndex>) {
            assertIterableEquals(expect, squashOperatedIndices(indices))
        }

        assert(
            listOf(
                OperatedIndex(0, OperatedIndex.Operate.Set),
                OperatedIndex(1, OperatedIndex.Operate.Set)
            ),
            listOf(OperatedIndex(0, OperatedIndex.Operate.Set, 2))
        )
        assert(
            listOf(
                OperatedIndex(0, OperatedIndex.Operate.Set),
                OperatedIndex(3, OperatedIndex.Operate.Set),
                OperatedIndex(4, OperatedIndex.Operate.Set),
                OperatedIndex(5, OperatedIndex.Operate.Set)
            ),
            listOf(
                OperatedIndex(0, OperatedIndex.Operate.Set, 1),
                OperatedIndex(3, OperatedIndex.Operate.Set, 3)
            )
        )
        assert(
            listOf(
                OperatedIndex(1, OperatedIndex.Operate.Set),
                OperatedIndex(2, OperatedIndex.Operate.Set),
                OperatedIndex(4, OperatedIndex.Operate.Set),
                OperatedIndex(8, OperatedIndex.Operate.Set),
                OperatedIndex(10, OperatedIndex.Operate.Set),
                OperatedIndex(11, OperatedIndex.Operate.Set)
            ),
            listOf(
                OperatedIndex(1, OperatedIndex.Operate.Set, 2),
                OperatedIndex(4, OperatedIndex.Operate.Set, 1),
                OperatedIndex(8, OperatedIndex.Operate.Set, 1),
                OperatedIndex(10, OperatedIndex.Operate.Set, 2)
            )
        )
        assert(
            listOf(
                OperatedIndex(0, OperatedIndex.Operate.Set),
                OperatedIndex(1, OperatedIndex.Operate.Change),
                OperatedIndex(2, OperatedIndex.Operate.Change),
                OperatedIndex(3, OperatedIndex.Operate.Change),
                OperatedIndex(4, OperatedIndex.Operate.Set),
                OperatedIndex(6, OperatedIndex.Operate.Change)
            ),
            listOf(
                OperatedIndex(0, OperatedIndex.Operate.Set, 1),
                OperatedIndex(1, OperatedIndex.Operate.Change, 3),
                OperatedIndex(4, OperatedIndex.Operate.Set, 1),
                OperatedIndex(6, OperatedIndex.Operate.Change, 1)
            )
        )

        assert(
            listOf(
                OperatedIndex(1, OperatedIndex.Operate.Set),
                OperatedIndex(0, OperatedIndex.Operate.Set)
            ),
            listOf(OperatedIndex(0, OperatedIndex.Operate.Set, 2))
        )
        assert(
            listOf(
                OperatedIndex(5, OperatedIndex.Operate.Set),
                OperatedIndex(4, OperatedIndex.Operate.Set),
                OperatedIndex(3, OperatedIndex.Operate.Set),
                OperatedIndex(0, OperatedIndex.Operate.Set)
            ),
            listOf(
                OperatedIndex(3, OperatedIndex.Operate.Set, 3),
                OperatedIndex(0, OperatedIndex.Operate.Set, 1)
            )
        )
        assert(
            listOf(
                OperatedIndex(11, OperatedIndex.Operate.Set),
                OperatedIndex(10, OperatedIndex.Operate.Set),
                OperatedIndex(8, OperatedIndex.Operate.Set),
                OperatedIndex(4, OperatedIndex.Operate.Set),
                OperatedIndex(2, OperatedIndex.Operate.Set),
                OperatedIndex(1, OperatedIndex.Operate.Set)
            ),
            listOf(
                OperatedIndex(10, OperatedIndex.Operate.Set, 2),
                OperatedIndex(8, OperatedIndex.Operate.Set, 1),
                OperatedIndex(4, OperatedIndex.Operate.Set, 1),
                OperatedIndex(1, OperatedIndex.Operate.Set, 2)
            )
        )
        assert(
            listOf(
                OperatedIndex(6, OperatedIndex.Operate.Change),
                OperatedIndex(4, OperatedIndex.Operate.Set),
                OperatedIndex(3, OperatedIndex.Operate.Change),
                OperatedIndex(2, OperatedIndex.Operate.Change),
                OperatedIndex(1, OperatedIndex.Operate.Change),
                OperatedIndex(0, OperatedIndex.Operate.Set)
            ),
            listOf(
                OperatedIndex(6, OperatedIndex.Operate.Change, 1),
                OperatedIndex(4, OperatedIndex.Operate.Set, 1),
                OperatedIndex(1, OperatedIndex.Operate.Change, 3),
                OperatedIndex(0, OperatedIndex.Operate.Set, 1)
            )
        )
    }
}