package controllers;

import domain.RailLine;
import domain.RailNode;
import graph.Graph;
import services.MinimalBackboneService;

public class MinimalBackboneController {

    private final MinimalBackboneService service = new MinimalBackboneService();

    public Graph<RailNode, RailLine> computeMinimalBackbone(Graph<RailNode, RailLine> graph) {
        return service.computeMinimalBackbone(graph);
    }
}
