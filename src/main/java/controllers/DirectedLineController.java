package controllers;

import graph.Graph;
import domain.RailNode;
import domain.RailLine;
import services.DirectedLineService;
import dto.DirectedLineResultDTO;

public class DirectedLineController {

    private final DirectedLineService service = new DirectedLineService();

    public DirectedLineResultDTO execute(Graph<RailNode, RailLine> graph) {
        return service.computeUpgradePlan(graph);
    }
}
