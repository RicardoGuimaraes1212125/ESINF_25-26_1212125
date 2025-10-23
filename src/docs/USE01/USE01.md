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


### Diagrams

## System Sequence Diagram

![USE01_ssd](USE01_ssd.png)

## Class Diagram

![USE01_cd](USE01_cd.png)

## Sequence Diagram

![USE01_sd](USE01_sd.png)

#### Done 

*Unloading*: wagons.csv is processed, boxes assigned to bays, and correctly ordered by expiry → receivedAt → boxId.

*Dispatch*: Stock is consumed from the front of bay lists, across multiple bays if needed, leaving empty bays intact but without boxes.

*Relocation*: Boxes can be moved to a new bay/aisle/warehouse and appear in the correct FEFO position without disrupting others.

*Quality*: Operations run reliably, maintain traceability, preserve FEFO/FIFO logic, and all tests/acceptance checks pass.


### Test Cases

| **Test Name** | **Description** | **Expected Result** |
|----------------|-----------------|---------------------|
| **Controller Initialization** | Check if the controller starts correctly with a warehouse. | Controller is created successfully. |
| **Delegates to Service** | Ensure the controller runs the service to unload wagons. | Log shows “Stored box”. |
| **Validation Errors** | Test if invalid wagons trigger error messages. | Log shows “Validation errors”. |
| **Empty Warehouse** | Run when there are no wagons or bays. | Log shows “No wagons or bays available.” |
| **Inventory Updated** | Check if warehouse inventory updates after unload. | Boxes appear in warehouse inventory. |
| **Multiple Wagons** | Unload several wagons at once. | Log shows multiple “Stored box” messages. |
| **Multiple Warehouses** | Unload wagons into bays of different warehouses. | Boxes are stored across both warehouses. |
| **Unknown SKU** | Try unloading a wagon with a non-existing SKU. | Log shows “Unknown SKU”. |
| **Invalid Quantity** | Try unloading a wagon with zero or negative quantity. | Log shows “Invalid quantity”. |
| **Invalid Expiry Date** | Wagon has invalid expiry date format. | Log shows “Invalid expiry date”. |
| **Missing Received Date** | Wagon has no received date. | Log shows “Missing received date”. |
| **Successful Unload** | Normal unload with valid data. | Log shows “Stored box” and box is saved. |
| **FEFO Order** | Boxes sorted by expiry date (earliest first). | Correct FEFO order in bays. |
| **FIFO Order** | Boxes without expiry sorted by received date. | Correct FIFO order in bays. |
| **Bay Capacity Limit** | Bays full — try adding more boxes. | Log shows “No space available.” |
| **Inventory Indexed** | After unload, check inventory map. | SKU appears in inventory list. |
| **Large Number of Wagons** | Stress test with many wagons. | Logs show “Total boxes to unload” and many “Stored box” lines. |
| **Expired Items** | Include expired and valid wagons. | Expired boxes handled without crash. |
| **Summary Log** | Check if logs end with summary info. | Log includes “Total boxes to unload”. |
| **Validation Failure Stops Storage** | Invalid data should stop box storage. | No boxes stored; log shows “Validation errors”. |