# User Story 11 — Directed Line Upgrade Plan

## Compute a Directed Upgrade Plan with Cycle Detection

As an **infrastructure planner**, I want to analyse the **directed railway network** to determine a safe **upgrade order of stations**, detecting cycles that prevent a linear upgrade plan.

---

## Introduction

Railway upgrade operations often depend on **directional constraints** between stations (e.g., signalling dependencies, power distribution order).  
The railway network is therefore modelled as a **directed graph**, where:

- **Vertices** represent railway stations  
- **Directed edges** represent upgrade dependencies  

This User Story computes a **Directed Line Upgrade Plan**, identifying whether the graph is **acyclic** and, if so, producing a valid **topological upgrade order**.

---

## Acceptance Criteria

- Railway network is treated as a **directed graph**
- Cycles are detected deterministically
- If the graph is **acyclic**:
  - A valid **upgrade order** is returned
- If cycles exist:
  - All stations involved in cycles are identified
- The result includes:
  - Cycle existence flag
  - Upgrade order
  - Stations in cycles
  - Time complexity
- A **Graphviz DOT representation** of the directed graph is generated

---

## Directed Upgrade Plan Algorithm

The algorithm follows a **graph traversal and dependency analysis** strategy:

1. Traverse the directed graph
2. Detect cycles using DFS with a recursion stack
3. If no cycles are found:
   - Produce a **topological ordering**
4. If cycles exist:
   - Collect all stations involved in cycles
5. Package the result into a `DirectedLineResultDTO`

---

## Diagrams

### System Sequence Diagram (SSD)

![USE11_ssd](USE11_ssd.svg)

### Sequence Diagram (SD)

![USE11_sd](USE11_sd.svg)

### Class Diagram (CD)

![USE11_cd](USE11_cd.svg)

## Results

| Output | Description |
|------|------------|
| **hasCycle** | Indicates whether cycles exist |
| **upgradeOrder** | Valid upgrade order if acyclic |
| **cycleStations** | Stations involved in cycles |
| **DOT file** | Visual representation of the directed graph |


## Complexity Analysis

| Operation | Complexity | Explanation |
|---------|------------|-------------|
| Cycle detection | **O(V + E)** | DFS traversal |
| Topological sort | **O(V + E)** | Standard DAG ordering |
| Graph export | **O(V + E)** | Iterates over vertices and edges |


## Test Coverage

### Test Cases

| Test | Description | Expected Result |
|------|-------------|-----------------|
| **Linear Graph** | 8-node linear chain A→B→...→H | Valid topological order, no cycles |
| **Empty Graph** | Graph with no vertices | Empty upgrade order, no cycles |
| **Single Node** | Graph with only one station | Single node in order, no cycles |
| **Simple Cycle** | 3-node cycle A→B→C→A | Cycle detected, 3 stations marked |
| **Diamond Graph** | A→{B,C}→D structure (acyclic) | Valid topological order with 2 paths |
| **Self-Loop** | Single station A→A | Cycle detected (self-loop) |
| **Parallel Paths** | 2 independent paths A→B→C→E and A→D→E | Valid topological order, no cycles |
| **Multiple Cycles** | 2 independent cycles A→B→A and C→D→C | Both cycles detected, 4 stations marked |
| **Complex DAG** | 10-node acyclic directed graph with cross-edges | Valid topological order maintained |
| **Cycle Links Validation** | 3-node cycle tracking edges | All 3 cycle edges captured in cycleLinks set |
| **Controller - Medium Graph** | 6-node directed graph | Correct delegation to service |
| **Controller - Multiple Cycles** | 4-node graph with 2 independent cycles | Both cycles reported with correct count |

