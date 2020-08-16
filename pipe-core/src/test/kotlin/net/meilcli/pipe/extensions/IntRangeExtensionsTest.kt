package net.meilcli.pipe.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class IntRangeExtensionsTest {

    @Test
    fun testSize() {
        assertEquals(3, (0..2).size)
        assertEquals(1, (0..0).size)
        assertEquals(3, (3..5).size)
    }

    @Test
    fun testIntersect() {
        assertEquals(1..3, (0..3).intersect(1..5))
        assertEquals(1..3, (1..4).intersect(1..3))
        assertEquals(1..3, (1..3).intersect(0..5))
    }

    @Test
    fun testInclude() {
        assertEquals(true, (1..4).include(1..3))
        assertEquals(true, (1..4).include(3..4))
        assertEquals(true, (1..4).include(2..3))
        assertEquals(false, (1..4).include(0..3))
        assertEquals(false, (1..4).include(1..6))
    }
}