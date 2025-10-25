### User Story

As a warehouse planner, when I receive open orders, I want the
system to examine current inventory and allocate quantities from boxes in FEFO
order, and produce per-line statuses (ELIGIBLE, PARTIAL, UNDISPATCHABLE)
and a list of allocation rows with box and bay information.

### Acceptance Criteria

1. Orders are processed by: priority ASC, dueDate ASC,
orderId ASC. Within an order, lines are processed by lineNo ASC (input order).
For each line, allocation walks the SKU’s boxes in FEFO/FIFO order.
For each visited bay, allocate take min(remainingQty, box.qtyAvailable), reduce
remainingQty, and continue until the request is satisfied or boxes end. The available
box quantity never goes below zero during planning.
The system supports two modes: flag eligibility strict, partial, the default is
strict:
- strict: a line is ELIGIBLE only if its entire requested quantity is allocated;
otherwise it is UNDISPATCHABLE (no allocations for that line are kept).
- partial: if 0 ¡ allocated ¡ requested, mark the line PARTIAL and keep the
allocated portion; if allocated = 0, mark UNDISPATCHABLE.

### Returns

- a list of orders eligibility with: orderId, lineNo, sku, requestedQty, allocatedQty, status;
- output allocations (one row per allocation fragment): orderId, lineNo, sku,
qty, boxId, aisle, bay.


### Diagrams

## System Sequence Diagram

![USE02_ssd](USE02_ssd.svg)

## Class Diagram

![USE02_cd](USE02_cd.svg)

## Sequence Diagram

![USE02_sd](USE02_sd.svg)

#### Done 



### Test Cases

| **Test Name** | **Description** | **Expected Result** |
|----------------|-----------------|---------------------|
| **Controller Returns Valid Result Object** | Checks if the controller returns a valid result with summaries and allocations. | A non-null result with filled summaries and allocations. |
| **Executes Strict Mode Successfully** | Runs strict allocation with enough stock. | All lines eligible, one summary and one allocation created. |
| **Executes Partial Mode Successfully** | Runs partial allocation when stock is insufficient. | Line marked as PARTIAL, with partial quantity allocated. |
| **Handles Empty Warehouse Gracefully** | Runs with an empty warehouse (no data). | Returns empty summaries and allocations without errors. |
| **Produces Same Results As Service** | Compares controller and service direct outputs. | Both produce the same summaries and allocations. |
| **Result DTO Stores Data Correctly** | Checks if DTO fields are filled correctly after execution. | DTO contains correct order ID, SKU, and box information. |
| **Strict Mode - Fully Eligible** | Tests full allocation under strict mode. | Line marked ELIGIBLE; full quantity allocated. |
| **Partial Mode - Partially Allocates** | Tests allocation with limited stock under partial mode. | Partial quantity allocated; status is PARTIAL. |
| **Strict Mode - Reverts Partial Allocations** | Tests strict mode with insufficient stock. | Line marked UNDISPATCHABLE; box quantity unchanged. |
| **No Orders Produces Empty Results** | Runs when there are no orders in the warehouse. | Both summaries and allocations lists are empty. |
| **Orders Sorted By Priority And Date** | Tests correct sorting of orders by priority/date. | Higher-priority orders are processed first. |
| **Expired Boxes Are Ignored** | Ensures expired boxes aren’t used for allocation. | No allocations created; line marked UNDISPATCHABLE. |
| **Invalid Expiry Date Is Accepted** | Checks handling of invalid expiry date format. | Box still used; line marked ELIGIBLE. |
| **Box Removed When Emptied** | Tests if boxes are deleted after full use. | Box list becomes empty after allocation. |
| **Boxes Removed When Fully Used** | Confirms that empty boxes are removed after use. | All boxes removed from the bay. |
| **Allocation Row Data Is Correct** | Verifies allocation data content (box, SKU, qty). | Allocation row contains correct order, SKU, qty, and box ID. |
| **Unknown SKU Becomes Undispatchable** | Tests behavior with a non-existing SKU in the order. | No allocations; line marked UNDISPATCHABLE. |
| **Multiple Lines In Same Order** | Handles multiple SKUs in a single order. | Both lines processed and marked ELIGIBLE. |
| **Multiple Orders Competing For Same Stock** | Tests allocation order when stock is limited. | First order ELIGIBLE, second order PARTIAL. |
| **No Bays Available** | Tests behavior when no bays exist in warehouse. | No allocations created. |
| **Warehouse Inventory Index Updated** | Checks if warehouse index updates after allocation. | SKU boxes removed or list becomes empty. |

