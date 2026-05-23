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
    var coefficient: Double
) {
    /** Negate this term, producing a new [Term] with negated coefficient. */
    fun neg(): Term =
        Term(variable, -coefficient)

    /**
     * Multiply this term by a scalar, mirroring the upstream multiply-by-Double operator on `Term`.
     */
    fun mul(rhs: Double): Term =
        Term(variable, coefficient * rhs)

    /**
     * Multiply this term by a scalar, mirroring the upstream multiply-by-Float operator on `Term`.
     */
    fun mul(rhs: Float): Term =
        Term(variable, coefficient * rhs.toDouble())

    /**
     * Multiply this term's coefficient by a scalar in place, mirroring the upstream in-place multiplication operator that takes a `Double` on `Term`.
     */
    fun mulAssign(rhs: Double) {
        coefficient *= rhs
    }

    /**
     * Multiply this term's coefficient by a scalar in place, mirroring the upstream in-place multiplication operator that takes a `Float` on `Term`.
     */
    fun mulAssign(rhs: Float) {
        coefficient *= rhs.toDouble()
    }

    /**
     * Divide this term by a scalar, mirroring the upstream divide-by-Double operator on `Term`.
     */
    fun div(rhs: Double): Term =
        Term(variable, coefficient / rhs)

    /**
     * Divide this term by a scalar, mirroring the upstream divide-by-Float operator on `Term`.
     */
    fun div(rhs: Float): Term =
        Term(variable, coefficient / rhs.toDouble())

    /**
     * Divide this term's coefficient by a scalar in place, mirroring the upstream in-place division operator that takes a `Double` on `Term`.
     */
    fun divAssign(rhs: Double) {
        coefficient /= rhs
    }

    /**
     * Divide this term's coefficient by a scalar in place, mirroring the upstream in-place division operator that takes a `Float` on `Term`.
     */
    fun divAssign(rhs: Float) {
        coefficient /= rhs.toDouble()
    }

    /**
     * Add a constant to this term, producing an [Expression], mirroring the upstream addition operator that takes a `Double` on `Term`.
     */
    fun add(rhs: Double): Expression =
        Expression.new(listOf(this), rhs)

    /**
     * Add a constant to this term, producing an [Expression], mirroring the upstream addition operator that takes a `Float` on `Term`.
     */
    fun add(rhs: Float): Expression =
        Expression.new(listOf(this), rhs.toDouble())

    /**
     * Add a term to this term, producing an [Expression], mirroring the upstream addition operator that takes a `Term` on `Term`.
     */
    fun add(rhs: Term): Expression =
        Expression.fromTerms(listOf(this, rhs))

    /**
     * Add an [Expression] to this term, producing an [Expression], mirroring the upstream addition operator that takes a `Expression` on `Term`.
     */
    fun add(rhs: Expression): Expression {
        val terms = mutableListOf(this)
        terms.addAll(rhs.terms)
        return Expression(terms, rhs.constant)
    }

    /**
     * Subtract a constant from this term, producing an [Expression], mirroring the upstream subtraction operator that takes a `Double` on `Term`.
     */
    fun sub(rhs: Double): Expression =
        Expression.new(listOf(this), -rhs)

    /**
     * Subtract a constant from this term, producing an [Expression], mirroring the upstream subtraction operator that takes a `Float` on `Term`.
     */
    fun sub(rhs: Float): Expression =
        Expression.new(listOf(this), -rhs.toDouble())

    /**
     * Subtract another term from this term, producing an [Expression], mirroring the upstream subtraction operator that takes a `Term` on `Term`.
     */
    fun sub(rhs: Term): Expression =
        Expression.fromTerms(listOf(this, -rhs))

    /**
     * Subtract an [Expression] from this term, producing an [Expression], mirroring the upstream subtraction operator that takes a `Expression` on `Term`.
     */
    fun sub(rhs: Expression): Expression {
        val negated = -rhs
        val terms = mutableListOf(this)
        terms.addAll(negated.terms)
        return Expression(terms, negated.constant)
    }

    /**
     * Multiply this term by a `Double` scalar. Spelled with the upstream type-suffixed
     * convention so the upstream test gate maps cleanly. Same behaviour as [mul].
     */
    fun mulF64(rhs: Double): Term = mul(rhs)

    /**
     * Multiply this term by a `Float` scalar. Spelled with the upstream type-suffixed
     * convention so the upstream test gate maps cleanly. Same behaviour as [mul].
     */
    fun mulF32(rhs: Float): Term = mul(rhs)

    /**
     * In-place multiplication of this term's coefficient by a `Double`. Spelled with the
     * upstream type-suffixed convention. Same behaviour as [mulAssign].
     */
    fun mulAssignF64(rhs: Double) { mulAssign(rhs) }

    /**
     * In-place multiplication of this term's coefficient by a `Float`. Spelled with the
     * upstream type-suffixed convention. Same behaviour as [mulAssign].
     */
    fun mulAssignF32(rhs: Float) { mulAssign(rhs) }

    /**
     * Divide this term by a `Double` scalar. Spelled with the upstream type-suffixed
     * convention. Same behaviour as [div].
     */
    fun divF64(rhs: Double): Term = div(rhs)

    /**
     * Divide this term by a `Float` scalar. Spelled with the upstream type-suffixed
     * convention. Same behaviour as [div].
     */
    fun divF32(rhs: Float): Term = div(rhs)

    /**
     * In-place division of this term's coefficient by a `Double`. Spelled with the upstream
     * type-suffixed convention. Same behaviour as [divAssign].
     */
    fun divAssignF64(rhs: Double) { divAssign(rhs) }

    /**
     * In-place division of this term's coefficient by a `Float`. Spelled with the upstream
     * type-suffixed convention. Same behaviour as [divAssign].
     */
    fun divAssignF32(rhs: Float) { divAssign(rhs) }

    /**
     * Add a `Double` constant to this term. Spelled with the upstream type-suffixed
     * convention. Same behaviour as [add].
     */
    fun addF64(rhs: Double): Expression = add(rhs)

    /**
     * Add a `Float` constant to this term. Spelled with the upstream type-suffixed
     * convention. Same behaviour as [add].
     */
    fun addF32(rhs: Float): Expression = add(rhs)

    /**
     * Add another [Term] to this term. Spelled with the upstream type-suffixed convention.
     * Same behaviour as [add].
     */
    fun addTerm(rhs: Term): Expression = add(rhs)

    /**
     * Add an [Expression] to this term. Spelled with the upstream type-suffixed convention.
     * Same behaviour as [add].
     */
    fun addExpression(rhs: Expression): Expression = add(rhs)

    /**
     * Subtract a `Double` constant from this term. Spelled with the upstream type-suffixed
     * convention. Same behaviour as [sub].
     */
    fun subF64(rhs: Double): Expression = sub(rhs)

    /**
     * Subtract a `Float` constant from this term. Spelled with the upstream type-suffixed
     * convention. Same behaviour as [sub].
     */
    fun subF32(rhs: Float): Expression = sub(rhs)

    /**
     * Subtract another [Term] from this term. Spelled with the upstream type-suffixed
     * convention. Same behaviour as [sub].
     */
    fun subTerm(rhs: Term): Expression = sub(rhs)

    /**
     * Subtract an [Expression] from this term. Spelled with the upstream type-suffixed
     * convention. Same behaviour as [sub].
     */
    fun subExpression(rhs: Expression): Expression = sub(rhs)

    companion object {
        /**
         * Constructs a new [Term] from a variable and a coefficient.
         *
         * @param variable The variable for this term.
         * @param coefficient The coefficient to multiply the variable by.
         * @return A new [Term].
         */
        fun new(variable: Variable, coefficient: Double): Term =
            Term(variable, coefficient)

        /**
         * Constructs a new [Term] from a variable with a coefficient of 1.0.
         *
         * @param variable The variable for this term.
         * @return A new [Term] with coefficient 1.0.
         */
        fun fromVariable(variable: Variable): Term =
            Term(variable, 1.0)

        /**
         * Converts a [Variable] to a [Term] with coefficient 1.0.
         *
         * @param variable The variable to convert.
         * @return A new [Term] with coefficient 1.0.
         */
        fun from(variable: Variable): Term = fromVariable(variable)
    }
}

// ============================================================================
// Operator overloading for Term
// ============================================================================

/** Multiplies this term by a scalar, producing a new [Term]. */
operator fun Term.times(rhs: Double): Term =
    Term.new(this.variable, this.coefficient * rhs)

/** Multiplies a scalar by this term, producing a new [Term]. */
operator fun Double.times(rhs: Term): Term =
    Term.new(rhs.variable, this * rhs.coefficient)

/** Multiplies this term by a scalar (Float), producing a new [Term]. */
operator fun Term.times(rhs: Float): Term =
    Term.new(this.variable, this.coefficient * rhs.toDouble())

/** Multiplies a scalar (Float) by this term, producing a new [Term]. */
operator fun Float.times(rhs: Term): Term =
    Term.new(rhs.variable, this.toDouble() * rhs.coefficient)

/** Divides this term by a scalar, producing a new [Term]. */
operator fun Term.div(rhs: Double): Term =
    Term.new(this.variable, this.coefficient / rhs)

/** Divides this term by a scalar (Float), producing a new [Term]. */
operator fun Term.div(rhs: Float): Term =
    Term.new(this.variable, this.coefficient / rhs.toDouble())

/** Multiplies this term's coefficient by a scalar in place. */
operator fun Term.timesAssign(rhs: Double) {
    coefficient *= rhs
}

/** Multiplies this term's coefficient by a scalar (Float) in place. */
operator fun Term.timesAssign(rhs: Float) {
    coefficient *= rhs.toDouble()
}

/** Divides this term's coefficient by a scalar in place. */
operator fun Term.divAssign(rhs: Double) {
    coefficient /= rhs
}

/** Divides this term's coefficient by a scalar (Float) in place. */
operator fun Term.divAssign(rhs: Float) {
    coefficient /= rhs.toDouble()
}

/** Negates this term, producing a new [Term] with negated coefficient. */
operator fun Term.unaryMinus(): Term =
    this.neg()

/** Adds a constant to this term, producing an [Expression]. */
operator fun Term.plus(rhs: Double): Expression =
    Expression.new(listOf(this), rhs)

/** Adds a term to this constant, producing an [Expression]. */
operator fun Double.plus(rhs: Term): Expression =
    Expression.new(listOf(rhs), this)

/** Adds a constant (Float) to this term, producing an [Expression]. */
operator fun Term.plus(rhs: Float): Expression =
    Expression.new(listOf(this), rhs.toDouble())

/** Adds a term to this constant (Float), producing an [Expression]. */
operator fun Float.plus(rhs: Term): Expression =
    Expression.new(listOf(rhs), this.toDouble())

/** Adds two terms together, producing an [Expression]. */
operator fun Term.plus(rhs: Term): Expression =
    Expression.fromTerms(listOf(this, rhs))

/** Adds an [Expression] to this term, producing an [Expression]. */
operator fun Term.plus(rhs: Expression): Expression {
    val newTerms = mutableListOf(this)
    newTerms.addAll(rhs.terms)
    return Expression(newTerms, rhs.constant)
}

/** Adds a [Term] to this expression, producing an [Expression]. */
operator fun Expression.plus(rhs: Term): Expression {
    val result = this.copy()
    result.terms.add(rhs)
    return result
}

/** Adds a [Term] to this expression in place. */
operator fun Expression.plusAssign(rhs: Term) {
    this.terms.add(rhs)
}

/**
 * Add a [Term] to this expression in place. Mirrors the upstream in-place addition operator
 * on an `Expression` whose right-hand side is a `Term`.
 */
fun Expression.addAssign(rhs: Term) {
    this.terms.add(rhs)
}

/** Subtracts a constant from this term, producing an [Expression]. */
operator fun Term.minus(rhs: Double): Expression =
    Expression.new(listOf(this), -rhs)

/** Subtracts a term from this constant, producing an [Expression]. */
operator fun Double.minus(rhs: Term): Expression =
    Expression.new(listOf(-rhs), this)

/** Subtracts a constant (Float) from this term, producing an [Expression]. */
operator fun Term.minus(rhs: Float): Expression =
    Expression.new(listOf(this), -rhs.toDouble())

/** Subtracts a term from this constant (Float), producing an [Expression]. */
operator fun Float.minus(rhs: Term): Expression =
    Expression.new(listOf(-rhs), this.toDouble())

/** Subtracts another term from this term, producing an [Expression]. */
operator fun Term.minus(rhs: Term): Expression =
    Expression.fromTerms(listOf(this, -rhs))

/** Subtracts an [Expression] from this term, producing an [Expression]. */
operator fun Term.minus(rhs: Expression): Expression {
    val negated = -rhs
    val newTerms = mutableListOf(this)
    newTerms.addAll(negated.terms)
    return Expression(newTerms, negated.constant)
}

/** Subtracts a [Term] from this expression, producing an [Expression]. */
operator fun Expression.minus(rhs: Term): Expression {
    val result = this.copy()
    result.terms.add(-rhs)
    return result
}

/** Subtracts a [Term] from this expression in place. */
operator fun Expression.minusAssign(rhs: Term) {
    this.terms.add(-rhs)
}

/**
 * Subtract a [Term] from this expression in place. Mirrors the upstream in-place
 * subtraction operator on an `Expression` whose right-hand side is a `Term`.
 */
fun Expression.subAssign(rhs: Term) {
    this.terms.add(-rhs)
}

// ============================================================================
// Conversion extensions for Term
// ============================================================================

/**
 * Converts this [Term] to an [Expression].
 *
 * @return An [Expression] containing only this term.
 */
fun Term.toExpression(): Expression = Expression.fromTerm(this)
