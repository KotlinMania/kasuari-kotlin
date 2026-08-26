// port-lint: source relations.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.kasuari

import kotlin.native.HiddenFromObjC

/** The possible relations that a constraint can specify. */
enum class RelationalOperator {
    /** `<=` */
    LessOrEqual,

    /** `==` */
    Equal,

    /** `>=` */
    GreaterOrEqual,

    ;

    /**
     * Render this operator as its relational symbol — `"<="`, `"=="`, or `">="` — for use
     * when printing or logging a constraint.
     */
    override fun toString(): String =
        when (this) {
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
sealed class WeightedRelation(
    val strength: Strength,
) {
    /** `==` */
    class EQ(
        strength: Strength,
    ) : WeightedRelation(strength)

    /** `<=` */
    class LE(
        strength: Strength,
    ) : WeightedRelation(strength)

    /** `>=` */
    class GE(
        strength: Strength,
    ) : WeightedRelation(strength)

    /** Decompose a weighted relation into its underlying operator and strength. */
    @HiddenFromObjC
    fun toOperatorAndStrength(): Pair<RelationalOperator, Strength> =
        when (this) {
            is EQ -> RelationalOperator.Equal to strength
            is LE -> RelationalOperator.LessOrEqual to strength
            is GE -> RelationalOperator.GreaterOrEqual to strength
        }

    companion object {
        /** Decompose a weighted relation into its underlying operator and strength. */
        @HiddenFromObjC
        fun from(relation: WeightedRelation): Pair<RelationalOperator, Strength> =
            relation.toOperatorAndStrength()
    }
}

/**
 * Kotlin-friendly constraint DSL helpers.
 *
 * The upstream uses bitwise-or (`|`) for this sugar, but Kotlin cannot overload `|` for
 * arbitrary types. These `with`/`to` infix functions provide equivalent ergonomics:
 *
 * ```
 * expr with WeightedRelation.EQ(strength) to rhs
 * ```
 *
 * The left-hand side begins the partial constraint with [with]; [PartialConstraint.to]
 * completes it with a right-hand side value.
 */
infix fun Double.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromConstant(this), relation)

infix fun Float.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromConstant(this.toDouble()), relation)

infix fun Variable.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromVariable(this), relation)

infix fun Term.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint.new(Expression.fromTerm(this), relation)

infix fun Expression.with(relation: WeightedRelation): PartialConstraint =
    PartialConstraint.new(this, relation)
