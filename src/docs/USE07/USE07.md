### User Story 07 - Build a balanced 2D-Tree on Latitude/Longitude

As a data engineer, I want a balanced 2D-tree (k = 2) on (latitude, longitude) built deterministically (bulk build using AVL indices) so that range and nearest-neighbour queries run efficiently.

### Introduction

This user story defines the automatic unloading process of wagons into warehouse bays.
The goal is to ensure accurate stock registration and proper product rotation using FEFO (First-Expired, First-Out) and FIFO (First-In, First-Out) principles.
This automation minimizes manual errors, reduces waste, and maintains product traceability across warehouses.


### Acceptance Criteria

- Balanced KD-Tree is built by a bulk (deterministic) algorithm that uses the AVLs from US06.

- If multiple stations share the same coordinates, their bucket preserves all stations sorted by station name (ascending).

- KD-Tree exposes: size, height, distinct bucket sizes; supports rangeSearch(latMin, latMax, lonMin, lonMax) and nearest(lat,lon).

- Complexity analysis included.

### Returns

**3–5 sample queries** and Temporal analysis complexity

### **2. AVL Index Construction**

Three AVL trees must be built:

#### **a) tzTree — Time-Zone / Country / Station Index**
Sorted lexicographically by:
1. `timeZoneGroup`
2. `country`
3. `stationName`

#### **b) latTree — Latitude Index**

`StationByLat.compareTo` sorts by:

1. latitude  
2. longitude  
3. station name  

#### **c) lonTree — Longitude Index**

`StationByLon.compareTo` sorts by:

1. longitude  
2. latitude  
3. station name 

### Diagrams

## System Sequence Diagram

![USE06_ssd](USE06_ssd.svg)

## Class Diagram

![USE06_cd](USE06_cd.svg)

## Sequence Diagram

![USE06_sd](USE06_sd.svg)

#### Done 

**AVL Indexing**  
 - Three balanced trees built in O(N log N)
 - All indexes contain exactly the same number of elements
 - Duplicate coordinates resolved via alphabetical name ordering
 - Guaranteed deterministic traversal order

 **Query Execution**
 - Time-zone group queries return correct stations
 - Window queries retrieve lexicographically between TZ groups
 - Latitude/longitude ranges successfully find Lisboa Oriente
 - All results returned in AVL sorted order

 ## Complexity Analysis

| Operation                              | Complexity          | Explanation |
|----------------------------------------|---------------------|-------------|
| Sorting before building TZ tree        | **O(N log N)**      | Order station list por TZ/country/name. |
| Insert into AVL (N inserts)            | **O(N log N)**      | Each Insert cost log N. |
| Build 3 AVL trees                      | **≈ 3 × O(N log N)**| TZ, Lat, Lon trees. |
| Total index build time                 | **O(N log N)**      | Sorting + inserts dominam. |
| `getStationsByTZGroup(tz)`             | **O(N)**            | Make inOrder. |
| `getStationsByTZWindow(low, high)`     | **O(N)**            | It also sweeps the entire tree. |
| `getStationsByLatitudeRange(min,max)`  | **O(N)**            | Scans AVL of latitudes (complete in-order). |
| `getStationsByLongitudeRange(min,max)` | **O(N)**            | Scan AVL longitudes. |

Since range/window queries operate via in-order traversal of the AVL trees,
their worst-case complexity is linear, while insertions remain logarithmic.

### Test Cases

| **Test Name**                     | **Description**                             | **Expected Result**                     |
|----------------------------------|---------------------------------------------|-----------------------------------------|
| Real CSV Loads                   | Import real dataset                          | ~62142 stations                         |
| Invalid Rows Filtered            | Validate discarded rows                      | No invalid objects                      |
| Lisboa Latitude Query            | Range 38.6–38.8                              | Lisboa Oriente found                    |
| Lisboa Longitude Query           | Range -9.2–-9.0                              | Lisboa Oriente found                    |
| WET/GMT Query                    | TZ group                                     | >1000 stations                          |
| CET→EET Window                   | TZ window                                    | >9000 stations                          |
| Small CSV — CET Count            | Deterministic count                          | Correct                                 |
| Small CSV — Heathrow             | WET/GMT                                      | Heathrow found                          |
| Duplicate Coordinates            | Sorting check                                 | Alphabetical ordering                   |
| AVL Sizes Match                  | tz = lat = lon                                | All equal                               |
| TZ Group Missing                 | Query nonexistent group                       | 0 results                               |
| Latitude Window Empty            | Range with no stations                        | empty result                            |
| Longitude Window Empty           | Range with no stations                        | empty result                            |
| Window Bound Order               | low > high                                    | 0 results                               |
| TZ Window Exact                  | low = high                                    | stations of that TZ                     |
