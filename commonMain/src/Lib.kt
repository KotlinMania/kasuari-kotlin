// port-lint: source lib.rs
//
// This file is the Kotlin counterpart to upstream `lib.rs`, which in Rust is
// the crate root and only carries (a) crate-level documentation and (b) public
// re-exports of the symbols declared in sibling modules. Per the workspace
// porting rule, crate-root re-exports are NOT minted as Kotlin typealiases —
// callers import the original symbols (Solver, Variable, etc.) directly from
// the kasuari package. This file therefore exists as a doc-only ledger that
// carries the translated crate-level tutorial, attached to the [Kasuari]
// documentation object below.
package kasuari

/**
 * This package contains an implementation of the Cassowary constraint solving algorithm, based
 * upon the work by G.J. Badros et al. in 2001.
 *
 * This algorithm is designed primarily for use constraining elements in user interfaces.
 * Constraints are linear combinations of the problem variables. The notable features of
 * Cassowary that make it ideal for user interfaces are that it is incremental (i.e. you can add
 * and remove constraints at runtime and it will perform the minimum work to update the result)
 * and that the constraints can be violated if necessary, with the order in which they are
 * violated specified by setting a "strength" for each constraint.
 *
 * This allows the solution to gracefully degrade, which is useful for when a user interface
 * needs to compromise on its constraints in order to still be able to display something.
 *
 * ## Constraint syntax
 *
 * This package aims to provide syntax for describing linear constraints as naturally as
 * possible, within the limitations of Kotlin's type system.
 *
 * Generally you can write constraints as you would naturally; the relational operator
 * (greater-than, less-than, equals) is replaced with an instance of the [WeightedRelation]
 * sealed class, combined with the left-hand side via the `with` infix function and completed
 * with the right-hand side via the `to` infix function.
 *
 * For example, for the constraint `(a + b) * 2 + c >= d + 1` with strength `s`, the Kotlin
 * equivalent is:
 *
 * ```kotlin
 * ((a + b) * 2.0 + c) with WeightedRelation.GE(s) to (d + 1.0)
 * ```
 *
 * # A simple example
 *
 * Imagine a layout consisting of two elements laid out horizontally.
 *
 * For small window widths the elements should compress to fit, but if there is enough space
 * they should display at their preferred widths. The first element will align to the left, and
 * the second to the right. For this example we will ignore vertical layout.
 *
 * First we need to include the relevant parts of the `kasuari` package:
 *
 * ```kotlin
 * import kasuari.Solver
 * import kasuari.Strength
 * import kasuari.Variable
 * import kasuari.WeightedRelation
 * ```
 *
 * And we'll construct some conveniences for pretty printing (which should hopefully be
 * self-explanatory):
 *
 * ```kotlin
 * val names = mutableMapOf<Variable, String>()
 *
 * fun printChanges(names: Map<Variable, String>, changes: List<Pair<Variable, Double>>) {
 *     println("Changes:")
 *     for ((variable, value) in changes) {
 *         println("${names[variable]}: $value")
 *     }
 * }
 * ```
 *
 * Let's define the variables required — the left and right edges of the elements, and the
 * width of the window.
 *
 * ```kotlin
 * val windowWidth = Variable.new()
 * names[windowWidth] = "windowWidth"
 *
 * data class Element(val left: Variable, val right: Variable)
 *
 * val box1 = Element(left = Variable.new(), right = Variable.new())
 * names[box1.left] = "box1.left"
 * names[box1.right] = "box1.right"
 *
 * val box2 = Element(left = Variable.new(), right = Variable.new())
 * names[box2.left] = "box2.left"
 * names[box2.right] = "box2.right"
 * ```
 *
 * Now to set up the solver and constraints.
 *
 * ```kotlin
 * val solver = Solver.new()
 * solver.addConstraints(
 *     listOf(
 *         windowWidth with WeightedRelation.GE(Strength.REQUIRED) to 0.0,            // positive window width
 *         box1.left   with WeightedRelation.EQ(Strength.REQUIRED) to 0.0,            // left align
 *         box2.right  with WeightedRelation.EQ(Strength.REQUIRED) to windowWidth,    // right align
 *         box2.left   with WeightedRelation.GE(Strength.REQUIRED) to box1.right,     // no overlap
 *         // positive widths
 *         box1.left   with WeightedRelation.LE(Strength.REQUIRED) to box1.right,
 *         box2.left   with WeightedRelation.LE(Strength.REQUIRED) to box2.right,
 *         // preferred widths:
 *         (box1.right - box1.left) with WeightedRelation.EQ(Strength.WEAK) to 50.0,
 *         (box2.right - box2.left) with WeightedRelation.EQ(Strength.WEAK) to 100.0,
 *     )
 * )
 * ```
 *
 * The window width is currently free to take any positive value.
 *
 * Let's constrain it to a particular value. Since for this example we will repeatedly change
 * the window width, it is most efficient to use an "edit variable", instead of repeatedly
 * removing and adding constraints (note that for efficiency reasons we cannot edit a normal
 * constraint that has been added to the solver).
 *
 * ```kotlin
 * solver.addEditVariable(windowWidth, Strength.STRONG)
 * solver.suggestValue(windowWidth, 300.0)
 * ```
 *
 * This value of 300 is enough to fit both boxes in with room to spare, so let's check that
 * this is the case.
 *
 * We can fetch a list of changes to the values of variables in the solver. Using the pretty
 * printer defined earlier we can see what values our variables now hold.
 *
 * ```kotlin
 * printChanges(names, solver.fetchChanges())
 * ```
 *
 * This should print (in a possibly different order):
 *
 * ```text
 * Changes:
 * windowWidth: 300
 * box1.right: 50
 * box2.left: 200
 * box2.right: 300
 * ```
 *
 * Note that the value of `box1.left` is not mentioned. This is because [Solver.fetchChanges]
 * only lists *changes* to variables, and since each variable starts in the solver with a value
 * of zero, any values that have not changed from zero will not be reported.
 *
 * Now let's try compressing the window so that the boxes can't take up their preferred widths.
 *
 * ```kotlin
 * solver.suggestValue(windowWidth, 75.0)
 * printChanges(names, solver.fetchChanges())
 * ```
 *
 * Now the solver can't satisfy all of the constraints.
 *
 * It will pick at least one of the weakest constraints to violate. In this case it will be one
 * or both of the preferred widths. For efficiency reasons this is picked nondeterministically,
 * so there are two possible results. This could be
 *
 * ```text
 * Changes:
 * windowWidth: 75
 * box1.right: 0
 * box2.left: 0
 * box2.right: 75
 * ```
 *
 * or
 *
 * ```text
 * Changes:
 * windowWidth: 75
 * box2.left: 50
 * box2.right: 75
 * ```
 *
 * Due to the nature of the algorithm, "in-between" solutions, although just as valid, are not
 * picked.
 *
 * In a user interface this is not likely a result we would prefer.
 *
 * The solution is to add another constraint to control the behaviour when the preferred widths
 * cannot both be satisfied. In this example we are going to constrain the boxes to try to
 * maintain a ratio between their widths.
 *
 * ```kotlin
 * solver.addConstraint(
 *     ((box1.right - box1.left) / 50.0) with WeightedRelation.EQ(Strength.MEDIUM) to ((box2.right - box2.left) / 100.0)
 * )
 * printChanges(names, solver.fetchChanges())
 * ```
 *
 * Now the result gives values that maintain the ratio between the sizes of the two boxes:
 *
 * ```text
 * Changes:
 * box1.right: 25
 * box2.left: 25
 * ```
 *
 * This example may have appeared somewhat contrived, but hopefully it shows the power of the
 * Cassowary algorithm for laying out user interfaces.
 *
 * One thing that this example exposes is that this package is a rather low level library.
 *
 * It does not have any inherent knowledge of user interfaces, directions or boxes. Thus for
 * use in a user interface this package should ideally be wrapped by a higher level API, which
 * is outside the scope of this package.
 *
 * @see Solver
 * @see Variable
 * @see Constraint
 * @see Expression
 * @see Strength
 * @see WeightedRelation
 */
internal object Kasuari
