// port-lint: source tests/common/mod.rs
package kasuari

/**
 * Helper class that tracks the current value of every [Variable] reported by the
 * solver, mirroring the upstream `Values` helper used by the integration tests
 * in the kasuari test suite.
 *
 * Each call to [updateValues] applies the change list returned by
 * [Solver.fetchChanges]; each call to [valueOf] returns the most recently seen
 * value for the given variable, defaulting to `0.0` when the variable has never
 * received a change.
 */
class Values {
    private val values: MutableMap<Variable, Double> = mutableMapOf()

    fun valueOf(variable: Variable): Double {
        val current = values[variable]
        return current ?: 0.0
    }

    fun updateValues(changes: List<Pair<Variable, Double>>) {
        for ((variable, value) in changes) {
            println("$variable changed to $value")
            values[variable] = value
        }
    }
}

/**
 * Create a new [Values] instance and return a (valueOf, updateValues) pair of
 * function references, mirroring the upstream factory that hands back two
 * closures sharing the same underlying value map.
 *
 * The two callables share the same underlying [Values] so updates made through
 * [Values.updateValues] are visible to subsequent [Values.valueOf] calls.
 */
fun newValues(): Pair<(Variable) -> Double, (List<Pair<Variable, Double>>) -> Unit> {
    val values = Values()
    return Pair(values::valueOf, values::updateValues)
}
