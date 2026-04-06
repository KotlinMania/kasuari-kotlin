// port-lint: source constraint.rs
package kasuari

import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

private class Inner(
    val expression: Expression,
    val strength: Strength,
    val operator: RelationalOperator,
)

/**
 * A constraint, consisting of an equation governed by an expression and a relational operator,
 * and an associated strength.
 */
class Constraint private constructor(
    private val inner: Inner,
    private val id: Long,
) {
    companion object {
        @OptIn(ExperimentalAtomicApi::class)
        private val nextId = AtomicLong(0)

        /**
         * Construct a new constraint from an expression, a relational operator and a strength.
         * This corresponds to the equation `e op 0.0`, e.g. `x + y >= 0.0`. For equations with a
         * non-zero right hand side, subtract it from the equation to give a zero right hand side.
         */
        @OptIn(ExperimentalAtomicApi::class)
        fun new(
            expression: Expression,
            operator: RelationalOperator,
            strength: Strength,
        ): Constraint =
            Constraint(
                Inner(
                    expression = expression,
                    strength = strength,
                    operator = operator,
                ),
                id = nextId.fetchAndAdd(1),
            )
    }

    /** The expression of the left hand side of the constraint equation. */
    fun expr(): Expression =
        inner.expression

    /** The relational operator governing the constraint. */
    fun op(): RelationalOperator =
        inner.operator

    /** The strength of the constraint that the solver will use. */
    fun strength(): Strength =
        inner.strength

    override fun hashCode(): Int =
        id.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Constraint) return false
        return id == other.id
    }
}

/** This is an intermediate type used in the syntactic sugar for specifying constraints. You should not use it directly. */
class PartialConstraint(
    val expression: Expression,
    val relation: WeightedRelation,
) {
    companion object {
        /** Construct a new partial constraint from an expression and a relational operator. */
        fun new(expression: Expression, relation: WeightedRelation): PartialConstraint =
            PartialConstraint(expression = expression, relation = relation)
    }

    infix fun to(rhs: Double): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    infix fun to(rhs: Float): Constraint =
        to(rhs.toDouble())

    infix fun to(rhs: Variable): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    infix fun to(rhs: Term): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    infix fun to(rhs: Expression): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }
}
