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

    /**
     * Compute a hash for this constraint based on its identity, matching the upstream identity-based hash that hashes the inner pointer.
     */
    fun hash(): Int =
        id.hashCode()

    /**
     * Test whether two constraints share the same underlying identity, matching the upstream identity-based equality that compares the inner pointers.
     */
    fun eq(other: Constraint): Boolean =
        id == other.id

    override fun hashCode(): Int =
        hash()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Constraint) return false
        return eq(other)
    }
}

/**
 * This is an intermediate type used in the syntactic sugar for specifying constraints. You
 * should not use it directly.
 */
class PartialConstraint(
    val expression: Expression,
    val relation: WeightedRelation,
) {
    companion object {
        /** Construct a new partial constraint from an expression and a relational operator. */
        fun new(expression: Expression, relation: WeightedRelation): PartialConstraint =
            PartialConstraint(expression = expression, relation = relation)
    }

    /**
     * Complete the partial constraint by subtracting the given constant from the left-hand
     * side expression and constructing a [Constraint] with the operator and strength carried
     * by this partial constraint.
     */
    infix fun to(rhs: Double): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    /**
     * Complete the partial constraint by subtracting the given constant from the left-hand
     * side expression and constructing a [Constraint] with the operator and strength carried
     * by this partial constraint.
     */
    infix fun to(rhs: Float): Constraint =
        to(rhs.toDouble())

    /**
     * Complete the partial constraint by subtracting the given variable from the left-hand
     * side expression and constructing a [Constraint] with the operator and strength carried
     * by this partial constraint.
     */
    infix fun to(rhs: Variable): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    /**
     * Complete the partial constraint by subtracting the given term from the left-hand side
     * expression and constructing a [Constraint] with the operator and strength carried by
     * this partial constraint.
     */
    infix fun to(rhs: Term): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    /**
     * Complete the partial constraint by subtracting the given expression from the left-hand
     * side expression and constructing a [Constraint] with the operator and strength carried
     * by this partial constraint.
     */
    infix fun to(rhs: Expression): Constraint {
        val (operator, strength) = relation.toOperatorAndStrength()
        return Constraint.new(expression - rhs, operator, strength)
    }

    /**
     * Complete the partial constraint with a constant on the right-hand side, mirroring the upstream BitOr operator that takes a `Double` on `PartialConstraint`. Equivalent to [to].
     */
    infix fun bitor(rhs: Double): Constraint =
        this to rhs

    /**
     * Complete the partial constraint with a constant on the right-hand side, mirroring the upstream BitOr operator that takes a `Float` on `PartialConstraint`. Equivalent to [to].
     */
    infix fun bitor(rhs: Float): Constraint =
        this to rhs

    /**
     * Complete the partial constraint with a variable on the right-hand side, mirroring the upstream BitOr operator that takes a `Variable` on `PartialConstraint`. Equivalent to [to].
     */
    infix fun bitor(rhs: Variable): Constraint =
        this to rhs

    /**
     * Complete the partial constraint with a term on the right-hand side, mirroring the upstream BitOr operator that takes a `Term` on `PartialConstraint`. Equivalent to [to].
     */
    infix fun bitor(rhs: Term): Constraint =
        this to rhs

    /**
     * Complete the partial constraint with an expression on the right-hand side, mirroring
     * the upstream BitOr operator that takes an `Expression` on `PartialConstraint`.
     * Equivalent to [to].
     */
    infix fun bitor(rhs: Expression): Constraint =
        this to rhs
}
