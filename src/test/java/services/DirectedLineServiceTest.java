package services;

import dto.DirectedLineResultDTO;
import graph.Graph;
import graph.map.MapGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectedLineServiceTest {

    private Graph<String, Double> newDirectedGraph() {
        return new MapGraph<>(true);
    }

    @Test
    void largeLinearGraphShouldReturnValidTopologicalOrder() {

        Graph<String, Double> g = newDirectedGraph();

        for (char c = 'A'; c <= 'H'; c++) {
            g.addVertex(String.valueOf(c));
        }

        g.addEdge("A", "B", 1.0);
        g.addEdge("B", "C", 1.0);
        g.addEdge("C", "D", 1.0);
        g.addEdge("D", "E", 1.0);
        g.addEdge("E", "F", 1.0);
        g.addEdge("F", "G", 1.0);
        g.addEdge("G", "H", 1.0);

        DirectedLineService service = new DirectedLineService();
        DirectedLineResultDTO result = service.computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(8, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().indexOf("A")
                < result.getUpgradeOrder().indexOf("H"));
    }

    @Test
    void treeLikeGraphShouldReturnValidOrder() {

        Graph<String, Double> g = newDirectedGraph();

        g.addEdge("A", "B", 1.0);
        g.addEdge("A", "C", 1.0);
        g.addEdge("B", "D", 1.0);
        g.addEdge("B", "E", 1.0);
        g.addEdge("C", "F", 1.0);
        g.addEdge("C", "G", 1.0);

        DirectedLineService service = new DirectedLineService();
        DirectedLineResultDTO result = service.computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(7, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().indexOf("A") <
                   result.getUpgradeOrder().indexOf("D"));
    }

    @Test
    void partialCycleShouldReturnOnlyCycleStations() {

        Graph<String, Double> g = newDirectedGraph();

        g.addEdge("A", "B", 1.0);
        g.addEdge("B", "C", 1.0);
        g.addEdge("C", "A", 1.0); // cycle
        g.addEdge("C", "D", 1.0);
        g.addEdge("D", "E", 1.0);

        DirectedLineResultDTO result =
                new DirectedLineService().computeUpgradePlan(g);

        assertTrue(result.hasCycle());
        assertEquals(3, result.getCycleStations().size());
        assertTrue(result.getCycleStations().contains("A"));
        assertTrue(result.getCycleStations().contains("B"));
        assertTrue(result.getCycleStations().contains("C"));
        assertFalse(result.getCycleStations().contains("D"));
    }

    @Test
    void multipleCyclesShouldReturnDetectedCycleStations() {

        Graph<String, Double> g = newDirectedGraph();

        // Cycle 1
        g.addEdge("A", "B", 1.0);
        g.addEdge("B", "A", 1.0);

        // Cycle 2
        g.addEdge("C", "D", 1.0);
        g.addEdge("D", "E", 1.0);
        g.addEdge("E", "C", 1.0);

        DirectedLineResultDTO result =
                new DirectedLineService().computeUpgradePlan(g);

        assertTrue(result.hasCycle());
        assertTrue(result.getCycleStations().size() >= 2);
    }

    @Test
    void disconnectedGraphShouldReturnAllVerticesInOrder() {

        Graph<String, Double> g = newDirectedGraph();

        g.addEdge("A", "B", 1.0);
        g.addEdge("C", "D", 1.0);
        g.addVertex("E");

        DirectedLineResultDTO result =
                new DirectedLineService().computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(5, result.getUpgradeOrder().size());
    }

    @Test
    void emptyGraphShouldReturnEmptyOrder() {

        Graph<String, Double> g = newDirectedGraph();

        DirectedLineResultDTO result =
                new DirectedLineService().computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertTrue(result.getUpgradeOrder().isEmpty());
    }

    @Test
    void singleVertexGraphShouldReturnSingleElementOrder() {

        Graph<String, Double> g = newDirectedGraph();
        g.addVertex("A");

        DirectedLineResultDTO result =
                new DirectedLineService().computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(1, result.getUpgradeOrder().size());
        assertEquals("A", result.getUpgradeOrder().get(0));
    }
}

