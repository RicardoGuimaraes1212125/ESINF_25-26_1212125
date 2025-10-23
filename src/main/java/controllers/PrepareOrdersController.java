package controllers;

import domain.AllocationMode;
import domain.Warehouse;
import dto.PrepareOrdersDTO;
import dto.AllocationRowDTO;
import services.PrepareOrdersService;
import java.util.List;

public class PrepareOrdersController {

    private final Warehouse warehouse;
    private final PrepareOrdersService service = new PrepareOrdersService();

    public PrepareOrdersController(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public PrepareResultDTO execute(AllocationMode mode) {
        PrepareOrdersService.PrepareResult result = service.prepareOrders(warehouse, mode);
        return new PrepareResultDTO(result.summaries, result.allocations);
    }

    public static class PrepareResultDTO {
        private final List<PrepareOrdersDTO> summaries;
        private final List<AllocationRowDTO> allocations;

        public PrepareResultDTO(List<PrepareOrdersDTO> summaries, List<AllocationRowDTO> allocations) {
            this.summaries = summaries;
            this.allocations = allocations;
        }

        public List<PrepareOrdersDTO> getSummaries() {
            return summaries;
        }

        public List<AllocationRowDTO> getAllocations() {
            return allocations;
        }
    }
}
