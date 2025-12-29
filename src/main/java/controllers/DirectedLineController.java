package controllers;

import graph.Graph;
import services.DirectedLineService;
import dto.DirectedLineResultDTO;

public class DirectedLineController {

    private final DirectedLineService service = new DirectedLineService();

    public DirectedLineResultDTO execute(Graph<String, Double> graph) {
        return service.computeUpgradePlan(graph);
    }
}

