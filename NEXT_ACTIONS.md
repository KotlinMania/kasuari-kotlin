# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 9/10 (90.0%)
- **Function parity:** 138/139 matched (target 409) — 99.3%
- **Class/type parity:** 21/27 matched (target 39) — 77.8%
- **Combined symbol parity:** 159/166 matched (target 448) — 95.8%
- **Average inline-code cosine:** 0.70 (function body across 9 matched files)
- **Average documentation cosine:** 0.72 (doc text across 9 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. strength

- **Target:** `kasuari.Strength`
- **Similarity:** 0.48
- **Dependents:** 2
- **Priority Score:** 2011705.2
- **Functions:** 15/15 matched (target 102)
- **Missing functions:** _none_
- **Types:** 1/2 matched
- **Missing types:** `Output`

### 2. variable

- **Target:** `kasuari.Variable`
- **Similarity:** 0.63
- **Dependents:** 1
- **Priority Score:** 1023003.7
- **Functions:** 27/28 matched (target 70)
- **Missing functions:** `from_id`
- **Types:** 1/2 matched
- **Missing types:** `Output`
- **Tests:** 18/19 matched

### 3. term

- **Target:** `kasuari.Term`
- **Similarity:** 0.67
- **Dependents:** 1
- **Priority Score:** 1013003.4
- **Functions:** 28/28 matched (target 70)
- **Missing functions:** _none_
- **Types:** 1/2 matched
- **Missing types:** `Output`
- **Tests:** 16/16 matched

### 4. expression

- **Target:** `kasuari.Expression`
- **Similarity:** 0.78
- **Dependents:** 1
- **Priority Score:** 1011802.2
- **Functions:** 16/16 matched (target 70)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`

### 5. constraint

- **Target:** `kasuari.Constraint`
- **Similarity:** 0.70
- **Dependents:** 1
- **Priority Score:** 1011102.9
- **Functions:** 7/7 matched (target 33)
- **Missing functions:** _none_
- **Types:** 3/4 matched
- **Missing types:** `Output`

### 6. relations

- **Target:** `kasuari.Relations`
- **Similarity:** 0.36
- **Dependents:** 0
- **Priority Score:** 10606.4
- **Functions:** 3/3 matched (target 19)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 5)
- **Missing types:** `Output`

### 7. solver

- **Target:** `kasuari.Solver`
- **Similarity:** 0.85
- **Dependents:** 0
- **Priority Score:** 3301.5
- **Functions:** 29/29 matched
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_

### 8. row

- **Target:** `kasuari.Row`
- **Similarity:** 0.79
- **Dependents:** 0
- **Priority Score:** 1602.1
- **Functions:** 13/13 matched (target 16)
- **Missing functions:** _none_
- **Types:** 3/3 matched
- **Missing types:** _none_

### 9. error

- **Target:** `kasuari.Error`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 500.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 5/5 matched (target 12)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

