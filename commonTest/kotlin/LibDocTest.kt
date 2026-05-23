// port-lint: source lib.rs
package kasuari

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Port of the runnable doctest in `lib.rs` (the second `//! ` ... `//! ` block, lines
 * 166-218). The doctest exercises the full constraint-solving tutorial end to end:
 * sets up `windowWidth` plus two `Element` boxes, adds the alignment / no-overlap /
 * preferred-width constraints, makes `windowWidth` an edit variable, suggests two
 * different widths, then adds a ratio constraint between the two box widths.
 *
 * The rustdoc example doesn't carry explicit assertions, but the prose immediately
 * above and below each fetchChanges call documents the expected output. This Kotlin
 * port turns those documented expectations into real assertions where they are
 * deterministic and uses [assertTrue] for the one nondeterministic step the upstream
 * rustdoc calls out.
 */
class LibDocTest {

    private data class Element(val left: Variable, val right: Variable)

    @Test
    fun libDocExample() {
        val (valueOf, updateValues) = newValues()

        val windowWidth = Variable.new()
        val box1 = Element(left = Variable.new(), right = Variable.new())
        val box2 = Element(left = Variable.new(), right = Variable.new())

        val solver = Solver.new()
        solver.addConstraints(listOf(
            windowWidth with WeightedRelation.GE(Strength.REQUIRED) to 0.0,            // positive window width
            box1.left   with WeightedRelation.EQ(Strength.REQUIRED) to 0.0,            // left align
            box2.right  with WeightedRelation.EQ(Strength.REQUIRED) to windowWidth,    // right align
            box2.left   with WeightedRelation.GE(Strength.REQUIRED) to box1.right,     // no overlap
            // positive widths
            box1.left   with WeightedRelation.LE(Strength.REQUIRED) to box1.right,
            box2.left   with WeightedRelation.LE(Strength.REQUIRED) to box2.right,
            // preferred widths:
            (box1.right - box1.left) with WeightedRelation.EQ(Strength.WEAK) to 50.0,
            (box2.right - box2.left) with WeightedRelation.EQ(Strength.WEAK) to 100.0,
        ))

        solver.addEditVariable(windowWidth, Strength.STRONG)
        solver.suggestValue(windowWidth, 300.0)
        updateValues(solver.fetchChanges())

        // Expected output documented in the rustdoc after the first suggestValue(300.0):
        //   windowWidth: 300, box1.right: 50, box2.left: 200, box2.right: 300
        // (box1.left is unchanged at the solver-initial 0.0).
        assertEquals(300.0, valueOf(windowWidth))
        assertEquals(0.0, valueOf(box1.left))
        assertEquals(50.0, valueOf(box1.right))
        assertEquals(200.0, valueOf(box2.left))
        assertEquals(300.0, valueOf(box2.right))

        solver.suggestValue(windowWidth, 75.0)
        updateValues(solver.fetchChanges())

        // The rustdoc calls this step out as nondeterministic — the solver may pick
        // either of two valid weak-constraint violation sets:
        //   (a) box1.right: 0, box2.left: 0, box2.right: 75
        //   (b) box2.left: 50, box2.right: 75 (box1.right stays at the prior value)
        // The deterministic invariants are: the layout still fits in the new width
        // and no box has negative width.
        assertEquals(75.0, valueOf(windowWidth))
        assertEquals(75.0, valueOf(box2.right))
        assertEquals(0.0, valueOf(box1.left))
        assertTrue(valueOf(box1.right) >= 0.0 && valueOf(box1.right) <= 75.0)
        assertTrue(valueOf(box2.left) >= valueOf(box1.right) && valueOf(box2.left) <= 75.0)

        solver.addConstraint(
            ((box1.right - box1.left) / 50.0) with WeightedRelation.EQ(Strength.MEDIUM) to ((box2.right - box2.left) / 100.0),
        )
        updateValues(solver.fetchChanges())

        // After the ratio constraint the rustdoc documents the changes as:
        //   box1.right: 25, box2.left: 25
        // i.e. the ratio (box1.width : box2.width) = (50 : 100) drives both boxes
        // to share the remaining 75 pixels in a 1:2 split (25 and 50), leaving
        // box1.right at 25 and box2.left at 25 (start of the second box).
        assertEquals(75.0, valueOf(windowWidth))
        assertEquals(25.0, valueOf(box1.right))
        assertEquals(25.0, valueOf(box2.left))
        assertEquals(75.0, valueOf(box2.right))
        // The ratio between the two widths matches the upstream constraint:
        //   (box1.right - box1.left) / 50.0 == (box2.right - box2.left) / 100.0
        val box1Width = valueOf(box1.right) - valueOf(box1.left)
        val box2Width = valueOf(box2.right) - valueOf(box2.left)
        assertEquals(box1Width / 50.0, box2Width / 100.0, 1e-9)
    }
}
