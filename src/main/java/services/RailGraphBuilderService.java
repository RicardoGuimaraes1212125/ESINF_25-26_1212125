package services;

import domain.RailNode;
import domain.RailLine;
import graph.Graph;
import graph.map.MapGraph;

import java.util.List;
import java.util.Map;

public class RailGraphBuilderService {

    public Graph<RailNode, RailLine> buildDirectedGraph(
            Map<String, RailNode> nodes,
            List<RailLine> lines) {

        Graph<RailNode, RailLine> graph = new MapGraph<>(true);

        // Add all stations
        for (RailNode node : nodes.values()) {
            graph.addVertex(node);
        }

        // Add directed railway lines
        for (RailLine line : lines) {

            RailNode from = nodes.get(line.getFromStationId());
            RailNode to   = nodes.get(line.getToStationId());

            if (from != null && to != null) {
                graph.addEdge(from, to, line);
            }
        }

        return graph;
    }
}
