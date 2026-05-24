# port-lint Proposed Changes

**Generated:** 2026-05-24
**Source:** tmp/scan-rust
**Target:** tmp/scan-kotlin

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `../../commonMain/src/Variable.kt` | `// port-lint: source variable.rs` | `// port-lint: source variable.rs` | `variable.rs` | `port-lint provenance header matched only after fallback normalization: 'variable.rs' vs expected 'variable.rs'` |
| `../../commonTest/kotlin/VariableTest.kt` | `// port-lint: tests variable.rs` | `// port-lint: tests variable.rs` | `variable.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:variable.rs' vs expected 'variable.rs'` |
| `../../commonMain/src/Strength.kt` | `// port-lint: source strength.rs` | `// port-lint: source strength.rs` | `strength.rs` | `port-lint provenance header matched only after fallback normalization: 'strength.rs' vs expected 'strength.rs'` |
| `../../commonTest/kotlin/StrengthTest.kt` | `// port-lint: tests strength.rs` | `// port-lint: tests strength.rs` | `strength.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:strength.rs' vs expected 'strength.rs'` |
| `../../commonMain/src/Expression.kt` | `// port-lint: source expression.rs` | `// port-lint: source expression.rs` | `expression.rs` | `port-lint provenance header matched only after fallback normalization: 'expression.rs' vs expected 'expression.rs'` |
| `../../commonMain/src/Term.kt` | `// port-lint: source term.rs` | `// port-lint: source term.rs` | `term.rs` | `port-lint provenance header matched only after fallback normalization: 'term.rs' vs expected 'term.rs'` |
| `../../commonTest/kotlin/TermTest.kt` | `// port-lint: tests term.rs` | `// port-lint: tests term.rs` | `term.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:term.rs' vs expected 'term.rs'` |
| `../../commonMain/src/Constraint.kt` | `// port-lint: source constraint.rs` | `// port-lint: source constraint.rs` | `constraint.rs` | `port-lint provenance header matched only after fallback normalization: 'constraint.rs' vs expected 'constraint.rs'` |
| `../../commonTest/kotlin/ConstraintTest.kt` | `// port-lint: tests constraint.rs` | `// port-lint: tests constraint.rs` | `constraint.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:constraint.rs' vs expected 'constraint.rs'` |
| `../../commonMain/src/Relations.kt` | `// port-lint: source relations.rs` | `// port-lint: source relations.rs` | `relations.rs` | `port-lint provenance header matched only after fallback normalization: 'relations.rs' vs expected 'relations.rs'` |
| `../../commonMain/src/Solver.kt` | `// port-lint: source solver.rs` | `// port-lint: source solver.rs` | `solver.rs` | `port-lint provenance header matched only after fallback normalization: 'solver.rs' vs expected 'solver.rs'` |
| `../../commonMain/src/Row.kt` | `// port-lint: source row.rs` | `// port-lint: source row.rs` | `row.rs` | `port-lint provenance header matched only after fallback normalization: 'row.rs' vs expected 'row.rs'` |
| `../../commonMain/src/Error.kt` | `// port-lint: source error.rs` | `// port-lint: source error.rs` | `error.rs` | `port-lint provenance header matched only after fallback normalization: 'error.rs' vs expected 'error.rs'` |
| `../../commonTest/kotlin/Common.kt` | `// port-lint: source tests/common/mod.rs` | `// port-lint: source ../kasuari-rs/tests/common/mod.rs` | `../kasuari-rs/tests/common/mod.rs` | `port-lint provenance header matched only by basename: 'tests/common/mod.rs' vs expected '../kasuari-rs/tests/common/mod.rs'` |
| `../../commonTest/kotlin/QuadrilateralTest.kt` | `// port-lint: source tests/quadrilateral.rs` | `// port-lint: source ../kasuari-rs/tests/quadrilateral.rs` | `../kasuari-rs/tests/quadrilateral.rs` | `port-lint provenance header matched only by basename: 'tests/quadrilateral.rs' vs expected '../kasuari-rs/tests/quadrilateral.rs'` |
| `../../commonTest/kotlin/RemovalTest.kt` | `// port-lint: source tests/removal.rs` | `// port-lint: source ../kasuari-rs/tests/removal.rs` | `../kasuari-rs/tests/removal.rs` | `port-lint provenance header matched only by basename: 'tests/removal.rs' vs expected '../kasuari-rs/tests/removal.rs'` |
| `../../commonMain/src/Lib.kt` | `// port-lint: source lib.rs` | `// port-lint: source lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'lib.rs' vs expected 'lib.rs'` |
