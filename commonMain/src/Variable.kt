// port-lint: source variable.rs
package kasuari

import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Identifies a variable for the constraint solver.
 * Each new variable is unique in the view of the solver, but copying or cloning the variable
 * produces a copy of the same variable.
 */
@ConsistentCopyVisibility
data class Variable internal constructor(val id: Long) : Comparable<Variable> {

    override fun compareTo(other: Variable): Int = id.compareTo(other.id)

    override fun toString(): String = "Variable(id=$id)"

    fun add(constant: Double): Expression =
        Term.from(this) + constant

    fun add(constant: Float): Expression =
        Term.from(this) + constant.toDouble()

    fun add(other: Variable): Expression =
        Term.from(this) + Term.from(other)

    fun add(term: Term): Expression =
        Term.from(this) + term

    fun add(expression: Expression): Expression =
        Term.from(this) + expression

    fun sub(constant: Double): Expression =
        Term.from(this) - constant

    fun sub(constant: Float): Expression =
        Term.from(this) - constant.toDouble()

    fun sub(other: Variable): Expression =
        Term.from(this) - Term.from(other)

    fun sub(term: Term): Expression =
        Term.from(this) - term

    fun sub(expression: Expression): Expression =
        Term.from(this) - expression

    fun mul(coefficient: Double): Term =
        Term.new(this, coefficient)

    fun mul(coefficient: Float): Term =
        Term.new(this, coefficient.toDouble())

    fun neg(): Term =
        -Term.from(this)

    companion object {
        @OptIn(ExperimentalAtomicApi::class)
        private val nextId = AtomicLong(0)

        /**
         * Produces a new unique variable for use in constraint solving.
         *
         * Each call to [new] returns a variable with a unique ID that will never
         * be reused, even across different solver instances.
         *
         * @return A new unique [Variable].
         */
        @OptIn(ExperimentalAtomicApi::class)
        fun new(): Variable = Variable(nextId.fetchAndAdd(1))

        fun default(): Variable = new()

        /**
         * Creates a variable with a specific ID. For testing purposes only.
         */
        internal fun fromId(id: Long): Variable = Variable(id)
    }

    operator fun plus(constant: Double): Expression = add(constant)
    operator fun plus(constant: Float): Expression = add(constant)
    operator fun plus(other: Variable): Expression = add(other)
    operator fun plus(term: Term): Expression = add(term)
    operator fun plus(expression: Expression): Expression = add(expression)

    operator fun unaryMinus(): Term = neg()

    operator fun minus(constant: Double): Expression = sub(constant)
    operator fun minus(constant: Float): Expression = sub(constant)
    operator fun minus(other: Variable): Expression = sub(other)
    operator fun minus(term: Term): Expression = sub(term)
    operator fun minus(expression: Expression): Expression = sub(expression)

    operator fun times(coefficient: Double): Term = mul(coefficient)
    operator fun times(coefficient: Float): Term = mul(coefficient)

    operator fun div(coefficient: Double): Term = Term.new(this, 1.0 / coefficient)
    operator fun div(coefficient: Float): Term = Term.new(this, 1.0 / coefficient.toDouble())
}

// ============================================================================
// Operator overloading for Variable
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

/** Adds a [Variable] to this expression in place. */
operator fun Expression.plusAssign(variable: Variable) {
    this += Term.from(variable)
}

/** Subtracts a [Variable] from this expression in place. */
operator fun Expression.minusAssign(variable: Variable) {
    this -= Term.from(variable)
}

// ============================================================================
// Conversion extensions for Variable
// ============================================================================

/**
 * Converts this [Variable] to a [Term] with coefficient 1.0.
 *
 * This is equivalent to Rust's `impl From<Variable> for Term`.
 *
 * @return A [Term] representing this variable with coefficient 1.0.
 */
fun Variable.toTerm(): Term = Term.fromVariable(this)

/**
 * Converts this [Variable] to an [Expression].
 *
 * This is equivalent to Rust's `impl From<Variable> for Expression`.
 *
 * @return An [Expression] containing only this variable.
 */
fun Variable.toExpression(): Expression = Expression.fromVariable(this)
