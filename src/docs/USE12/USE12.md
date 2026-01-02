# User Story 12 — Minimal Backbone Network

## Compute a Minimal Backbone Network (Undirected MST)

As an **infrastructure planner**, I want to compute a **minimal railway backbone** that connects all stations with **minimum total track length**, to estimate maintenance or expansion costs.

---

## Introduction

To estimate a **cost baseline** for railway infrastructure planning, it is necessary to extract a **minimal subset of tracks** that connects all reachable stations.

The railway network is treated as an **undirected weighted graph**, where:
- Vertices represent stations
- Edge weights represent track distances

The goal is to compute a **Minimum Spanning Tree (MST)**, referred to as the **Minimal Backbone Network**.

---

## Acceptance Criteria

- Railway graph is treated as **undirected**
- All stations are connected with **minimum total distance**
- Resulting graph contains:
  - Same number of vertices
  - Exactly **V − 1 edges** (when connected)
- DOT file places each station at its **XY coordinates**
- SVG file generated using Graphviz **neato**
- Complexity analysis included

---

## Minimal Backbone Algorithm

A **Prim-style greedy algorithm** is used:

1. Start from an arbitrary station
2. Repeatedly select the **minimum-distance edge**
   connecting the current tree to an unvisited station
3. Add the edge and station to the backbone
4. Repeat until all stations are included

---

## Diagrams

### System Sequence Diagram (SSD)

![USE12_ssd](USE12_ssd.svg)

### Sequence Diagram (SD)

![USE12_sd](USE12_sd.svg)

### Class Diagram (CD)

![USE12_cd](USE12_cd.svg)

## Results

| Metric | Value |
|------|------|
| **Vertices** | Same as original graph |
| **Edges** | V − 1 |
| **Graph Type** | Undirected |
| **Output** | DOT + SVG |

---

## Complexity Analysis

| Operation | Complexity | Explanation |
|---------|------------|-------------|
| MST computation | **O(V × E)** | Naive Prim implementation |
| Graph export | **O(V + E)** | Iteration over graph |


---

## Test Coverage

- Correct number of vertices preserved
- Exactly V − 1 edges produced
- No cycles created
- Equal-weight edges handled
- Dense graphs
- End-to-end test via UI execution

