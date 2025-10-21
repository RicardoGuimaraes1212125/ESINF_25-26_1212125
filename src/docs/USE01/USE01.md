### User Story 01 -  Unloading Wagons

As a terminal operator, I want unloading operations of wagons to
automatically store inventory using FEFO and/or FIFO logic, so that I can ensure
the correct dispatch order and minimize product spoilage.


### Acceptance Criteria

1. Stock initialization/unloading wagons - The file wagons.csv contains the available stock per SKU. Each wagon’s contents should be assigned to an aisle/bay,
and their products must be inserted into the correct position inside the bay
accordingly to the following rules:
(a) boxes are ordered by expiry date (earliest first; null last)
(b) receivedAt (oldest first)
(c) boxId ASC (tie-break)

2. Dispatch operation - The operation dispatch must always consume stock from
the “front” of the bay list, guaranteeing FEFO/FIFO behaviour. If a bay
becomes empty, it remains in the WMS (the bay still exists), but the box list
is empty; only delete empty boxes, not bays. Partial dispatch across multiple
bays must be supported: if the target bay runs out, continue in the next bay,
ascending number, that holds that SKU.

3. Relocation - The operation relocation must update a box’s warehouseId/aisle/bay
only; do not re-sort its new bay if its expiryDate/receivedAt is unchanged except for inserting it into the new bay’s FEFO position.

#### Done 

*Unloading*: wagons.csv is processed, boxes assigned to bays, and correctly ordered by expiry → receivedAt → boxId.

*Dispatch*: Stock is consumed from the front of bay lists, across multiple bays if needed, leaving empty bays intact but without boxes.

*Relocation*: Boxes can be moved to a new bay/aisle/warehouse and appear in the correct FEFO position without disrupting others.

*Quality*: Operations run reliably, maintain traceability, preserve FEFO/FIFO logic, and all tests/acceptance checks pass.


### Test Cases

No response