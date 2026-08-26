# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 10/10 (100.0%)
- **Function parity:** 107/139 matched (target 307) — 77.0%
- **Class/type parity:** 21/27 matched (target 40) — 77.8%
- **Combined symbol parity:** 128/166 matched (target 347) — 77.1%
- **Average inline-code cosine:** 0.49 (function body across 9 matched files)
- **Average documentation cosine:** 0.71 (doc text across 9 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 7 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. strength

- **Target:** `kasuari.Strength`
- **Similarity:** 0.25
- **Dependents:** 2
- **Priority Score:** 2071707.5
- **Functions:** 9/15 matched (target 92)
- **Missing functions:** `add_assign`, `sub_assign`, `mul`, `mul_assign`, `cmp`, `partial_cmp`
- **Types:** 1/2 matched
- **Missing types:** `Output`

### 2. expression

- **Target:** `kasuari.Expression`
- **Similarity:** 0.25
- **Dependents:** 1
- **Priority Score:** 1091807.5
- **Functions:** 8/16 matched (target 40)
- **Missing functions:** `from_iter`, `neg`, `mul`, `mul_assign`, `add`, `add_assign`, `sub`, `sub_assign`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`

### 3. variable

- **Target:** `kasuari.Variable`
- **Similarity:** 0.38
- **Dependents:** 1
- **Priority Score:** 1083006.1
- **Functions:** 21/28 matched (target 51)
- **Missing functions:** `from_id`, `add`, `add_assign`, `neg`, `sub`, `sub_assign`, `mul`
- **Types:** 1/2 matched
- **Missing types:** `Output`
- **Tests:** 18/19 matched

### 4. term

- **Target:** `kasuari.Term`
- **Similarity:** 0.41
- **Dependents:** 1
- **Priority Score:** 1073005.9
- **Functions:** 22/28 matched (target 50)
- **Missing functions:** `mul`, `mul_assign`, `add`, `add_assign`, `sub`, `sub_assign`
- **Types:** 1/2 matched
- **Missing types:** `Output`
- **Tests:** 16/16 matched

### 5. constraint

- **Target:** `kasuari.Constraint`
- **Similarity:** 0.39
- **Dependents:** 1
- **Priority Score:** 1041106.1
- **Functions:** 4/7 matched (target 21)
- **Missing functions:** `hash`, `eq`, `bitor`
- **Types:** 3/4 matched
- **Missing types:** `Output`

### 6. relations

- **Target:** `kasuari.Relations`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 30609.1
- **Functions:** 1/3 matched (target 8)
- **Missing functions:** `fmt`, `bitor`
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

### 10. lib

- **Target:** `kasuari.Lib [STUB]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

