package controllers;

import domain.RailLine;
import domain.RailNode;
import dto.DirectedLineResultDTO;
import graph.Graph;
import graph.map.MapGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectedLineControllerTest {

    private RailNode n(String id) {
        return new RailNode(id, "Station " + id, 0, 0, 0, 0);
    }

    @Test
    void controllerShouldHandleMediumGraph() {

        Graph<RailNode, RailLine> g = new MapGraph<>(true);

        RailNode s1 = n("S1");
        RailNode s2 = n("S2");
        RailNode s3 = n("S3");
        RailNode s4 = n("S4");
        RailNode s5 = n("S5");
        RailNode s6 = n("S6");

        g.addEdge(s1, s2, new RailLine("S1", "S2", 5, 0, 0));
        g.addEdge(s2, s3, new RailLine("S2", "S3", 3, 0, 0));
        g.addEdge(s3, s4, new RailLine("S3", "S4", 2, 0, 0));
        g.addEdge(s1, s5, new RailLine("S1", "S5", 7, 0, 0));
        g.addEdge(s5, s6, new RailLine("S5", "S6", 1, 0, 0));

        DirectedLineResultDTO result =
                new DirectedLineController().execute(g);

        assertFalse(result.hasCycle());
        assertEquals(6, result.getUpgradeOrder().size());
    }

    @Test
    void controllerShouldReturnCycleInformation() {

        Graph<RailNode, RailLine> g = new MapGraph<>(true);

        RailNode x = n("X");
        RailNode y = n("Y");
        RailNode z = n("Z");

        g.addEdge(x, y, new RailLine("X", "Y", 1, 0, 0));
        g.addEdge(y, z, new RailLine("Y", "Z", 1, 0, 0));
        g.addEdge(z, x, new RailLine("Z", "X", 1, 0, 0));

        DirectedLineResultDTO result =
                new DirectedLineController().execute(g);

        assertTrue(result.hasCycle());
        assertEquals(3, result.getCycleStations().size());
    }
}
