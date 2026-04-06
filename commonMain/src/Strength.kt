// port-lint: source strength.rs
package kasuari

/**
 * Contains useful constants and functions for producing strengths for use in the constraint
 * solver. Each constraint added to the solver has an associated strength specifying the precedence
 * the solver should impose when choosing which constraints to enforce. It will try to enforce all
 * constraints, but if that is impossible the lowest strength constraints are the first to be
 * violated.
 *
 * Strengths are simply real numbers. The strongest legal strength is 1,001,001,000.0. The weakest
 * is 0.0. For convenience constants are declared for commonly used strengths. These are
 * [REQUIRED], [STRONG], [MEDIUM] and [WEAK]. Feel free to multiply these by other values
 * to get intermediate strengths. Note that the solver will clip given strengths to the legal
 * range.
 *
 * [REQUIRED] signifies a constraint that cannot be violated under any circumstance. Use this
 * special strength sparingly, as the solver will fail completely if it find that not all of the
 * [REQUIRED] constraints can be satisfied. The other strengths represent fallible constraints.
 * These should be the most commonly used strenghts for use cases where violating a constraint is
 * acceptable or even desired.
 *
 * The solver will try to get as close to satisfying the constraints it violates as possible,
 * strongest first. This behaviour can be used (for example) to provide a "default" value for a
 * variable should no other stronger constraints be put upon it.
 */
data class Strength(private val value: Double) : Comparable<Strength> {

    companion object {
        /** The required strength for a constraint. This is the strongest possible strength. */
        val REQUIRED: Strength = Strength(1_001_001_000.0)

        /** A strong strength for a constraint. This is weaker than [REQUIRED] but stronger than [MEDIUM]. */
        val STRONG: Strength = Strength(1_000_000.0)

        /** A medium strength for a constraint. This is weaker than [STRONG] but stronger than [WEAK]. */
        val MEDIUM: Strength = Strength(1_000.0)

        /** A weak strength for a constraint. This is weaker than [MEDIUM] but stronger than `0.0`. */
        val WEAK: Strength = Strength(1.0)

        /** The weakest possible strength for a constraint. This is weaker than [WEAK]. */
        val ZERO: Strength = Strength(0.0)

        /** Create a new strength with the given value, clipped to the legal range (0.0, REQUIRED) */
        fun new(value: Double): Strength =
            Strength(value.clamp(0.0, REQUIRED.value()))

        /**
         * Create a constraint as a linear combination of STRONG, MEDIUM and WEAK strengths.
         *
         * Each weight is multiplied by the multiplier, clamped to the legal range and then multiplied
         * by the corresponding strength. The resulting strengths are then summed.
         */
        fun create(strong: Double, medium: Double, weak: Double, multiplier: Double): Strength {
            val strongValue = (strong * multiplier).clamp(0.0, 1000.0) * STRONG.value()
            val mediumValue = (medium * multiplier).clamp(0.0, 1000.0) * MEDIUM.value()
            val weakValue = (weak * multiplier).clamp(0.0, 1000.0) * WEAK.value()
            return new(strongValue + mediumValue + weakValue)
        }
    }

    /** The value of the strength */
    fun value(): Double =
        value

    /** Add two strengths together, clamping the result to the legal range */
    fun add(rhs: Strength): Strength =
        new(this.value + rhs.value)

    /** Subtract one strength from another, clipping the result to the legal range */
    fun sub(rhs: Strength): Strength =
        new(this.value - rhs.value)

    /** Multiply a strength by a scalar, clipping the result to the legal range */
    fun mulF64(rhs: Double): Strength =
        new(this.value * rhs)

    /** Multiply a strength by a scalar, clipping the result to the legal range */
    fun mulF32(rhs: Float): Strength =
        new(this.value * rhs.toDouble())

    /** Divide a strength by a scalar, clipping the result to the legal range */
    fun divF64(rhs: Double): Strength =
        new(this.value / rhs)

    /** Divide a strength by a scalar, clipping the result to the legal range */
    fun divF32(rhs: Float): Strength =
        new(this.value / rhs.toDouble())

    /** Add two strengths together, clipping the result to the legal range */
    operator fun plus(rhs: Strength): Strength =
        add(rhs)

    /** Subtract one strength from another, clipping the result to the legal range */
    operator fun minus(rhs: Strength): Strength =
        sub(rhs)

    /** Multiply a strength by a scalar, clipping the result to the legal range */
    operator fun times(rhs: Double): Strength =
        mulF64(rhs)

    /** Multiply a strength by a scalar, clipping the result to the legal range */
    operator fun times(rhs: Float): Strength =
        mulF32(rhs)

    /** Divide a strength by a scalar, clipping the result to the legal range */
    operator fun div(rhs: Double): Strength =
        divF64(rhs)

    /** Divide a strength by a scalar, clipping the result to the legal range */
    operator fun div(rhs: Float): Strength =
        divF32(rhs)

    override fun compareTo(other: Strength): Int =
        this.value.compareTo(other.value)
}

/** Multiply a scalar by a strength, clipping the result to the legal range */
operator fun Double.times(rhs: Strength): Strength =
    rhs.mulF64(this)

private fun Double.clamp(min: Double, max: Double): Double =
    this.coerceIn(min, max)
