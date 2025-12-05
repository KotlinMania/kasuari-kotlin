package kasuari

/**
 * The possible relations that a constraint can specify.
 */
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
 * This is part of the syntactic sugar used for specifying constraints. This enum should be used as
 * part of a constraint expression. See the module documentation for more information.
 */
sealed class WeightedRelation {
    abstract val strength: Strength

    /** `==` */
    data class EQ(override val strength: Strength) : WeightedRelation()
    /** `<=` */
    data class LE(override val strength: Strength) : WeightedRelation()
    /** `>=` */
    data class GE(override val strength: Strength) : WeightedRelation()

    fun toOperatorAndStrength(): Pair<RelationalOperator, Strength> = when (this) {
        is EQ -> RelationalOperator.Equal to strength
        is LE -> RelationalOperator.LessOrEqual to strength
        is GE -> RelationalOperator.GreaterOrEqual to strength
    }
}

// Kotlin doesn't have operator overloading for BitOr on arbitrary types like Rust.
// Instead, we provide infix functions for building constraints.
// Usage: expression infixOp WeightedRelation.EQ(strength) infixOp otherExpression

/**
 * Create a partial constraint from a Double and a WeightedRelation.
 */
infix fun Double.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint(Expression.fromConstant(this), relation)

/**
 * Create a partial constraint from a Float and a WeightedRelation.
 */
infix fun Float.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint(Expression.fromConstant(this.toDouble()), relation)

/**
 * Create a partial constraint from a Variable and a WeightedRelation.
 */
infix fun Variable.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint(Expression.fromVariable(this), relation)

/**
 * Create a partial constraint from a Term and a WeightedRelation.
 */
infix fun Term.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint(Expression.fromTerm(this), relation)

/**
 * Create a partial constraint from an Expression and a WeightedRelation.
 */
infix fun Expression.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint(this, relation)
