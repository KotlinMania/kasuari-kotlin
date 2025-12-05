package kasuari

class Row(
    val cells: MutableMap<Symbol, Double> = mutableMapOf(),
    var constant: Double
) {
    fun clone(): Row = Row(cells.toMutableMap(), constant)

    companion object {
        fun new(constant: Double): Row = Row(mutableMapOf(), constant)
    }

    fun add(v: Double): Double {
        constant += v
        return constant
    }

    fun insertSymbol(s: Symbol, coefficient: Double) {
        val existing = cells[s]
        if (existing == null) {
            if (!nearZero(coefficient)) {
                cells[s] = coefficient
            }
        } else {
            val newValue = existing + coefficient
            if (nearZero(newValue)) {
                cells.remove(s)
            } else {
                cells[s] = newValue
            }
        }
    }

    fun insertRow(other: Row, coefficient: Double): Boolean {
        val constantDiff = other.constant * coefficient
        constant += constantDiff
        for ((s, v) in other.cells) {
            insertSymbol(s, v * coefficient)
        }
        return constantDiff != 0.0
    }

    fun remove(s: Symbol) {
        cells.remove(s)
    }

    fun reverseSign() {
        constant = -constant
        for (key in cells.keys) {
            cells[key] = -cells[key]!!
        }
    }

    fun solveForSymbol(s: Symbol) {
        val coeff = -1.0 / cells.remove(s)!!
        constant *= coeff
        for (key in cells.keys) {
            cells[key] = cells[key]!! * coeff
        }
    }

    fun solveForSymbols(lhs: Symbol, rhs: Symbol) {
        insertSymbol(lhs, -1.0)
        solveForSymbol(rhs)
    }

    fun coefficientFor(s: Symbol): Double = cells[s] ?: 0.0

    fun substitute(s: Symbol, row: Row): Boolean {
        val coeff = cells.remove(s)
        return if (coeff != null) {
            insertRow(row, coeff)
        } else {
            false
        }
    }
}

data class Symbol(val id: Int, val kind: SymbolKind) : Comparable<Symbol> {
    override fun compareTo(other: Symbol): Int {
        val idCmp = id.compareTo(other.id)
        return if (idCmp != 0) idCmp else kind.compareTo(other.kind)
    }

    companion object {
        fun new(id: Int, kind: SymbolKind): Symbol = Symbol(id, kind)
        fun invalid(): Symbol = Symbol(0, SymbolKind.Invalid)
    }
}

enum class SymbolKind {
    Invalid,
    External,
    Slack,
    Error,
    Dummy,
}

private const val EPS: Double = 1E-8

fun nearZero(value: Double): Boolean {
    return if (value < 0.0) {
        -value < EPS
    } else {
        value < EPS
    }
}

