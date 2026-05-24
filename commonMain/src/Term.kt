// port-lint: source term.rs
package kasuari

/**
 * A variable and a coefficient to multiply that variable by.
 *
 * This is a sub-expression in a constraint equation that represents:
 *
 * ```
 * term = coefficient × variable
 * ```
 *
 * Terms are the building blocks of [Expression]s. A term represents a single
 * variable multiplied by a coefficient. Multiple terms can be combined with
 * constants to form expressions.
 *
 * ## Creating Terms
 *
 * ```kotlin
 * val x = Variable.new()
 *
 * // From a variable (coefficient = 1.0)
 * val term1 = Term.fromVariable(x)
 *
 * // With explicit coefficient
 * val term2 = Term.new(x, 2.5)
 *
 * // Using operators
 * val term3 = x * 2.5    // 2.5x
 * val term4 = 3.0 * x    // 3x
 * val term5 = x / 2.0    // 0.5x
 * ```
 *
 * ## Building Expressions from Terms
 *
 * ```kotlin
 * val x = Variable.new()
 * val y = Variable.new()
 *
 * val term1 = 2.0 * x
 * val term2 = 3.0 * y
 *
 * // Combine terms into expressions
 * val expr1 = term1 + term2           // 2x + 3y
 * val expr2 = term1 + 5.0             // 2x + 5
 * val expr3 = term1 - term2           // 2x - 3y
 * ```
 *
 * @property variable The variable this term contains.
 * @property coefficient The coefficient to multiply the variable by.
 * @see Variable
 * @see Expression
 */
data class Term(
    val variable: Variable,
    var coefficient: Double,
) {
    companion object {
        /**
         * Constructs a new [Term] from a variable and a coefficient.
         */
        fun new(variable: Variable, coefficient: Double): Term =
            Term(variable, coefficient)

        /**
         * Constructs a new [Term] from a variable with a coefficient of 1.0.
         */
        fun fromVariable(variable: Variable): Term =
            Term(variable, 1.0)

        /**
         * Converts a [Variable] to a [Term] with coefficient 1.0.
         */
        fun from(variable: Variable): Term = fromVariable(variable)
    }
}

// ============================================================================
// Operator overloading for Term
// ============================================================================

operator fun Term.times(rhs: Double): Term =
    Term.new(variable, coefficient * rhs)

operator fun Term.times(rhs: Float): Term =
    Term.new(variable, coefficient * rhs.toDouble())

operator fun Double.times(rhs: Term): Term =
    Term.new(rhs.variable, this * rhs.coefficient)

operator fun Float.times(rhs: Term): Term =
    Term.new(rhs.variable, this.toDouble() * rhs.coefficient)

operator fun Term.div(rhs: Double): Term =
    Term.new(variable, coefficient / rhs)

operator fun Term.div(rhs: Float): Term =
    Term.new(variable, coefficient / rhs.toDouble())

operator fun Term.timesAssign(rhs: Double) {
    coefficient *= rhs
}

operator fun Term.timesAssign(rhs: Float) {
    coefficient *= rhs.toDouble()
}

operator fun Term.divAssign(rhs: Double) {
    coefficient /= rhs
}

operator fun Term.divAssign(rhs: Float) {
    coefficient /= rhs.toDouble()
}

operator fun Term.unaryMinus(): Term =
    Term(variable, -coefficient)

operator fun Term.plus(rhs: Double): Expression =
    Expression.new(listOf(this), rhs)

operator fun Term.plus(rhs: Float): Expression =
    Expression.new(listOf(this), rhs.toDouble())

operator fun Double.plus(rhs: Term): Expression =
    Expression.new(listOf(rhs), this)

operator fun Float.plus(rhs: Term): Expression =
    Expression.new(listOf(rhs), this.toDouble())

operator fun Term.plus(rhs: Term): Expression =
    Expression.fromTerms(listOf(this, rhs))

operator fun Term.plus(rhs: Expression): Expression {
    val newTerms = mutableListOf(this)
    newTerms.addAll(rhs.terms)
    return Expression(newTerms, rhs.constant)
}

operator fun Expression.plus(rhs: Term): Expression {
    val result = this.copy()
    result.terms = result.terms + rhs
    return result
}

operator fun Expression.plusAssign(rhs: Term) {
    terms = terms + rhs
}

operator fun Term.minus(rhs: Double): Expression =
    Expression.new(listOf(this), -rhs)

operator fun Term.minus(rhs: Float): Expression =
    Expression.new(listOf(this), -rhs.toDouble())

operator fun Double.minus(rhs: Term): Expression =
    Expression.new(listOf(-rhs), this)

operator fun Float.minus(rhs: Term): Expression =
    Expression.new(listOf(-rhs), this.toDouble())

operator fun Term.minus(rhs: Term): Expression =
    Expression.fromTerms(listOf(this, -rhs))

operator fun Term.minus(rhs: Expression): Expression {
    val negated = -rhs
    val newTerms = mutableListOf(this)
    newTerms.addAll(negated.terms)
    return Expression(newTerms, negated.constant)
}

operator fun Expression.minus(rhs: Term): Expression {
    val result = this.copy()
    result.terms = result.terms + (-rhs)
    return result
}

operator fun Expression.minusAssign(rhs: Term) {
    terms = terms + (-rhs)
}

// ============================================================================
// Conversion extensions for Term
// ============================================================================

/** Converts this [Term] to an [Expression]. */
fun Term.toExpression(): Expression = Expression.fromTerm(this)
