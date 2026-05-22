// port-lint: source relations.rs
package kasuari

/** The possible relations that a constraint can specify. */
enum class RelationalOperator {
    /** `<=` */
    LessOrEqual,

    /** `==` */
    Equal,

    /** `>=` */
    GreaterOrEqual;

    override fun toString(): String = when (this) {
        LessOrEqual -> "<="
        Equal -> "=="
        GreaterOrEqual -> ">="
    }
}

/**
 * This is part of the syntactic sugar used for specifying constraints. This sealed class should
 * be used as part of a constraint expression. See the package documentation on [Kasuari] for
 * more information.
 */
sealed class WeightedRelation(val strength: Strength) {
    /** `==` */
    class EQ(strength: Strength) : WeightedRelation(strength)

    /** `<=` */
    class LE(strength: Strength) : WeightedRelation(strength)

    /** `>=` */
    class GE(strength: Strength) : WeightedRelation(strength)

    /** Decompose a weighted relation into its underlying operator and strength. */
    fun toOperatorAndStrength(): Pair<RelationalOperator, Strength> = when (this) {
        is EQ -> RelationalOperator.Equal to strength
        is LE -> RelationalOperator.LessOrEqual to strength
        is GE -> RelationalOperator.GreaterOrEqual to strength
    }

    companion object {
        /** Decompose a weighted relation into its underlying operator and strength. */
        fun from(relation: WeightedRelation): Pair<RelationalOperator, Strength> =
            relation.toOperatorAndStrength()
    }
}

/** Begin a partial constraint with this constant value as the left-hand side expression. */
infix fun Double.bitor(rhs: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromConstant(this), rhs)

/** Begin a partial constraint with this constant value as the left-hand side expression. */
infix fun Float.bitor(rhs: WeightedRelation): PartialConstraint =
    this.toDouble().bitor(rhs)

/** Begin a partial constraint with this variable as the left-hand side expression. */
infix fun Variable.bitor(rhs: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromVariable(this), rhs)

/** Begin a partial constraint with this term as the left-hand side expression. */
infix fun Term.bitor(rhs: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromTerm(this), rhs)

/** Begin a partial constraint with this expression as the left-hand side expression. */
infix fun Expression.bitor(rhs: WeightedRelation): PartialConstraint =
    PartialConstraint.new(this, rhs)
