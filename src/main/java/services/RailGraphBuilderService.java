package services;

import domain.StationConnection;
import graph.Graph;
import graph.map.MapGraph;

import java.util.List;

/*
* Service class for building a directed graph from station connections.
* It uses the MapGraph implementation to create the graph structure.
*/
public class RailGraphBuilderService {

    public Graph<String, Double> buildDirectedGraph(List<StationConnection> connections) {

        Graph<String, Double> graph = new MapGraph<>(true);

        for (StationConnection c : connections) {
            String from = c.getStationFromName();
            String to   = c.getStationToName();
            double dist = c.getLengthKm();

            graph.addVertex(from);
            graph.addVertex(to);
            graph.addEdge(from, to, dist);
        }

        return graph;
    }
}

