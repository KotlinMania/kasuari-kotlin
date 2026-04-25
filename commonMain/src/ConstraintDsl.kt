package kasuari

/**
 * Kotlin-friendly constraint DSL helpers.
 *
 * Rust uses `BitOr<WeightedRelation>` (`|`) for this sugar, but Kotlin cannot overload `|` for
 * arbitrary types. These `with` infix functions provide equivalent ergonomics:
 *
 * `expr with WeightedRelation.EQ(strength) to rhs`
 */
infix fun Double.with(relation: WeightedRelation): PartialConstraint =
    this.bitor(relation)

infix fun Float.with(relation: WeightedRelation): PartialConstraint =
    this.bitor(relation)

infix fun Variable.with(relation: WeightedRelation): PartialConstraint =
    this.bitor(relation)

infix fun Term.with(relation: WeightedRelation): PartialConstraint =
    this.bitor(relation)

infix fun Expression.with(relation: WeightedRelation): PartialConstraint =
    this.bitor(relation)

