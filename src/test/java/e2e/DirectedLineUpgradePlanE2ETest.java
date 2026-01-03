package e2e;

import controllers.DirectedLineController;
import dto.DirectedLineResultDTO;
import domain.RailLine;
import domain.RailNode;
import graph.Graph;
import graph.map.MapGraph;
import org.junit.jupiter.api.Test;
import utils.GraphvizExporter;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class DirectedLineUpgradePlanE2ETest {

    @Test
    void endToEnd_US11_DirectedLineUpgradePlan() throws Exception {

        Graph<RailNode, RailLine> graph = new MapGraph<>(true);

        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");

        RailLine ab = l("A","B",5);
        RailLine bc = l("B","C",3);
        RailLine ca = l("C","A",2); // cycle
        RailLine cd = l("C","D",4);

        graph.addEdge(a, b, ab);
        graph.addEdge(b, c, bc);
        graph.addEdge(c, a, ca);
        graph.addEdge(c, d, cd);

        DirectedLineController controller = new DirectedLineController();

        // ---------- Act ----------
        DirectedLineResultDTO result =
                controller.execute(graph);

        GraphvizExporter.exportDirectedGraph(
                graph,
                result.getCycleStations(),
                "target/upgrade_plan_e2e.dot"
        );

        assertNotNull(result);
        assertTrue(result.hasCycle());
        assertEquals(1, result.getCycleCount()); // 1 ciclo detectado
        assertEquals(3, result.getCycleStations().size());
        assertTrue(result.getCycleStations().contains(a));
        assertTrue(result.getCycleStations().contains(b));
        assertTrue(result.getCycleStations().contains(c));
        assertFalse(result.getCycleStations().contains(d));
        assertTrue(result.getCycleLinks().contains(ab) || result.getCycleLinks().contains(bc) || result.getCycleLinks().contains(ca));

        File dot = new File("target/upgrade_plan_e2e.dot");
        assertTrue(dot.exists());
    }

    private RailNode n(String id) {
        return new RailNode(id, id, 0, 0, 0, 0);
    }

    private RailLine l(String a, String b, double d) {
        return new RailLine(a, b, d, 1, d);
    }
}

