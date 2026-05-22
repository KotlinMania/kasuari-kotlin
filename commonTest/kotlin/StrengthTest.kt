// port-lint: tests strength.rs
package kasuari

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StrengthTest {

    // ============================================================================
    // new() tests
    // ============================================================================

    @Test
    fun newUnder() {
        assertEquals(Strength.new(-1.0), Strength.ZERO)
    }

    @Test
    fun newMin() {
        assertEquals(Strength.new(0.0), Strength.ZERO)
    }

    @Test
    fun newWeak() {
        assertEquals(Strength.new(1.0), Strength.WEAK)
    }

    @Test
    fun newMedium() {
        assertEquals(Strength.new(1_000.0), Strength.MEDIUM)
    }

    @Test
    fun newStrong() {
        assertEquals(Strength.new(1_000_000.0), Strength.STRONG)
    }

    @Test
    fun newRequired() {
        assertEquals(Strength.new(1_001_001_000.0), Strength.REQUIRED)
    }

    @Test
    fun newOver() {
        assertEquals(Strength.new(1_001_001_001.0), Strength.REQUIRED)
    }

    // ============================================================================
    // create() tests
    // ============================================================================

    @Test
    fun createAllZeroes() {
        assertEquals(Strength.create(0.0, 0.0, 0.0, 1.0), Strength.ZERO)
    }

    @Test
    fun createWeak() {
        assertEquals(Strength.create(0.0, 0.0, 1.0, 1.0), Strength.WEAK)
    }

    @Test
    fun createMedium() {
        assertEquals(Strength.create(0.0, 1.0, 0.0, 1.0), Strength.MEDIUM)
    }

    @Test
    fun createStrong() {
        assertEquals(Strength.create(1.0, 0.0, 0.0, 1.0), Strength.STRONG)
    }

    @Test
    fun createWeakClip() {
        assertEquals(Strength.create(0.0, 0.0, 1000.0, 2.0), Strength.MEDIUM)
    }

    @Test
    fun createMediumClip() {
        assertEquals(Strength.create(0.0, 1000.0, 0.0, 2.0), Strength.STRONG)
    }

    @Test
    fun createStrongClip() {
        assertEquals(Strength.create(1000.0, 0.0, 0.0, 2.0), 1000.0 * Strength.STRONG)
    }

    @Test
    fun createAllNonZero() {
        assertEquals(
            Strength.create(1.0, 1.0, 1.0, 1.0),
            Strength.STRONG + Strength.MEDIUM + Strength.WEAK
        )
    }

    @Test
    fun createMultiplier() {
        assertEquals(
            Strength.create(1.0, 1.0, 1.0, 2.0),
            2.0 * (Strength.STRONG + Strength.MEDIUM + Strength.WEAK)
        )
    }

    @Test
    fun createMax() {
        assertEquals(Strength.create(1000.0, 1000.0, 1000.0, 1.0), Strength.REQUIRED)
    }

    // ============================================================================
    // add tests
    // ============================================================================

    @Test
    fun addZeroPlusZero() {
        assertEquals(Strength.ZERO + Strength.ZERO, Strength.ZERO)
    }

    @Test
    fun addZeroPlusWeak() {
        assertEquals(Strength.ZERO + Strength.WEAK, Strength.WEAK)
    }

    @Test
    fun addWeakPlusZero() {
        assertEquals(Strength.WEAK + Strength.ZERO, Strength.WEAK)
    }

    @Test
    fun addWeakPlusWeak() {
        assertEquals(Strength.WEAK + Strength.WEAK, Strength.new(2.0))
    }

    @Test
    fun addWeakPlusMedium() {
        assertEquals(Strength.WEAK + Strength.MEDIUM, Strength.new(1001.0))
    }

    @Test
    fun addMediumPlusStrong() {
        assertEquals(Strength.MEDIUM + Strength.STRONG, Strength.new(1_001_000.0))
    }

    @Test
    fun addStrongPlusRequired() {
        assertEquals(Strength.STRONG + Strength.REQUIRED, Strength.REQUIRED)
    }

    // ============================================================================
    // sub tests
    // ============================================================================

    @Test
    fun subSaturateLow() {
        assertEquals(Strength.ZERO - Strength.WEAK, Strength.ZERO)
    }

    @Test
    fun subZeroMinusZero() {
        assertEquals(Strength.ZERO - Strength.ZERO, Strength.ZERO)
    }

    @Test
    fun subWeakMinusZero() {
        assertEquals(Strength.WEAK - Strength.ZERO, Strength.WEAK)
    }

    @Test
    fun subWeakMinusWeak() {
        assertEquals(Strength.WEAK - Strength.WEAK, Strength.ZERO)
    }

    @Test
    fun subMediumMinusWeak() {
        assertEquals(Strength.MEDIUM - Strength.WEAK, Strength.new(999.0))
    }

    @Test
    fun subStrongMinusMedium() {
        assertEquals(Strength.STRONG - Strength.MEDIUM, Strength.new(999_000.0))
    }

    @Test
    fun subRequiredMinusStrong() {
        assertEquals(Strength.REQUIRED - Strength.STRONG, Strength.new(1_000_001_000.0))
    }

    @Test
    fun subRequiredMinusRequired() {
        assertEquals(Strength.REQUIRED - Strength.REQUIRED, Strength.ZERO)
    }

    // ============================================================================
    // mul tests
    // ============================================================================

    @Test
    fun mulNegative() {
        assertEquals(Strength.WEAK * -1.0, Strength.ZERO)
    }

    @Test
    fun mulZeroTimesZero() {
        assertEquals(Strength.ZERO * 0.0, Strength.ZERO)
    }

    @Test
    fun mulZeroTimesOne() {
        assertEquals(Strength.ZERO * 1.0, Strength.ZERO)
    }

    @Test
    fun mulWeakTimesZero() {
        assertEquals(Strength.WEAK * 0.0, Strength.ZERO)
    }

    @Test
    fun mulWeakTimesOne() {
        assertEquals(Strength.WEAK * 1.0, Strength.WEAK)
    }

    @Test
    fun mulWeakTimesTwo() {
        assertEquals(Strength.WEAK * 2.0, Strength.new(2.0))
    }

    @Test
    fun mulMediumTimesHalf() {
        assertEquals(Strength.MEDIUM * 0.5, Strength.new(500.0))
    }

    @Test
    fun mulStrongTimesTwo() {
        assertEquals(Strength.STRONG * 2.0, Strength.new(2_000_000.0))
    }

    @Test
    fun mulRequiredTimesHalf() {
        assertEquals(Strength.REQUIRED * 0.5, Strength.new(500_500_500.0))
    }

    // ============================================================================
    // scalar * Strength tests
    // ============================================================================

    @Test
    fun scalarMulNegative() {
        assertEquals(-1.0 * Strength.WEAK, Strength.ZERO)
    }

    @Test
    fun scalarMulTwo() {
        assertEquals(2.0 * Strength.WEAK, Strength.new(2.0))
    }

    // ============================================================================
    // Assignment-style arithmetic tests
    // ============================================================================

    @Test
    fun addAssign() {
        listOf(
            Triple(Strength.ZERO, Strength.ZERO, Strength.ZERO),
            Triple(Strength.ZERO, Strength.WEAK, Strength.WEAK),
            Triple(Strength.WEAK, Strength.ZERO, Strength.WEAK),
            Triple(Strength.WEAK, Strength.WEAK, Strength.new(2.0)),
            Triple(Strength.WEAK, Strength.MEDIUM, Strength.new(1001.0)),
            Triple(Strength.MEDIUM, Strength.STRONG, Strength.new(1_001_000.0)),
            Triple(Strength.STRONG, Strength.REQUIRED, Strength.REQUIRED),
        ).forEach { (lhs, rhs, expected) ->
            var reassigned = lhs
            reassigned += rhs
            assertEquals(expected, reassigned)
            assertEquals(expected, lhs.addAssign(rhs))
        }
    }

    @Test
    fun subAssign() {
        listOf(
            Triple(Strength.ZERO, Strength.WEAK, Strength.ZERO),
            Triple(Strength.ZERO, Strength.ZERO, Strength.ZERO),
            Triple(Strength.WEAK, Strength.ZERO, Strength.WEAK),
            Triple(Strength.WEAK, Strength.WEAK, Strength.ZERO),
            Triple(Strength.MEDIUM, Strength.WEAK, Strength.new(999.0)),
            Triple(Strength.STRONG, Strength.MEDIUM, Strength.new(999_000.0)),
            Triple(Strength.REQUIRED, Strength.STRONG, Strength.new(1_000_001_000.0)),
            Triple(Strength.REQUIRED, Strength.REQUIRED, Strength.ZERO),
        ).forEach { (lhs, rhs, expected) ->
            var reassigned = lhs
            reassigned -= rhs
            assertEquals(expected, reassigned)
            assertEquals(expected, lhs.subAssign(rhs))
        }
    }

    @Test
    fun mulAssign() {
        listOf(
            Triple(Strength.WEAK, -1.0, Strength.ZERO),
            Triple(Strength.ZERO, 0.0, Strength.ZERO),
            Triple(Strength.ZERO, 1.0, Strength.ZERO),
            Triple(Strength.WEAK, 0.0, Strength.ZERO),
            Triple(Strength.WEAK, 1.0, Strength.WEAK),
            Triple(Strength.WEAK, 2.0, Strength.new(2.0)),
            Triple(Strength.MEDIUM, 0.5, Strength.new(500.0)),
            Triple(Strength.STRONG, 2.0, Strength.new(2_000_000.0)),
            Triple(Strength.REQUIRED, 0.5, Strength.new(500_500_500.0)),
        ).forEach { (lhs, rhs, expected) ->
            var reassigned = lhs
            reassigned *= rhs
            assertEquals(expected, reassigned)
            assertEquals(expected, lhs.mulAssign(rhs))
            assertEquals(expected, lhs.mul(rhs))
        }
    }

    @Test
    fun compareStrengths() {
        assertTrue(Strength.ZERO.cmp(Strength.WEAK) < 0)
        assertEquals(0, Strength.MEDIUM.cmp(Strength.MEDIUM))
        assertTrue(Strength.REQUIRED.cmp(Strength.STRONG) > 0)
        assertTrue(Strength.ZERO.partialCmp(Strength.WEAK) < 0)
    }
}
