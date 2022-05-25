package net.oleg.fd.viewmodel

import junit.framework.TestCase.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
open class FloatFieldStateTest {

    @Test
    @Config(qualifiers="en")
    fun floatFieldStateEnTest() {
        assertEquals(FloatFieldState(1.1f), FloatFieldState("1.1", false))
        assertEquals(FloatFieldState("1.1"), FloatFieldState(1.1f, 0))
        assertEquals(FloatFieldState("1.10"), FloatFieldState(1.1f, 2))
        assertEquals(FloatFieldState(""), FloatFieldState(null, 0))

        assertTrue(FloatFieldState("a").isError)
        assertTrue(FloatFieldState(0f).isError)
        assertTrue(FloatFieldState(-1f).isError)
        assertTrue(FloatFieldState("1. 1").isError)
        assertTrue(FloatFieldState("1, 1").isError)

        assertFalse(FloatFieldState(1f).isError)
        assertFalse(FloatFieldState("1.1, ").isError)   // current behavior
        assertFalse(FloatFieldState(" 1.10").isError)
        assertFalse(FloatFieldState("  ").isError)
    }

    @Test
    @Config(qualifiers="ru")
    fun floatFieldStateRuTest() {
        assertEquals(FloatFieldState(1.1f), FloatFieldState("1,1", false))
        assertEquals(FloatFieldState("1,1"), FloatFieldState(1.1f, 0))
        assertEquals(FloatFieldState("1,10"), FloatFieldState(1.1f, 2))
        assertEquals(FloatFieldState(""), FloatFieldState(null, 0))

        assertTrue(FloatFieldState("a").isError)
        assertTrue(FloatFieldState(0f).isError)
        assertTrue(FloatFieldState(-1f).isError)
        assertTrue(FloatFieldState("1. 1").isError)
        assertTrue(FloatFieldState("1, 1").isError)

        assertFalse(FloatFieldState(1f).isError)
        assertFalse(FloatFieldState("1,1. ").isError)   // current behavior
        assertFalse(FloatFieldState(" 1,10").isError)
        assertFalse(FloatFieldState("  ").isError)
    }
}