package us11;

import controllers.DirectedLineController;
import domain.StationConnection;
import dto.DirectedLineResultDTO;
import graph.Graph;
import org.junit.jupiter.api.Test;
import services.RailGraphBuilderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class US11AcceptanceTest {

    @Test
    void shouldComputeUpgradeOrderWhenNoCyclesExist() {

        // GIVEN — Belgian railway dependencies
        List<StationConnection> connections = List.of(
                new StationConnection("", "1", "Brussels", "2", "Antwerp", 45, ""),
                new StationConnection("", "2", "Antwerp", "3", "Ghent", 60, ""),
                new StationConnection("", "3", "Ghent", "4", "Bruges", 25, ""),
                new StationConnection("", "1", "Brussels", "5", "Namur", 65, ""),
                new StationConnection("", "5", "Namur", "6", "Arlon", 90, "")
        );

        RailGraphBuilderService builder = new RailGraphBuilderService();
        Graph<String, Double> graph = builder.buildDirectedGraph(connections);

        DirectedLineController controller = new DirectedLineController();

        // WHEN — executing US11
        DirectedLineResultDTO result = controller.execute(graph);

        // THEN — no cycles
        assertFalse(result.hasCycle());

        // all stations must be in the ordering
        assertEquals(6, result.getUpgradeOrder().size());

        // Brussels must come before all its dependents
        int brussels = result.getUpgradeOrder().indexOf("1 | Brussels");
        int antwerp  = result.getUpgradeOrder().indexOf("2 | Antwerp");
        int namur    = result.getUpgradeOrder().indexOf("5 | Namur");

        assertTrue(brussels < antwerp);
        assertTrue(brussels < namur);

        // complexity must be reported
        assertNotNull(result.getComplexity());
        assertTrue(result.getComplexity().contains("O("));
    }

    @Test
    void shouldDetectCyclesAndReturnStationsInvolved() {

        List<StationConnection> connections = List.of(
                new StationConnection("", "1", "Brussels", "2", "Antwerp", 45, ""),
                new StationConnection("", "2", "Antwerp", "3", "Ghent", 60, ""),
                new StationConnection("", "3", "Ghent", "1", "Brussels", 70, ""), // cycle
                new StationConnection("", "1", "Brussels", "5", "Namur", 65, ""),
                new StationConnection("", "5", "Namur", "6", "Arlon", 90, "")
        );

        RailGraphBuilderService builder = new RailGraphBuilderService();
        Graph<String, Double> graph = builder.buildDirectedGraph(connections);

        DirectedLineController controller = new DirectedLineController();
        DirectedLineResultDTO result = controller.execute(graph);

        assertTrue(result.hasCycle());
        assertFalse(result.getCycleStations().isEmpty());

        assertTrue(result.getCycleStations().contains("1 | Brussels"));
        assertTrue(result.getCycleStations().contains("2 | Antwerp"));
        assertTrue(result.getCycleStations().contains("3 | Ghent"));
        assertFalse(result.getCycleStations().contains("5 | Namur"));   
    }

}