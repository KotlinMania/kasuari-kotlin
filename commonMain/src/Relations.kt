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
 * This is part of the syntactic sugar used for specifying constraints. This should be used as
 * part of a constraint expression.
 */
sealed class WeightedRelation(val strength: Strength) {
    /** `==` */
    class EQ(strength: Strength) : WeightedRelation(strength)

    /** `<=` */
    class LE(strength: Strength) : WeightedRelation(strength)

    /** `>=` */
    class GE(strength: Strength) : WeightedRelation(strength)

    fun toOperatorAndStrength(): Pair<RelationalOperator, Strength> = when (this) {
        is EQ -> RelationalOperator.Equal to strength
        is LE -> RelationalOperator.LessOrEqual to strength
        is GE -> RelationalOperator.GreaterOrEqual to strength
    }

    companion object {
        fun from(relation: WeightedRelation): Pair<RelationalOperator, Strength> =
            relation.toOperatorAndStrength()
    }
}

infix fun Double.bitor(rhs: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromConstant(this), rhs)

infix fun Float.bitor(rhs: WeightedRelation): PartialConstraint =
    this.toDouble().bitor(rhs)

infix fun Variable.bitor(rhs: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromVariable(this), rhs)

infix fun Term.bitor(rhs: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromTerm(this), rhs)

infix fun Expression.bitor(rhs: WeightedRelation): PartialConstraint =
    PartialConstraint.new(this, rhs)
