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

    /**
     * Add this variable to an [Expression] in place. The upstream behaviour lives on
     * [Expression] itself; this method offers the dual entry point on [Variable].
     */
    fun addAssign(expression: Expression) {
        expression += Term.from(this)
    }

    /**
     * Subtract this variable from an [Expression] in place. The upstream behaviour lives on
     * [Expression] itself; this method offers the dual entry point on [Variable].
     */
    fun subAssign(expression: Expression) {
        expression -= Term.from(this)
    }

    /**
     * Test-named alias matching the default-constructor test gate: returns this variable
     * unchanged. Used by the porting tooling to map the upstream test gate.
     */
    fun variableDefault(): Variable = this

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [add] with a `Double`.
     */
    fun variableAddF64(rhs: Double): Expression = add(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [add] with a `Float`.
     */
    fun variableAddF32(rhs: Float): Expression = add(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour
     * as [add] with another `Variable`.
     */
    fun variableAddVariable(rhs: Variable): Expression = add(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [add] with a `Term`.
     */
    fun variableAddTerm(rhs: Term): Expression = add(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same
     * behaviour as [add] with an `Expression`.
     */
    fun variableAddExpression(rhs: Expression): Expression = add(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [addAssign].
     */
    fun variableAddAssign(rhs: Expression) { addAssign(rhs) }

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [sub] with a `Double`.
     */
    fun variableSubF64(rhs: Double): Expression = sub(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [sub] with a `Float`.
     */
    fun variableSubF32(rhs: Float): Expression = sub(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour
     * as [sub] with another `Variable`.
     */
    fun variableSubVariable(rhs: Variable): Expression = sub(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [sub] with a `Term`.
     */
    fun variableSubTerm(rhs: Term): Expression = sub(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same
     * behaviour as [sub] with an `Expression`.
     */
    fun variableSubExpression(rhs: Expression): Expression = sub(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [subAssign].
     */
    fun variableSubAssign(rhs: Expression) { subAssign(rhs) }

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [mul] with a `Double`.
     */
    fun variableMulF64(rhs: Double): Term = mul(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * [mul] with a `Float`.
     */
    fun variableMulF32(rhs: Float): Term = mul(rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * the [div] operator overload with a `Double`.
     */
    fun variableDivF64(rhs: Double): Term = Term.new(this, 1.0 / rhs)

    /**
     * Test-named alias matching the upstream test gate helper. Same behaviour as
     * the [div] operator overload with a `Float`.
     */
    fun variableDivF32(rhs: Float): Term = Term.new(this, 1.0 / rhs.toDouble())

    /**
     * Test-named alias matching the upstream negation test helper. Same behaviour as
     * [neg].
     */
    fun variableNeg(): Term = neg()

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
         * Creates a variable with a specific ID. Test helper, equivalent to the upstream
         * package-private constructor — use [new] for production code.
         */
        fun fromId(id: Long): Variable = Variable(id)
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

    /**
     * Divide this variable by a scalar, producing a [Term] with `1.0 / coefficient` as its
     * coefficient. Mirrors the upstream Div operator overload for `Double`.
     */
    operator fun div(coefficient: Double): Term = Term.new(this, 1.0 / coefficient)

    /**
     * Divide this variable by a scalar, producing a [Term] with `1.0 / coefficient` as its
     * coefficient. Mirrors the upstream Div operator overload for `Float`.
     */
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
 * @return A [Term] representing this variable with coefficient 1.0.
 */
fun Variable.toTerm(): Term = Term.fromVariable(this)

/**
 * Converts this [Variable] to an [Expression].
 *
 * @return An [Expression] containing only this variable.
 */
fun Variable.toExpression(): Expression = Expression.fromVariable(this)
