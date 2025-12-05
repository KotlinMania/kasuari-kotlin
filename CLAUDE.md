# kasuari-kotlin Transliteration Guide

This project is a **Kotlin Multiplatform Native** port of [kasuari](https://github.com/ratatui/kasuari), a Cassowary constraint solver implementation.

## See Also

Refer to `../ratatui-kotlin/CLAUDE.md` for the comprehensive Rust → Kotlin mapping guide. This file contains kasuari-specific notes.

## Project Structure

```
kasuari-kotlin/
├── commonMain/
│   ├── src/           # Main source files (.kt containing Rust to convert)
│   └── tests/         # Test files
├── build.gradle.kts   # Gradle build configuration
└── src/               # Original Rust source (reference only)
```

## Key Types

| Rust Type | Kotlin Type | Notes |
|-----------|-------------|-------|
| `Variable` | `Variable` | Wrapper around unique ID, used as map keys |
| `Term` | `Term` | Variable × coefficient pair |
| `Expression` | `Expression` | Linear combination of Terms + constant |
| `Constraint` | `Constraint` | Expression with relation and strength |
| `Solver` | `Solver` | Main constraint solver, mutable state |
| `Strength` | `Strength` | Constraint priority (REQUIRED, STRONG, MEDIUM, WEAK) |

## Cassowary-Specific Patterns

### Strength Constants

```rust
// Rust
pub const REQUIRED: Strength = Strength::REQUIRED;
pub const STRONG: Strength = Strength::STRONG;
```
```kotlin
// Kotlin - use companion object constants
companion object {
    val REQUIRED = Strength(1001001000.0)
    val STRONG = Strength(1000000.0)
    // etc.
}
```

### Constraint DSL

The Rust version uses `|EQ(s)|` pipe syntax via BitOr overloading. In Kotlin, use infix functions:

```rust
// Rust
box1.left |EQ(REQUIRED)| 0.0
```
```kotlin
// Kotlin
box1.left with WeightedRelation.EQ(Strength.REQUIRED) to 0.0
// Or create a constraint directly:
Constraint(box1.left.toExpression(), RelationalOperator.Equal, 0.0, Strength.REQUIRED)
```

### Variable Identity

Variables use identity-based equality (each `Variable.new()` is unique):

```rust
// Rust
let v1 = Variable::new();
let v2 = Variable::new();
assert!(v1 != v2);
```
```kotlin
// Kotlin - use a class with auto-incrementing ID
class Variable private constructor(val id: Long) {
    companion object {
        private var nextId: Long = 0
        fun new(): Variable = Variable(nextId++)
    }
    override fun equals(other: Any?) = other is Variable && id == other.id
    override fun hashCode() = id.hashCode()
}
```

### Internal Solver Symbols

The solver uses internal `Symbol` types for slack/error/dummy variables. These should remain internal:

```kotlin
internal sealed class Symbol {
    data class External(val id: Long) : Symbol()
    data class Slack(val id: Long) : Symbol()
    data class Error(val id: Long) : Symbol()
    data class Dummy(val id: Long) : Symbol()
}
```

## Files to Convert (in dependency order)

1. **Variable.kt** - No dependencies
2. **Strength.kt** - No dependencies
3. **Term.kt** - Depends on Variable
4. **Expression.kt** - Depends on Variable, Term
5. **Relations.kt** - Depends on Strength ✓ DONE
6. **Error.kt** - Depends on InternalSolverError ✓ DONE
7. **Constraint.kt** - Depends on Expression, Relations, Strength
8. **Row.kt** - Internal, depends on Symbol
9. **Solver.kt** - Depends on everything above
10. **Lib.kt** - Public API re-exports (convert to package-level declarations)

## Testing

Tests are in `commonMain/tests/`. Convert test files after the main source is working.
