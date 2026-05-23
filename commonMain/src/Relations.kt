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

    /**
     * Render this operator as its relational symbol — `"<="`, `"=="`, or `">="` — for use
     * when printing or logging a constraint.
     */
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
