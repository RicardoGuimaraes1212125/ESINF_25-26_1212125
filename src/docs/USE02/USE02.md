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



#### Done 



### Test Cases

