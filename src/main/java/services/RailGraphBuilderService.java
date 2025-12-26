package services;

import domain.StationConnection;
import graph.Graph;
import graph.map.MapGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Service class for building a directed rail graph from station connection data. 
* Each station is represented as a vertex, and each connection as a directed edge with length as weight.
* The vertex keys are formatted as "StationID | StationName" to ensure uniqueness.
*/
public class RailGraphBuilderService {

    public Graph<String, Double> buildDirectedGraph(List<StationConnection> connections) {

        Graph<String, Double> graph = new MapGraph<>(true);
        Map<String, String> stationIds = new HashMap<>();

        for (StationConnection c : connections) {

            String fromKey = c.getStationFromId() + " | " + c.getStationFromName();
            String toKey   = c.getStationToId()   + " | " + c.getStationToName();

            stationIds.putIfAbsent(fromKey, fromKey);
            stationIds.putIfAbsent(toKey, toKey);

            graph.addVertex(fromKey);
            graph.addVertex(toKey);
            graph.addEdge(fromKey, toKey, c.getLengthKm());
        }

        return graph;
    }
}

