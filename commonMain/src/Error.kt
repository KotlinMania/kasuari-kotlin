package kasuari

/**
 * The possible error conditions that [Solver.addConstraint] can fail with.
 */
sealed class AddConstraintError : Exception() {
    /**
     * The constraint specified has already been added to the solver.
     */
    data object DuplicateConstraint : AddConstraintError() {
        override val message: String = "The constraint specified has already been added to the solver."
    }

    /**
     * The constraint is required, but it is unsatisfiable in conjunction with the existing
     * constraints.
     */
    data object UnsatisfiableConstraint : AddConstraintError() {
        override val message: String = "The constraint is required, but it is unsatisfiable in conjunction with the existing constraints."
    }

    /**
     * The solver entered an invalid state.
     */
    data class InternalSolver(val error: InternalSolverError) : AddConstraintError() {
        override val message: String = "The solver entered an invalid state. If this occurs please report the issue."
    }
}

/**
 * The possible error conditions that [Solver.removeConstraint] can fail with.
 */
sealed class RemoveConstraintError : Exception() {
    /**
     * The constraint specified was not already in the solver, so cannot be removed.
     */
    data object UnknownConstraint : RemoveConstraintError() {
        override val message: String = "The constraint specified was not already in the solver, so cannot be removed."
    }

    /**
     * The solver entered an invalid state. If this occurs please report the issue.
     */
    data class InternalSolver(val error: InternalSolverError) : RemoveConstraintError() {
        override val message: String = "The solver entered an invalid state. If this occurs please report the issue."
    }
}

/**
 * The possible error conditions that [Solver.addEditVariable] can fail with.
 */
sealed class AddEditVariableError : Exception() {
    /**
     * The specified variable is already marked as an edit variable in the solver.
     */
    data object DuplicateEditVariable : AddEditVariableError() {
        override val message: String = "The specified variable is already marked as an edit variable in the solver."
    }

    /**
     * The specified strength was `REQUIRED`. This is illegal for edit variable strengths.
     */
    data object BadRequiredStrength : AddEditVariableError() {
        override val message: String = "The specified strength was `REQUIRED`. This is illegal for edit variable strengths."
    }
}

/**
 * The possible error conditions that [Solver.removeEditVariable] can fail with.
 */
sealed class RemoveEditVariableError : Exception() {
    /**
     * The specified variable was not an edit variable in the solver, so cannot be removed.
     */
    data object UnknownEditVariable : RemoveEditVariableError() {
        override val message: String = "The specified variable was not an edit variable in the solver, so cannot be removed."
    }

    /**
     * The solver entered an invalid state. If this occurs please report the issue.
     */
    data class InternalSolver(val error: InternalSolverError) : RemoveEditVariableError() {
        override val message: String = "The solver entered an invalid state. If this occurs please report the issue."
    }
}

/**
 * The possible error conditions that [Solver.suggestValue] can fail with.
 */
sealed class SuggestValueError : Exception() {
    /**
     * The specified variable was not an edit variable in the solver, so cannot have its value
     * suggested.
     */
    data object UnknownEditVariable : SuggestValueError() {
        override val message: String = "The specified variable was not an edit variable in the solver, so cannot have its value suggested."
    }

    /**
     * The solver entered an invalid state. If this occurs please report the issue.
     */
    data class InternalSolver(val error: InternalSolverError) : SuggestValueError() {
        override val message: String = "The solver entered an invalid state. If this occurs please report the issue."
    }
}
