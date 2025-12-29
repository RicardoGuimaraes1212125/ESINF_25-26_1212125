package services;

import domain.RailNode;
import domain.RailLine;
import graph.Graph;
import graph.map.MapGraph;

import java.util.List;
import java.util.Map;

public class RailGraphBuilderService {

    public Graph<String, Double> buildDirectedGraph(
            Map<String, RailNode> nodes,
            List<RailLine> lines) {

        Graph<String, Double> graph = new MapGraph<>(true);

        // Add all stations as vertices
        for (RailNode node : nodes.values()) {
            graph.addVertex(node.toString());
        }

        // Add all directed railway lines
        for (RailLine line : lines) {

            RailNode from = nodes.get(line.getFromStationId());
            RailNode to   = nodes.get(line.getToStationId());

            if (from != null && to != null) {
                graph.addEdge(
                        from.toString(),
                        to.toString(),
                        line.getDistance()
                );
            }
        }

        return graph;
    }
}
