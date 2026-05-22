# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 10/10 (100.0%)
- **Function parity:** 104/139 matched (target 294) — 74.8%
- **Class/type parity:** 21/27 matched (target 36) — 77.8%
- **Combined symbol parity:** 125/166 matched (target 330) — 75.3%
- **Average inline-code cosine:** 0.69 (function body across 9 matched files)
- **Average documentation cosine:** 0.62 (doc text across 9 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 4 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. strength

- **Target:** `Strength`
- **Similarity:** 0.49
- **Dependents:** 2
- **Priority Score:** 2011705.1
- **Functions:** 15/15 matched (target 26)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`

### 2. variable

- **Target:** `Variable`
- **Similarity:** 0.46
- **Dependents:** 1
- **Priority Score:** 1203005.4
- **Functions:** 9/28 matched (target 67)
- **Missing functions:** `from_id`, `variable_default`, `variable_add_f64`, `variable_add_f32`, `variable_add_variable`, `variable_add_term`, `variable_add_expression`, `variable_add_assign`, `variable_sub_f64`, `variable_sub_f32`, `variable_sub_variable`, `variable_sub_term`, `variable_sub_expression`, `variable_sub_assign`, `variable_mul_f64`, `variable_mul_f32`, `variable_div_f64`, `variable_div_f32`, `variable_neg`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`
- **Tests:** 0/19 matched

### 3. term

- **Target:** `Term`
- **Similarity:** 0.48
- **Dependents:** 1
- **Priority Score:** 1173005.2
- **Functions:** 12/28 matched (target 66)
- **Missing functions:** `mul_f64`, `mul_f32`, `mul_assign_f64`, `mul_assign_f32`, `div_f64`, `div_f32`, `div_assign_f64`, `div_assign_f32`, `add_f64`, `add_f32`, `add_term`, `add_expression`, `sub_f64`, `sub_f32`, `sub_term`, `sub_expression`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`
- **Tests:** 0/16 matched

### 4. expression

- **Target:** `Expression`
- **Similarity:** 0.66
- **Dependents:** 1
- **Priority Score:** 1011803.4
- **Functions:** 16/16 matched (target 62)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Output`

### 5. constraint

- **Target:** `Constraint`
- **Similarity:** 0.69
- **Dependents:** 1
- **Priority Score:** 1011103.1
- **Functions:** 7/7 matched (target 19)
- **Missing functions:** _none_
- **Types:** 3/4 matched (target 3)
- **Missing types:** `Output`

### 6. relations

- **Target:** `Relations`
- **Similarity:** 0.81
- **Dependents:** 0
- **Priority Score:** 10601.9
- **Functions:** 3/3 matched (target 9)
- **Missing functions:** _none_
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

