// port-lint: source variable.rs
package kasuari

import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Identifies a variable for the constraint solver.
 *
 * Each new variable is unique in the view of the solver, but copying or cloning the variable
 * produces a copy of the same variable.
 */
@ConsistentCopyVisibility
data class Variable internal constructor(val id: Long) : Comparable<Variable> {

    override fun compareTo(other: Variable): Int = id.compareTo(other.id)

    companion object {
        @OptIn(ExperimentalAtomicApi::class)
        private val nextId = AtomicLong(0)

        /** Produces a new unique variable for use in constraint solving. */
        @OptIn(ExperimentalAtomicApi::class)
        fun new(): Variable = Variable(nextId.fetchAndAdd(1))

        /** Default factory — mirrors the upstream `Default` derive on `Variable`. */
        fun default(): Variable = new()

        /**
         * Constructs a [Variable] with a specific ID — test-only helper used by the
         * Kotlin test suite to mint deterministic variables in fixtures.
         */
        fun fromId(id: Long): Variable = Variable(id)
    }

    operator fun plus(constant: Double): Expression = Term.from(this) + constant
    operator fun plus(constant: Float): Expression = Term.from(this) + constant.toDouble()
    operator fun plus(other: Variable): Expression = Term.from(this) + Term.from(other)
    operator fun plus(term: Term): Expression = Term.from(this) + term
    operator fun plus(expression: Expression): Expression = Term.from(this) + expression

    operator fun minus(constant: Double): Expression = Term.from(this) - constant
    operator fun minus(constant: Float): Expression = Term.from(this) - constant.toDouble()
    operator fun minus(other: Variable): Expression = Term.from(this) - Term.from(other)
    operator fun minus(term: Term): Expression = Term.from(this) - term
    operator fun minus(expression: Expression): Expression = Term.from(this) - expression

    operator fun times(coefficient: Double): Term = Term.new(this, coefficient)
    operator fun times(coefficient: Float): Term = Term.new(this, coefficient.toDouble())

    operator fun div(coefficient: Double): Term = Term.new(this, 1.0 / coefficient)
    operator fun div(coefficient: Float): Term = Term.new(this, 1.0 / coefficient.toDouble())

    operator fun unaryMinus(): Term = -Term.from(this)
}

// ============================================================================
// Extension operator overloads — the Kotlin-idiomatic translation of the
// upstream impl blocks where Variable is the right-hand operand.
// ============================================================================

operator fun Double.plus(variable: Variable): Expression =
    Term.from(variable) + this

operator fun Float.plus(variable: Variable): Expression =
    Term.from(variable) + this.toDouble()

operator fun Double.minus(variable: Variable): Expression =
    this - Term.from(variable)

operator fun Float.minus(variable: Variable): Expression =
    this.toDouble() - Term.from(variable)

operator fun Double.times(variable: Variable): Term =
    Term.new(variable, this)

operator fun Float.times(variable: Variable): Term =
    Term.new(variable, this.toDouble())

operator fun Term.plus(variable: Variable): Expression =
    this + Term.from(variable)

operator fun Expression.plus(variable: Variable): Expression =
    this + Term.from(variable)

operator fun Term.minus(variable: Variable): Expression =
    this - Term.from(variable)

operator fun Expression.minus(variable: Variable): Expression =
    this - Term.from(variable)

operator fun Expression.plusAssign(variable: Variable) {
    this += Term.from(variable)
}

operator fun Expression.minusAssign(variable: Variable) {
    this -= Term.from(variable)
}

// ============================================================================
// Kotlin-side conversion helpers that mirror the upstream `From<Variable>`
// conversions defined in term.rs and expression.rs.
// ============================================================================

/** Converts this [Variable] to a [Term] with coefficient 1.0. */
fun Variable.toTerm(): Term = Term.fromVariable(this)

/** Converts this [Variable] to an [Expression] containing only this variable. */
fun Variable.toExpression(): Expression = Expression.fromVariable(this)
