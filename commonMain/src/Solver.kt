package kasuari

/**
 * The solver entered an invalid state. If this occurs please report the issue.
 */
sealed class InternalSolverError : Exception() {
    /** The objective is unbounded. */
    data object ObjectiveUnbounded : InternalSolverError() {
        override val message: String = "The objective is unbounded."
    }
    /** Dual optimize failed. */
    data object DualOptimizeFailed : InternalSolverError() {
        override val message: String = "Dual optimize failed."
    }
    /** Failed to find leaving row. */
    data object FailedToFindLeavingRow : InternalSolverError() {
        override val message: String = "Failed to find leaving row."
    }
    /** Edit constraint not in system */
    data object EditConstraintNotInSystem : InternalSolverError() {
        override val message: String = "Edit constraint not in system"
    }
}

private data class Tag(
    var marker: Symbol,
    var other: Symbol
)

private data class EditInfo(
    val tag: Tag,
    val constraint: Constraint,
    var constant: Double
)

/**
 * A constraint solver using the Cassowary algorithm. For proper usage please see the top level
 * crate documentation.
 */
class Solver {
    private val constraints: MutableMap<Constraint, Tag> = mutableMapOf()
    private val varData: MutableMap<Variable, Triple<Double, Symbol, Int>> = mutableMapOf()
    private val varForSymbol: MutableMap<Symbol, Variable> = mutableMapOf()
    private val publicChanges: MutableList<Pair<Variable, Double>> = mutableListOf()
    private val changed: MutableSet<Variable> = mutableSetOf()
    private var shouldClearChanges: Boolean = false
    private val rows: MutableMap<Symbol, Row> = mutableMapOf()
    private val edits: MutableMap<Variable, EditInfo> = mutableMapOf()
    private val infeasibleRows: MutableList<Symbol> = mutableListOf() // never contains external symbols
    private var objective: Row = Row.new(0.0)
    private var artificial: Row? = null
    private var idTick: Int = 1

    companion object {
        /** Construct a new solver. */
        fun new(): Solver = Solver()
    }

    fun addConstraints(constraints: Iterable<Constraint>) {
        for (constraint in constraints) {
            addConstraint(constraint)
        }
    }

    /** Add a constraint to the solver. */
    fun addConstraint(constraint: Constraint) {
        if (constraints.containsKey(constraint)) {
            // TODO determine if we could just ignore duplicate constraints
            throw AddConstraintError.DuplicateConstraint
        }

        // Creating a row causes symbols to reserved for the variables in the constraint. If this
        // method exits with an exception, then its possible those variables will linger in the var
        // map. Since its likely that those variables will be used in other constraints and since
        // exceptional conditions are uncommon, i'm not too worried about aggressive cleanup of the
        // var map.
        val (row, tag) = createRow(constraint)
        var subject = chooseSubject(row, tag)

        // If chooseSubject could find a valid entering symbol, one last option is available if the
        // entire row is composed of dummy variables. If the constant of the row is zero, then this
        // represents redundant constraints and the new dummy marker can enter the basis. If the
        // constant is non-zero, then it represents an unsatisfiable constraint.
        if (subject.kind == SymbolKind.Invalid && allDummies(row)) {
            if (!nearZero(row.constant)) {
                throw AddConstraintError.UnsatisfiableConstraint
            } else {
                subject = tag.marker
            }
        }

        // If an entering symbol still isn't found, then the row must be added using an artificial
        // variable. If that fails, then the row represents an unsatisfiable constraint.
        if (subject.kind == SymbolKind.Invalid) {
            val satisfiable = addWithArtificialVariable(row)
            if (!satisfiable) {
                throw AddConstraintError.UnsatisfiableConstraint
            }
        } else {
            row.solveForSymbol(subject)
            substitute(subject, row)
            if (subject.kind == SymbolKind.External && row.constant != 0.0) {
                val v = varForSymbol[subject]!!
                varChanged(v)
            }
            rows[subject] = row
        }

        constraints[constraint] = tag

        // Optimizing after each constraint is added performs less aggregate work due to a smaller
        // average system size. It also ensures the solver remains in a consistent state.
        optimize(objective)
    }

    /** Remove a constraint from the solver. */
    fun removeConstraint(constraint: Constraint) {
        val tag = constraints.remove(constraint)
            ?: throw RemoveConstraintError.UnknownConstraint

        // Remove the error effects from the objective function
        // *before* pivoting, or substitutions into the objective
        // will lead to incorrect solver results.
        removeConstraintEffects(constraint, tag)

        // If the marker is basic, simply drop the row. Otherwise,
        // pivot the marker into the basis and then drop the row.
        if (rows.remove(tag.marker) == null) {
            val (leaving, row) = getMarkerLeavingRow(tag.marker)
                ?: throw RemoveConstraintError.InternalSolver(InternalSolverError.FailedToFindLeavingRow)
            row.solveForSymbols(leaving, tag.marker)
            substitute(tag.marker, row)
        }

        // Optimizing after each constraint is removed ensures that the
        // solver remains consistent. It makes the solver api easier to
        // use at a small tradeoff for speed.
        optimize(objective)

        // Check for and decrease the reference count for variables referenced by the constraint
        // If the reference count is zero remove the variable from the variable map
        for (term in constraint.expr().terms) {
            if (!nearZero(term.coefficient)) {
                var shouldRemove = false
                val data = varData[term.variable]
                if (data != null) {
                    val newCount = data.third - 1
                    varData[term.variable] = Triple(data.first, data.second, newCount)
                    shouldRemove = newCount == 0
                }
                if (shouldRemove) {
                    varForSymbol.remove(varData[term.variable]!!.second)
                    varData.remove(term.variable)
                }
            }
        }
    }

    /** Test whether a constraint has been added to the solver. */
    fun hasConstraint(constraint: Constraint): Boolean =
        constraints.containsKey(constraint)

    /**
     * Add an edit variable to the solver.
     *
     * This method should be called before the `suggestValue` method is
     * used to supply a suggested value for the given edit variable.
     */
    fun addEditVariable(v: Variable, strength: Strength) {
        if (edits.containsKey(v)) {
            throw AddEditVariableError.DuplicateEditVariable
        }
        if (strength == Strength.REQUIRED) {
            throw AddEditVariableError.BadRequiredStrength
        }
        val cn = Constraint.new(
            Expression.fromTerm(Term.new(v, 1.0)),
            RelationalOperator.Equal,
            strength
        )
        addConstraint(cn)
        edits[v] = EditInfo(
            tag = constraints[cn]!!,
            constraint = cn,
            constant = 0.0
        )
    }

    /** Remove an edit variable from the solver. */
    fun removeEditVariable(v: Variable) {
        val editInfo = edits.remove(v)
        if (editInfo != null) {
            try {
                removeConstraint(editInfo.constraint)
            } catch (e: RemoveConstraintError.UnknownConstraint) {
                throw RemoveEditVariableError.InternalSolver(InternalSolverError.EditConstraintNotInSystem)
            } catch (e: RemoveConstraintError.InternalSolver) {
                throw RemoveEditVariableError.InternalSolver(e.error)
            }
        } else {
            throw RemoveEditVariableError.UnknownEditVariable
        }
    }

    /** Test whether an edit variable has been added to the solver. */
    fun hasEditVariable(v: Variable): Boolean =
        edits.containsKey(v)

    /**
     * Suggest a value for the given edit variable.
     *
     * This method should be used after an edit variable has been added to
     * the solver in order to suggest the value for that variable.
     */
    fun suggestValue(variable: Variable, value: Double) {
        val info = edits[variable]
            ?: throw SuggestValueError.UnknownEditVariable
        val delta = value - info.constant
        info.constant = value
        val infoTagMarker = info.tag.marker
        val infoTagOther = info.tag.other
        // tag.marker and tag.other are never external symbols

        val markerRow = rows[infoTagMarker]
        val otherRow = rows[infoTagOther]

        if (markerRow != null) {
            if (markerRow.add(-delta) < 0.0) {
                infeasibleRows.add(infoTagMarker)
            }
        } else if (otherRow != null) {
            if (otherRow.add(delta) < 0.0) {
                infeasibleRows.add(infoTagOther)
            }
        } else {
            for ((symbol, row) in rows) {
                val coeff = row.coefficientFor(infoTagMarker)
                val diff = delta * coeff
                if (diff != 0.0 && symbol.kind == SymbolKind.External) {
                    val v = varForSymbol[symbol]!!
                    // inline varChanged
                    if (shouldClearChanges) {
                        changed.clear()
                        shouldClearChanges = false
                    }
                    changed.add(v)
                }
                if (coeff != 0.0 && row.add(diff) < 0.0 && symbol.kind != SymbolKind.External) {
                    infeasibleRows.add(symbol)
                }
            }
        }
        dualOptimize()
    }

    private fun varChanged(v: Variable) {
        if (shouldClearChanges) {
            changed.clear()
            shouldClearChanges = false
        }
        changed.add(v)
    }

    /**
     * Fetches all changes to the values of variables since the last call to this function.
     *
     * The list of changes returned is not in a specific order. Each change comprises the variable
     * changed and the new value of that variable.
     */
    fun fetchChanges(): List<Pair<Variable, Double>> {
        if (shouldClearChanges) {
            changed.clear()
            shouldClearChanges = false
        } else {
            shouldClearChanges = true
        }
        publicChanges.clear()
        for (v in changed) {
            val data = varData[v]
            if (data != null) {
                // Normalize -0.0 to 0.0 for consistent equality comparisons
                val rawValue = rows[data.second]?.constant ?: 0.0
                val newValue = if (rawValue == 0.0) 0.0 else rawValue
                val oldValue = data.first
                if (oldValue != newValue) {
                    publicChanges.add(Pair(v, newValue))
                    varData[v] = Triple(newValue, data.second, data.third)
                }
            }
        }
        return publicChanges
    }

    /**
     * Reset the solver to the empty starting condition.
     *
     * This method resets the internal solver state to the empty starting
     * condition, as if no constraints or edit variables have been added.
     * This can be faster than deleting the solver and creating a new one
     * when the entire system must change, since it can avoid unnecessary
     * heap (de)allocations.
     */
    fun reset() {
        rows.clear()
        constraints.clear()
        varData.clear()
        varForSymbol.clear()
        changed.clear()
        shouldClearChanges = false
        edits.clear()
        infeasibleRows.clear()
        objective = Row.new(0.0)
        artificial = null
        idTick = 1
    }

    /**
     * Get the symbol for the given variable.
     *
     * If a symbol does not exist for the variable, one will be created.
     */
    private fun getVarSymbol(v: Variable): Symbol {
        val existing = varData[v]
        if (existing != null) {
            varData[v] = Triple(existing.first, existing.second, existing.third + 1)
            return existing.second
        }
        val s = Symbol.new(idTick, SymbolKind.External)
        varForSymbol[s] = v
        idTick += 1
        varData[v] = Triple(Double.NaN, s, 1)
        return s
    }

    /**
     * Create a new Row object for the given constraint.
     *
     * The terms in the constraint will be converted to cells in the row. Any term in the
     * constraint with a coefficient of zero is ignored. This method uses the `getVarSymbol`
     * method to get the symbol for the variables added to the row. If the symbol for a given cell
     * variable is basic, the cell variable will be substituted with the basic row.
     *
     * The necessary slack and error variables will be added to the row. If the constant for the
     * row is negative, the sign for the row will be inverted so the constant becomes positive.
     *
     * The tag will be updated with the marker and error symbols to use for tracking the movement
     * of the constraint in the tableau.
     */
    private fun createRow(constraint: Constraint): Pair<Row, Tag> {
        val expr = constraint.expr()
        val row = Row.new(expr.constant)

        // Substitute the current basic variables into the row.
        for (term in expr.terms) {
            if (!nearZero(term.coefficient)) {
                val symbol = getVarSymbol(term.variable)
                val otherRow = rows[symbol]
                if (otherRow != null) {
                    row.insertRow(otherRow, term.coefficient)
                } else {
                    row.insertSymbol(symbol, term.coefficient)
                }
            }
        }

        // Add the necessary slack, error, and dummy variables.
        val tag = when (constraint.op()) {
            RelationalOperator.GreaterOrEqual, RelationalOperator.LessOrEqual -> {
                val coeff = if (constraint.op() == RelationalOperator.LessOrEqual) 1.0 else -1.0
                val slack = Symbol.new(idTick, SymbolKind.Slack)
                idTick += 1
                row.insertSymbol(slack, coeff)
                if (constraint.strength() < Strength.REQUIRED) {
                    val error = Symbol.new(idTick, SymbolKind.Error)
                    idTick += 1
                    row.insertSymbol(error, -coeff)
                    objective.insertSymbol(error, constraint.strength().value())
                    Tag(marker = slack, other = error)
                } else {
                    Tag(marker = slack, other = Symbol.invalid())
                }
            }
            RelationalOperator.Equal -> {
                if (constraint.strength() < Strength.REQUIRED) {
                    val errplus = Symbol.new(idTick, SymbolKind.Error)
                    idTick += 1
                    val errminus = Symbol.new(idTick, SymbolKind.Error)
                    idTick += 1
                    row.insertSymbol(errplus, -1.0) // v = eplus - eminus
                    row.insertSymbol(errminus, 1.0) // v - eplus + eminus = 0
                    objective.insertSymbol(errplus, constraint.strength().value())
                    objective.insertSymbol(errminus, constraint.strength().value())
                    Tag(marker = errplus, other = errminus)
                } else {
                    val dummy = Symbol.new(idTick, SymbolKind.Dummy)
                    idTick += 1
                    row.insertSymbol(dummy, 1.0)
                    Tag(marker = dummy, other = Symbol.invalid())
                }
            }
        }

        // Ensure the row has a positive constant.
        if (row.constant < 0.0) {
            row.reverseSign()
        }
        return Pair(row, tag)
    }

    /**
     * Choose the subject for solving for the row.
     *
     * This method will choose the best subject for using as the solve
     * target for the row. An invalid symbol will be returned if there
     * is no valid target.
     *
     * The symbols are chosen according to the following precedence:
     *
     * 1) The first symbol representing an external variable.
     * 2) A negative slack or error tag variable.
     *
     * If a subject cannot be found, an invalid symbol will be returned.
     */
    private fun chooseSubject(row: Row, tag: Tag): Symbol {
        for (s in row.cells.keys) {
            if (s.kind == SymbolKind.External) {
                return s
            }
        }
        if ((tag.marker.kind == SymbolKind.Slack || tag.marker.kind == SymbolKind.Error)
            && row.coefficientFor(tag.marker) < 0.0
        ) {
            return tag.marker
        }
        if ((tag.other.kind == SymbolKind.Slack || tag.other.kind == SymbolKind.Error)
            && row.coefficientFor(tag.other) < 0.0
        ) {
            return tag.other
        }
        return Symbol.invalid()
    }

    /**
     * Add the row to the tableau using an artificial variable.
     *
     * This will return false if the constraint cannot be satisfied.
     */
    private fun addWithArtificialVariable(row: Row): Boolean {
        // Create and add the artificial variable to the tableau
        val art = Symbol.new(idTick, SymbolKind.Slack)
        idTick += 1
        rows[art] = row.clone()
        artificial = row.clone()

        // Optimize the artificial objective. This is successful
        // only if the artificial objective is optimized to zero.
        val artificialRow = artificial!!
        optimize(artificialRow)
        val success = nearZero(artificialRow.constant)
        artificial = null

        // If the artificial variable is basic, pivot the row so that
        // it becomes basic. If the row is constant, exit early.
        val removedRow = rows.remove(art)
        if (removedRow != null) {
            if (removedRow.cells.isEmpty()) {
                return success
            }
            val entering = anyPivotableSymbol(removedRow) // never External
            if (entering.kind == SymbolKind.Invalid) {
                return false // unsatisfiable (will this ever happen?)
            }
            removedRow.solveForSymbols(art, entering)
            substitute(entering, removedRow)
            rows[entering] = removedRow
        }

        // Remove the artificial row from the tableau
        for (r in rows.values) {
            r.remove(art)
        }
        objective.remove(art)
        return success
    }

    /**
     * Substitute the parametric symbol with the given row.
     *
     * This method will substitute all instances of the parametric symbol
     * in the tableau and the objective function with the given row.
     */
    private fun substitute(symbol: Symbol, row: Row) {
        for ((otherSymbol, otherRow) in rows) {
            val constantChanged = otherRow.substitute(symbol, row)
            if (otherSymbol.kind == SymbolKind.External && constantChanged) {
                val v = varForSymbol[otherSymbol]!!
                // inline varChanged
                if (shouldClearChanges) {
                    changed.clear()
                    shouldClearChanges = false
                }
                changed.add(v)
            }
            if (otherSymbol.kind != SymbolKind.External && otherRow.constant < 0.0) {
                infeasibleRows.add(otherSymbol)
            }
        }
        objective.substitute(symbol, row)
        artificial?.substitute(symbol, row)
    }

    /**
     * Optimize the system for the given objective function.
     *
     * This method performs iterations of Phase 2 of the simplex method
     * until the objective function reaches a minimum.
     */
    private fun optimize(objective: Row) {
        while (true) {
            val entering = getEnteringSymbol(objective)
            if (entering.kind == SymbolKind.Invalid) {
                return
            }
            val (leaving, row) = getLeavingRow(entering)
                ?: throw InternalSolverError.ObjectiveUnbounded
            // pivot the entering symbol into the basis
            row.solveForSymbols(leaving, entering)
            substitute(entering, row)
            if (entering.kind == SymbolKind.External && row.constant != 0.0) {
                val v = varForSymbol[entering]!!
                varChanged(v)
            }
            rows[entering] = row
        }
    }

    /**
     * Optimize the system using the dual of the simplex method.
     *
     * The current state of the system should be such that the objective
     * function is optimal, but not feasible. This method will perform
     * an iteration of the dual simplex method to make the solution both
     * optimal and feasible.
     */
    private fun dualOptimize() {
        while (infeasibleRows.isNotEmpty()) {
            val leaving = infeasibleRows.removeAt(infeasibleRows.size - 1)
            val existingRow = rows[leaving]
            val row = if (existingRow != null && existingRow.constant < 0.0) {
                rows.remove(leaving)
            } else {
                null
            }
            if (row != null) {
                val entering = getDualEnteringSymbol(row)
                if (entering.kind == SymbolKind.Invalid) {
                    throw InternalSolverError.DualOptimizeFailed
                }
                // pivot the entering symbol into the basis
                row.solveForSymbols(leaving, entering)
                substitute(entering, row)
                if (entering.kind == SymbolKind.External && row.constant != 0.0) {
                    val v = varForSymbol[entering]!!
                    varChanged(v)
                }
                rows[entering] = row
            }
        }
    }

    /**
     * Compute the entering variable for a pivot operation.
     *
     * This method will return first symbol in the objective function which
     * is non-dummy and has a coefficient less than zero. If no symbol meets
     * the criteria, it means the objective function is at a minimum, and an
     * invalid symbol is returned.
     * Could return an External symbol
     */
    private fun getEnteringSymbol(objective: Row): Symbol {
        for ((symbol, value) in objective.cells) {
            if (symbol.kind != SymbolKind.Dummy && value < 0.0) {
                return symbol
            }
        }
        return Symbol.invalid()
    }

    /**
     * Compute the entering symbol for the dual optimize operation.
     *
     * This method will return the symbol in the row which has a positive
     * coefficient and yields the minimum ratio for its respective symbol
     * in the objective function. The provided row *must* be infeasible.
     * If no symbol is found which meets the criteria, an invalid symbol
     * is returned.
     * Could return an External symbol
     */
    private fun getDualEnteringSymbol(row: Row): Symbol {
        var entering = Symbol.invalid()
        var ratio = Double.POSITIVE_INFINITY
        for ((symbol, value) in row.cells) {
            if (value > 0.0 && symbol.kind != SymbolKind.Dummy) {
                val coeff = objective.coefficientFor(symbol)
                val r = coeff / value
                if (r < ratio) {
                    ratio = r
                    entering = symbol
                }
            }
        }
        return entering
    }

    /**
     * Get the first Slack or Error symbol in the row.
     *
     * If no such symbol is present, an Invalid symbol will be returned.
     * Never returns an External symbol
     */
    private fun anyPivotableSymbol(row: Row): Symbol {
        for (symbol in row.cells.keys) {
            if (symbol.kind == SymbolKind.Slack || symbol.kind == SymbolKind.Error) {
                return symbol
            }
        }
        return Symbol.invalid()
    }

    /**
     * Compute the row which holds the exit symbol for a pivot.
     *
     * This method will return an iterator to the row in the row map
     * which holds the exit symbol. If no appropriate exit symbol is
     * found, null will be returned. This indicates that
     * the objective function is unbounded.
     * Never returns a row for an External symbol
     */
    private fun getLeavingRow(entering: Symbol): Pair<Symbol, Row>? {
        var ratio = Double.POSITIVE_INFINITY
        var found: Symbol? = null
        for ((symbol, row) in rows) {
            if (symbol.kind != SymbolKind.External) {
                val temp = row.coefficientFor(entering)
                if (temp < 0.0) {
                    val tempRatio = -row.constant / temp
                    if (tempRatio < ratio) {
                        ratio = tempRatio
                        found = symbol
                    }
                }
            }
        }
        return found?.let { s -> Pair(s, rows.remove(s)!!) }
    }

    /**
     * Compute the leaving row for a marker variable.
     *
     * This method will return an iterator to the row in the row map
     * which holds the given marker variable. The row will be chosen
     * according to the following precedence:
     *
     * 1) The row with a restricted basic variable and a negative coefficient for the marker with
     *    the smallest ratio of -constant / coefficient.
     *
     * 2) The row with a restricted basic variable and the smallest ratio of constant /
     *    coefficient.
     *
     * 3) The last unrestricted row which contains the marker.
     *
     * If the marker does not exist in any row, null will be returned. This indicates an internal
     * solver error since the marker *should* exist somewhere in the tableau.
     */
    private fun getMarkerLeavingRow(marker: Symbol): Pair<Symbol, Row>? {
        var r1 = Double.POSITIVE_INFINITY
        var r2 = r1
        var first: Symbol? = null
        var second: Symbol? = null
        var third: Symbol? = null
        for ((symbol, row) in rows) {
            val c = row.coefficientFor(marker)
            if (c == 0.0) {
                continue
            }
            if (symbol.kind == SymbolKind.External) {
                third = symbol
            } else if (c < 0.0) {
                val r = -row.constant / c
                if (r < r1) {
                    r1 = r
                    first = symbol
                }
            } else {
                val r = row.constant / c
                if (r < r2) {
                    r2 = r
                    second = symbol
                }
            }
        }
        val s = first ?: second ?: third ?: return null
        if (s.kind == SymbolKind.External && rows[s]!!.constant != 0.0) {
            val v = varForSymbol[s]!!
            varChanged(v)
        }
        return rows.remove(s)?.let { Pair(s, it) }
    }

    /** Remove the effects of a constraint on the objective function. */
    private fun removeConstraintEffects(constraint: Constraint, tag: Tag) {
        if (tag.marker.kind == SymbolKind.Error) {
            removeMarkerEffects(tag.marker, constraint.strength().value())
        }
        if (tag.other.kind == SymbolKind.Error) {
            removeMarkerEffects(tag.other, constraint.strength().value())
        }
    }

    /** Remove the effects of an error marker on the objective function. */
    private fun removeMarkerEffects(marker: Symbol, strength: Double) {
        val row = rows[marker]
        if (row != null) {
            objective.insertRow(row, -strength)
        } else {
            objective.insertSymbol(marker, -strength)
        }
    }

    /** Test whether a row is composed of all dummy variables. */
    private fun allDummies(row: Row): Boolean {
        for (symbol in row.cells.keys) {
            if (symbol.kind != SymbolKind.Dummy) {
                return false
            }
        }
        return true
    }

    /**
     * Get the stored value for a variable.
     *
     * Normally values should be retrieved and updated using `fetchChanges`, but this method can
     * be used for debugging or testing.
     */
    fun getValue(v: Variable): Double {
        val data = varData[v] ?: return 0.0
        val rawValue = rows[data.second]?.constant ?: 0.0
        // Normalize -0.0 to 0.0 for consistent equality comparisons
        return if (rawValue == 0.0) 0.0 else rawValue
    }
}
