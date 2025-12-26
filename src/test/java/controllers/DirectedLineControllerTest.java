package controllers;

import dto.DirectedLineResultDTO;
import graph.Graph;
import graph.map.MapGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectedLineControllerTest {

    @Test
    void controllerShouldHandleMediumGraph() {

        Graph<String, Double> g = new MapGraph<>(true);

        g.addEdge("S1", "S2", 5.0);
        g.addEdge("S2", "S3", 3.0);
        g.addEdge("S3", "S4", 2.0);
        g.addEdge("S1", "S5", 7.0);
        g.addEdge("S5", "S6", 1.0);

        DirectedLineController controller = new DirectedLineController();
        DirectedLineResultDTO result = controller.execute(g);

        assertFalse(result.hasCycle());
        assertEquals(6, result.getUpgradeOrder().size());
    }

    @Test
    void controllerShouldReturnCycleInformation() {

        Graph<String, Double> g = new MapGraph<>(true);

        g.addEdge("X", "Y", 1.0);
        g.addEdge("Y", "Z", 1.0);
        g.addEdge("Z", "X", 1.0);

        DirectedLineResultDTO result =
                new DirectedLineController().execute(g);

        assertTrue(result.hasCycle());
        assertEquals(3, result.getCycleStations().size());
    }
}

