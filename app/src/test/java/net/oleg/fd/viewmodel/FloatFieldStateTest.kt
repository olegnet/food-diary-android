/*
 * Copyright 2022 Oleg Okhotnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.oleg.fd.viewmodel

import junit.framework.TestCase.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [32])                     // FIXME
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