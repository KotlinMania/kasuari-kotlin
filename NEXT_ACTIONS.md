# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 10/10 (100.0%)
- **Function parity:** 72/139 matched (target 182) — 51.8%
- **Class/type parity:** 21/27 matched (target 36) — 77.8%
- **Combined symbol parity:** 93/166 matched (target 218) — 56.0%
- **Average inline-code cosine:** 0.43 (function body across 9 matched files)
- **Average documentation cosine:** 0.72 (doc text across 9 matched files)
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

- **Target:** `Strength`
- **Similarity:** 0.25
- **Dependents:** 2
- **Priority Score:** 2071707.5
- **Functions:** 9/15 matched (target 18)
- **Missing functions:** `add_assign`, `sub_assign`, `mul`, `mul_assign`, `cmp`, `partial_cmp`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`

### 2. variable

- **Target:** `Variable`
- **Similarity:** 0.08
- **Dependents:** 1
- **Priority Score:** 1263009.2
- **Functions:** 3/28 matched (target 33)
- **Missing functions:** `from_id`, `add`, `add_assign`, `neg`, `sub`, `sub_assign`, `mul`, `variable_default`, `variable_add_f64`, `variable_add_f32`, `variable_add_variable`, `variable_add_term`, `variable_add_expression`, `variable_add_assign`, `variable_sub_f64`, `variable_sub_f32`, `variable_sub_variable`, `variable_sub_term`, `variable_sub_expression`, `variable_sub_assign`, `variable_mul_f64`, `variable_mul_f32`, `variable_div_f64`, `variable_div_f32`, `variable_neg`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`
- **Tests:** 0/19 matched

### 3. term

- **Target:** `Term`
- **Similarity:** 0.13
- **Dependents:** 1
- **Priority Score:** 1243008.6
- **Functions:** 5/28 matched (target 31)
- **Missing functions:** `mul`, `mul_assign`, `add`, `add_assign`, `neg`, `sub`, `sub_assign`, `mul_f64`, `mul_f32`, `mul_assign_f64`, `mul_assign_f32`, `div_f64`, `div_f32`, `div_assign_f64`, `div_assign_f32`, `add_f64`, `add_f32`, `add_term`, `add_expression`, `sub_f64`, `sub_f32`, `sub_term`, `sub_expression`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`
- **Tests:** 0/16 matched

### 4. expression

- **Target:** `Expression`
- **Similarity:** 0.25
- **Dependents:** 1
- **Priority Score:** 1091807.5
- **Functions:** 8/16 matched (target 40)
- **Missing functions:** `from_iter`, `neg`, `mul`, `mul_assign`, `add`, `add_assign`, `sub`, `sub_assign`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`

### 5. constraint

- **Target:** `Constraint`
- **Similarity:** 0.39
- **Dependents:** 1
- **Priority Score:** 1041106.1
- **Functions:** 4/7 matched (target 12)
- **Missing functions:** `hash`, `eq`, `bitor`
- **Types:** 3/4 matched (target 3)
- **Missing types:** `Output`

### 6. relations

- **Target:** `Relations`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 30609.1
- **Functions:** 1/3 matched
- **Missing functions:** `fmt`, `bitor`
- **Types:** 2/3 matched (target 5)
- **Missing types:** `Output`

### 7. solver

- **Target:** `Solver`
- **Similarity:** 0.85
- **Dependents:** 0
- **Priority Score:** 3301.5
- **Functions:** 29/29 matched
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_

### 8. row

- **Target:** `Row`
- **Similarity:** 0.79
- **Dependents:** 0
- **Priority Score:** 1602.1
- **Functions:** 13/13 matched (target 16)
- **Missing functions:** _none_
- **Types:** 3/3 matched
- **Missing types:** _none_

### 9. error

- **Target:** `Error`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 500.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 5/5 matched (target 12)
- **Missing types:** _none_

### 10. lib

- **Target:** `Lib [STUB]`
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

