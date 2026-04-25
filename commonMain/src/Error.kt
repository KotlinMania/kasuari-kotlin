// port-lint: source error.rs
package kasuari

/** The possible error conditions that `Solver.addConstraint` can fail with. */
sealed class AddConstraintError(message: String) : Exception(message) {
    data object DuplicateConstraint :
        AddConstraintError("The constraint specified has already been added to the solver.")

    data object UnsatisfiableConstraint :
        AddConstraintError("The constraint is required, but it is unsatisfiable in conjunction with the existing constraints.")

    data class InternalSolverError(val error: kasuari.InternalSolverError) :
        AddConstraintError("The solver entered an invalid state. If this occurs please report the issue.")
}

/** The possible error conditions that `Solver.removeConstraint` can fail with. */
sealed class RemoveConstraintError(message: String) : Exception(message) {
    data object UnknownConstraint :
        RemoveConstraintError("The constraint specified was not already in the solver, so cannot be removed.")

    data class InternalSolverError(val error: kasuari.InternalSolverError) :
        RemoveConstraintError("The solver entered an invalid state. If this occurs please report the issue.")
}

/** The possible error conditions that `Solver.addEditVariable` can fail with. */
sealed class AddEditVariableError(message: String) : Exception(message) {
    data object DuplicateEditVariable :
        AddEditVariableError("The specified variable is already marked as an edit variable in the solver.")

    data object BadRequiredStrength :
        AddEditVariableError("The specified strength was `REQUIRED`. This is illegal for edit variable strengths.")
}

/** The possible error conditions that `Solver.removeEditVariable` can fail with. */
sealed class RemoveEditVariableError(message: String) : Exception(message) {
    data object UnknownEditVariable :
        RemoveEditVariableError("The specified variable was not an edit variable in the solver, so cannot be removed.")

    data class InternalSolverError(val error: kasuari.InternalSolverError) :
        RemoveEditVariableError("The solver entered an invalid state. If this occurs please report the issue.")
}

/** The possible error conditions that `Solver.suggestValue` can fail with. */
sealed class SuggestValueError(message: String) : Exception(message) {
    data object UnknownEditVariable :
        SuggestValueError("The specified variable was not an edit variable in the solver, so cannot have its value suggested.")

    data class InternalSolverError(val error: kasuari.InternalSolverError) :
        SuggestValueError("The solver entered an invalid state. If this occurs please report the issue.")
}
