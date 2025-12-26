package services;

import domain.StationConnection;
import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RailGraphBuilderServiceTest {

    @Test
    void shouldBuildGraphWithManyStations() {

        List<StationConnection> connections = List.of(
                new StationConnection("", "1", "Brussels", "2", "Antwerp", 45.0, ""),
                new StationConnection("", "2", "Antwerp", "3", "Ghent", 60.0, ""),
                new StationConnection("", "3", "Ghent", "4", "Bruges", 25.0, ""),
                new StationConnection("", "1", "Brussels", "5", "Namur", 65.0, ""),
                new StationConnection("", "5", "Namur", "6", "Arlon", 90.0, "")
        );

        RailGraphBuilderService builder = new RailGraphBuilderService();
        Graph<String, Double> graph = builder.buildDirectedGraph(connections);

        assertEquals(6, graph.numVertices());

        assertEquals(5, graph.numEdges());

        String brussels = "1 | Brussels";
        String antwerp  = "2 | Antwerp";
        String namur    = "5 | Namur";

        assertTrue(graph.adjVertices(brussels).contains(antwerp));
        assertTrue(graph.adjVertices(brussels).contains(namur));
    }
}
