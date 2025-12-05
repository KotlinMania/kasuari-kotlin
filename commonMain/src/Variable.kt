package kasuari

import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Identifies a variable for the constraint solver.
 * Each new variable is unique in the view of the solver, but copying or cloning the variable
 * produces a copy of the same variable.
 */
data class Variable internal constructor(val id: Long) : Comparable<Variable> {

    override fun compareTo(other: Variable): Int = id.compareTo(other.id)

    companion object {
        @OptIn(ExperimentalAtomicApi::class)
        private val nextId = AtomicLong(0)

        /**
         * Produces a new unique variable for use in constraint solving.
         */
        @OptIn(ExperimentalAtomicApi::class)
        fun new(): Variable = Variable(nextId.fetchAndAdd(1))

        // For testing
        internal fun fromId(id: Long): Variable = Variable(id)
    }
}

// ============================================================================
// Operator overloading for Variable
// ============================================================================

// Variable + Double -> Expression
operator fun Variable.plus(constant: Double): Expression = Term.from(this) + constant
operator fun Double.plus(variable: Variable): Expression = Term.from(variable) + this

// Variable + Float -> Expression
operator fun Variable.plus(constant: Float): Expression = Term.from(this) + constant.toDouble()
operator fun Float.plus(variable: Variable): Expression = Term.from(variable) + this.toDouble()

// Variable + Variable -> Expression
operator fun Variable.plus(other: Variable): Expression = Term.from(this) + Term.from(other)

// Variable + Term -> Expression
operator fun Variable.plus(term: Term): Expression = Term.from(this) + term
operator fun Term.plus(variable: Variable): Expression = this + Term.from(variable)

// Variable + Expression -> Expression
operator fun Variable.plus(expression: Expression): Expression = Term.from(this) + expression
operator fun Expression.plus(variable: Variable): Expression = this + Term.from(variable)

// -Variable -> Term
operator fun Variable.unaryMinus(): Term = -Term.from(this)

// Variable - Double -> Expression
operator fun Variable.minus(constant: Double): Expression = Term.from(this) - constant
operator fun Double.minus(variable: Variable): Expression = this - Term.from(variable)

// Variable - Float -> Expression
operator fun Variable.minus(constant: Float): Expression = Term.from(this) - constant.toDouble()
operator fun Float.minus(variable: Variable): Expression = this.toDouble() - Term.from(variable)

// Variable - Variable -> Expression
operator fun Variable.minus(other: Variable): Expression = Term.from(this) - Term.from(other)

// Variable - Term -> Expression
operator fun Variable.minus(term: Term): Expression = Term.from(this) - term
operator fun Term.minus(variable: Variable): Expression = this - Term.from(variable)

// Variable - Expression -> Expression
operator fun Variable.minus(expression: Expression): Expression = Term.from(this) - expression
operator fun Expression.minus(variable: Variable): Expression = this - Term.from(variable)

// Variable * Double -> Term
operator fun Variable.times(coefficient: Double): Term = Term(this, coefficient)
operator fun Double.times(variable: Variable): Term = Term(variable, this)

// Variable * Float -> Term
operator fun Variable.times(coefficient: Float): Term = Term(this, coefficient.toDouble())
operator fun Float.times(variable: Variable): Term = Term(variable, this.toDouble())

// Variable / Double -> Term
operator fun Variable.div(coefficient: Double): Term = Term(this, 1.0 / coefficient)
operator fun Variable.div(coefficient: Float): Term = Term(this, 1.0 / coefficient.toDouble())

// Expression += Variable
operator fun Expression.plusAssign(variable: Variable) {
    this += Term.from(variable)
}

// Expression -= Variable
operator fun Expression.minusAssign(variable: Variable) {
    this -= Term.from(variable)
}
