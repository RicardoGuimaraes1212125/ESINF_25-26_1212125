package controllers;

import graph.Graph;
import services.DirectedLineService;
import dto.DirectedLineResultDTO;

/*
* Controller for the Directed Line Upgrade Plan functionality.
* It interacts with the DirectedLineService to process the graph
* and returns the result encapsulated in a DirectedLineResultDTO.
*/

public class DirectedLineController {

    private final DirectedLineService service = new DirectedLineService();

    public DirectedLineResultDTO execute(Graph<String, Double> graph) {
        return service.computeUpgradePlan(graph);
    }
}
