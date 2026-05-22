# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 13/13 (100.0%)
- **Function parity:** 144/145 matched (target 414) — 99.3%
- **Class/type parity:** 23/29 matched (target 44) — 79.3%
- **Combined symbol parity:** 167/174 matched (target 458) — 96.0%
- **Average inline-code cosine:** 0.76 (function body across 11 matched files)
- **Average documentation cosine:** 0.51 (doc text across 11 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 3 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. kasuari-rs.variable

- **Target:** `commonMain.Variable [PROVENANCE-FALLBACK]`
- **Similarity:** 0.66
- **Dependents:** 2
- **Priority Score:** 2023003.4
- **Functions:** 27/28 matched (target 85)
- **Missing functions:** `from_id`
- **Types:** 1/2 matched
- **Missing types:** `Output`
- **Tests:** 18/19 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `variable.rs` vs expected `variable.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:variable.rs` vs expected `variable.rs`
- **Proposed provenance header:** `// port-lint: source variable.rs` (current: `// port-lint: source variable.rs`)
- **Proposed provenance header:** `// port-lint: tests variable.rs` (current: `// port-lint: tests variable.rs`)
- **Lint issues:** 2

### 2. kasuari-rs.strength

- **Target:** `commonMain.Strength [PROVENANCE-FALLBACK]`
- **Similarity:** 0.49
- **Dependents:** 2
- **Priority Score:** 2011705.1
- **Functions:** 15/15 matched (target 94)
- **Missing functions:** _none_
- **Types:** 1/2 matched
- **Missing types:** `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strength.rs` vs expected `strength.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:strength.rs` vs expected `strength.rs`
- **Proposed provenance header:** `// port-lint: source strength.rs` (current: `// port-lint: source strength.rs`)
- **Proposed provenance header:** `// port-lint: tests strength.rs` (current: `// port-lint: tests strength.rs`)
- **Lint issues:** 2

### 3. kasuari-rs.term

- **Target:** `commonMain.Term [PROVENANCE-FALLBACK]`
- **Similarity:** 0.66
- **Dependents:** 1
- **Priority Score:** 1013003.4
- **Functions:** 28/28 matched (target 85)
- **Missing functions:** _none_
- **Types:** 1/2 matched
- **Missing types:** `Output`
- **Tests:** 16/16 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `term.rs` vs expected `term.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:term.rs` vs expected `term.rs`
- **Proposed provenance header:** `// port-lint: source term.rs` (current: `// port-lint: source term.rs`)
- **Proposed provenance header:** `// port-lint: tests term.rs` (current: `// port-lint: tests term.rs`)
- **Lint issues:** 2

### 4. kasuari-rs.expression

- **Target:** `commonMain.Expression [PROVENANCE-FALLBACK]`
- **Similarity:** 0.66
- **Dependents:** 1
- **Priority Score:** 1011803.4
- **Functions:** 16/16 matched (target 62)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `expression.rs` vs expected `expression.rs`
- **Proposed provenance header:** `// port-lint: source expression.rs` (current: `// port-lint: source expression.rs`)
- **Lint issues:** 1

### 5. kasuari-rs.constraint

- **Target:** `commonMain.Constraint [PROVENANCE-FALLBACK]`
- **Similarity:** 0.69
- **Dependents:** 1
- **Priority Score:** 1011103.1
- **Functions:** 7/7 matched (target 28)
- **Missing functions:** _none_
- **Types:** 3/4 matched
- **Missing types:** `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `constraint.rs` vs expected `constraint.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:constraint.rs` vs expected `constraint.rs`
- **Proposed provenance header:** `// port-lint: source constraint.rs` (current: `// port-lint: source constraint.rs`)
- **Proposed provenance header:** `// port-lint: tests constraint.rs` (current: `// port-lint: tests constraint.rs`)
- **Lint issues:** 2

### 6. kasuari-rs.relations

- **Target:** `commonMain.Relations [PROVENANCE-FALLBACK]`
- **Similarity:** 0.81
- **Dependents:** 0
- **Priority Score:** 10601.9
- **Functions:** 3/3 matched (target 9)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 5)
- **Missing types:** `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `relations.rs` vs expected `relations.rs`
- **Proposed provenance header:** `// port-lint: source relations.rs` (current: `// port-lint: source relations.rs`)
- **Lint issues:** 1

### 7. kasuari-rs.solver

- **Target:** `commonMain.Solver [PROVENANCE-FALLBACK]`
- **Similarity:** 0.85
- **Dependents:** 0
- **Priority Score:** 3301.5
- **Functions:** 29/29 matched
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `solver.rs` vs expected `solver.rs`
- **Proposed provenance header:** `// port-lint: source solver.rs` (current: `// port-lint: source solver.rs`)
- **Lint issues:** 1

### 8. kasuari-rs.row

- **Target:** `commonMain.Row [PROVENANCE-FALLBACK]`
- **Similarity:** 0.79
- **Dependents:** 0
- **Priority Score:** 1602.1
- **Functions:** 13/13 matched (target 16)
- **Missing functions:** _none_
- **Types:** 3/3 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `row.rs` vs expected `row.rs`
- **Proposed provenance header:** `// port-lint: source row.rs` (current: `// port-lint: source row.rs`)
- **Lint issues:** 1

### 9. kasuari-rs.error

- **Target:** `commonMain.Error [PROVENANCE-FALLBACK]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 500.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 5/5 matched (target 12)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source error.rs`)
- **Lint issues:** 1

### 10. common.mod

- **Target:** `kotlin.Common [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 410.0
- **Functions:** 3/3 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only by basename: `tests/common/mod.rs` vs expected `../kasuari-rs/tests/common/mod.rs`
- **Proposed provenance header:** `// port-lint: source ../kasuari-rs/tests/common/mod.rs` (current: `// port-lint: source tests/common/mod.rs`)
- **Lint issues:** 1

### 11. tests.quadrilateral

- **Target:** `kotlin.QuadrilateralTest [PROVENANCE-FALLBACK]`
- **Similarity:** 0.94
- **Dependents:** 0
- **Priority Score:** 300.6
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only by basename: `tests/quadrilateral.rs` vs expected `../kasuari-rs/tests/quadrilateral.rs`
- **Proposed provenance header:** `// port-lint: source ../kasuari-rs/tests/quadrilateral.rs` (current: `// port-lint: source tests/quadrilateral.rs`)
- **Lint issues:** 1

### 12. tests.removal

- **Target:** `kotlin.RemovalTest [PROVENANCE-FALLBACK]`
- **Similarity:** 0.85
- **Dependents:** 0
- **Priority Score:** 101.5
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only by basename: `tests/removal.rs` vs expected `../kasuari-rs/tests/removal.rs`
- **Proposed provenance header:** `// port-lint: source ../kasuari-rs/tests/removal.rs` (current: `// port-lint: source tests/removal.rs`)
- **Lint issues:** 1

### 13. kasuari-rs.lib

- **Target:** `commonMain.Lib [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

