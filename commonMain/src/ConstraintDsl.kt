// port-lint: ignore — Kotlin-side DSL helpers; no kasuari Rust source file.
package kasuari

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
