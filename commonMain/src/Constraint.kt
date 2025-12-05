package kasuari

/**
 * A constraint, consisting of an equation governed by an expression and a relational operator,
 * and an associated strength.
 *
 * Constraints are compared by identity (reference equality), not by value.
 */
class Constraint private constructor(
    private val expression: Expression,
    private val operator: RelationalOperator,
    private val strength: Strength,
    /** Unique ID for identity-based equality */
    private val id: Long
) {
    companion object {
        private var nextId: Long = 0

        /**
         * Construct a new constraint from an expression, a relational operator and a strength.
         * This corresponds to the equation `e op 0.0`, e.g. `x + y >= 0.0`. For equations with a
         * non-zero right hand side, subtract it from the equation to give a zero right hand side.
         */
        fun new(
            expression: Expression,
            operator: RelationalOperator,
            strength: Strength
        ): Constraint = Constraint(expression, operator, strength, nextId++)
    }

    /** The expression of the left hand side of the constraint equation. */
    fun expr(): Expression = expression

    /** The relational operator governing the constraint. */
    fun op(): RelationalOperator = operator

    /** The strength of the constraint that the solver will use. */
    fun strength(): Strength = strength

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Constraint) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String =
        "Constraint(expression=$expression, operator=$operator, strength=$strength)"
}

/**
 * This is an intermediate type used in the syntactic sugar for specifying constraints. You should
 * not use it directly.
 */
class PartialConstraint(
    val expression: Expression,
    val relation: WeightedRelation
) {
    /**
     * Complete the constraint with a Double right-hand side.
     * Usage: partialConstraint to 5.0
     */
    infix fun to(rhs: Double): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    /**
     * Complete the constraint with a Float right-hand side.
     * Usage: partialConstraint to 5.0f
     */
    infix fun to(rhs: Float): Constraint = to(rhs.toDouble())

    /**
     * Complete the constraint with a Variable right-hand side.
     * Usage: partialConstraint to variable
     */
    infix fun to(rhs: Variable): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    /**
     * Complete the constraint with a Term right-hand side.
     * Usage: partialConstraint to term
     */
    infix fun to(rhs: Term): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    /**
     * Complete the constraint with an Expression right-hand side.
     * Usage: partialConstraint to expression
     */
    infix fun to(rhs: Expression): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }
}
