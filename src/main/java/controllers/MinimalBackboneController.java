package controllers;

import graph.Graph;
import services.MinimalBackboneService;

public class MinimalBackboneController {

    private final MinimalBackboneService service;

    public MinimalBackboneController() {
        this.service = new MinimalBackboneService();
    }

    public Graph<String, Double> computeMinimalBackbone(Graph<String, Double> graph) {
        return service.computeMinimalBackbone(graph);
    }
}

