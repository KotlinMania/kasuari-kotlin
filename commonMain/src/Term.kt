package kasuari

/**
 * A variable and a coefficient to multiply that variable by.
 *
 * This is a sub-expression in a constraint equation that represents:
 *
 * ```
 * term = coefficient * variable
 * ```
 */
data class Term(
    val variable: Variable,
    var coefficient: Double
) {
    companion object {
        /**
         * Construct a new Term from a variable and a coefficient.
         */
        fun new(variable: Variable, coefficient: Double): Term =
            Term(variable, coefficient)

        /**
         * Construct a new Term from a variable with a coefficient of 1.0.
         */
        fun fromVariable(variable: Variable): Term =
            Term(variable, 1.0)

        /**
         * Convert a Variable to a Term with coefficient 1.0.
         */
        fun from(variable: Variable): Term = fromVariable(variable)
    }
}

// ============================================================================
// Operator overloading for Term
// ============================================================================

// Term * Double -> Term
operator fun Term.times(rhs: Double): Term =
    Term.new(this.variable, this.coefficient * rhs)

// Double * Term -> Term
operator fun Double.times(rhs: Term): Term =
    Term.new(rhs.variable, this * rhs.coefficient)

// Term * Float -> Term
operator fun Term.times(rhs: Float): Term =
    Term.new(this.variable, this.coefficient * rhs.toDouble())

// Float * Term -> Term
operator fun Float.times(rhs: Term): Term =
    Term.new(rhs.variable, this.toDouble() * rhs.coefficient)

// Term / Double -> Term
operator fun Term.div(rhs: Double): Term =
    Term.new(this.variable, this.coefficient / rhs)

// Term / Float -> Term
operator fun Term.div(rhs: Float): Term =
    Term.new(this.variable, this.coefficient / rhs.toDouble())

// Term *= Double
operator fun Term.timesAssign(rhs: Double) {
    coefficient *= rhs
}

// Term *= Float
operator fun Term.timesAssign(rhs: Float) {
    coefficient *= rhs.toDouble()
}

// Term /= Double
operator fun Term.divAssign(rhs: Double) {
    coefficient /= rhs
}

// Term /= Float
operator fun Term.divAssign(rhs: Float) {
    coefficient /= rhs.toDouble()
}

// -Term -> Term
operator fun Term.unaryMinus(): Term =
    Term(this.variable, -this.coefficient)

// Term + Double -> Expression
operator fun Term.plus(rhs: Double): Expression =
    Expression.new(listOf(this), rhs)

// Double + Term -> Expression
operator fun Double.plus(rhs: Term): Expression =
    Expression.new(listOf(rhs), this)

// Term + Float -> Expression
operator fun Term.plus(rhs: Float): Expression =
    Expression.new(listOf(this), rhs.toDouble())

// Float + Term -> Expression
operator fun Float.plus(rhs: Term): Expression =
    Expression.new(listOf(rhs), this.toDouble())

// Term + Term -> Expression
operator fun Term.plus(rhs: Term): Expression =
    Expression.fromTerms(listOf(this, rhs))

// Term + Expression -> Expression
operator fun Term.plus(rhs: Expression): Expression {
    val newTerms = mutableListOf(this)
    newTerms.addAll(rhs.terms)
    return Expression(newTerms, rhs.constant)
}

// Expression + Term -> Expression
operator fun Expression.plus(rhs: Term): Expression {
    val result = this.copy()
    result.terms.add(rhs)
    return result
}

// Expression += Term
operator fun Expression.plusAssign(rhs: Term) {
    this.terms.add(rhs)
}

// Term - Double -> Expression
operator fun Term.minus(rhs: Double): Expression =
    Expression.new(listOf(this), -rhs)

// Double - Term -> Expression
operator fun Double.minus(rhs: Term): Expression =
    Expression.new(listOf(-rhs), this)

// Term - Float -> Expression
operator fun Term.minus(rhs: Float): Expression =
    Expression.new(listOf(this), -rhs.toDouble())

// Float - Term -> Expression
operator fun Float.minus(rhs: Term): Expression =
    Expression.new(listOf(-rhs), this.toDouble())

// Term - Term -> Expression
operator fun Term.minus(rhs: Term): Expression =
    Expression.fromTerms(listOf(this, -rhs))

// Term - Expression -> Expression
operator fun Term.minus(rhs: Expression): Expression {
    val negated = -rhs
    val newTerms = mutableListOf(this)
    newTerms.addAll(negated.terms)
    return Expression(newTerms, negated.constant)
}

// Expression - Term -> Expression
operator fun Expression.minus(rhs: Term): Expression {
    val result = this.copy()
    result.terms.add(-rhs)
    return result
}

// Expression -= Term
operator fun Expression.minusAssign(rhs: Term) {
    this.terms.add(-rhs)
}
