package kasuari

/**
 * An expression that can be the left hand or right hand side of a constraint equation.
 *
 * It is a linear combination of variables, i.e., a sum of variables weighted by coefficients, plus
 * an optional constant.
 *
 * ```
 * expression = term_1 + term_2 + ... + term_n + constant
 * ```
 */
data class Expression(
    /** The terms in the expression. */
    val terms: MutableList<Term>,
    /** The constant in the expression. */
    var constant: Double
) {
    /**
     * Create a copy of this expression with independent mutable terms list.
     */
    fun copy(): Expression = Expression(terms.toMutableList(), constant)

    companion object {
        /**
         * Create a new Expression.
         *
         * ```
         * expression = term_1 + term_2 + ... + term_n + constant
         * ```
         */
        fun new(terms: List<Term>, constant: Double): Expression =
            Expression(terms.toMutableList(), constant)

        /**
         * Constructs an expression that represents a constant without any terms
         *
         * ```
         * expression = constant
         * ```
         */
        fun fromConstant(constant: Double): Expression =
            Expression(mutableListOf(), constant)

        /**
         * Constructs an expression from a single term.
         *
         * ```
         * expression = term
         * ```
         */
        fun fromTerm(term: Term): Expression =
            Expression(mutableListOf(term), 0.0)

        /**
         * Constructs an expression from terms
         *
         * ```
         * expression = term_1 + term_2 + ... + term_n
         * ```
         */
        fun fromTerms(terms: List<Term>): Expression =
            Expression(terms.toMutableList(), 0.0)

        /**
         * Constructs an expression from a variable
         *
         * ```
         * expression = variable
         * ```
         */
        fun fromVariable(variable: Variable): Expression =
            Expression(mutableListOf(Term.fromVariable(variable)), 0.0)
    }
}

// ============================================================================
// Operator overloading for Expression
// ============================================================================

// -Expression -> Expression
operator fun Expression.unaryMinus(): Expression =
    Expression(
        terms.map { -it }.toMutableList(),
        if (constant == 0.0) 0.0 else -constant  // Normalize -0.0 to 0.0
    )

// Expression * Double -> Expression
operator fun Expression.times(rhs: Double): Expression {
    val result = this.copy()
    result.constant *= rhs
    for (i in result.terms.indices) {
        result.terms[i] = result.terms[i] * rhs
    }
    return result
}

// Double * Expression -> Expression
operator fun Double.times(rhs: Expression): Expression = rhs * this

// Expression * Float -> Expression
operator fun Expression.times(rhs: Float): Expression = this * rhs.toDouble()

// Float * Expression -> Expression
operator fun Float.times(rhs: Expression): Expression = rhs * this.toDouble()

// Expression / Double -> Expression
operator fun Expression.div(rhs: Double): Expression {
    val result = this.copy()
    result.constant /= rhs
    for (i in result.terms.indices) {
        result.terms[i] = result.terms[i] / rhs
    }
    return result
}

// Expression / Float -> Expression
operator fun Expression.div(rhs: Float): Expression = this / rhs.toDouble()

// Expression + Double -> Expression
operator fun Expression.plus(rhs: Double): Expression {
    val result = this.copy()
    result.constant += rhs
    return result
}

// Double + Expression -> Expression
operator fun Double.plus(rhs: Expression): Expression {
    val result = rhs.copy()
    result.constant += this
    return result
}

// Expression + Float -> Expression
operator fun Expression.plus(rhs: Float): Expression = this + rhs.toDouble()

// Float + Expression -> Expression
operator fun Float.plus(rhs: Expression): Expression = this.toDouble() + rhs

// Expression + Expression -> Expression
operator fun Expression.plus(rhs: Expression): Expression {
    val result = this.copy()
    result.terms.addAll(rhs.terms)
    result.constant += rhs.constant
    return result
}

// Expression - Double -> Expression
operator fun Expression.minus(rhs: Double): Expression {
    val result = this.copy()
    result.constant -= rhs
    return result
}

// Double - Expression -> Expression
operator fun Double.minus(rhs: Expression): Expression {
    val negated = -rhs
    negated.constant += this
    return negated
}

// Expression - Float -> Expression
operator fun Expression.minus(rhs: Float): Expression = this - rhs.toDouble()

// Float - Expression -> Expression
operator fun Float.minus(rhs: Expression): Expression = this.toDouble() - rhs

// Expression - Expression -> Expression
operator fun Expression.minus(rhs: Expression): Expression {
    val result = this.copy()
    val negated = -rhs
    result.terms.addAll(negated.terms)
    result.constant += negated.constant
    return result
}
