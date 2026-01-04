package utils;

import domain.RailLine;
import domain.RailNode;
import graph.Graph;
import services.RailGraphBuilderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RailGraphBuilderServiceTest {

        private RailGraphBuilderService service;

        @BeforeEach
        void setUp() {
                service = new RailGraphBuilderService();
        }

        @Test
        void shouldAddAllRailNodesAsVertices() {
                // Arrange
                RailNode nodeA = new RailNode("A", "Station A", 0, 0, 0, 0);
                RailNode nodeB = new RailNode("B", "Station B", 1, 1, 10, 10);

                Map<String, RailNode> nodes = new HashMap<>();
                nodes.put("A", nodeA);
                nodes.put("B", nodeB);

                List<RailLine> lines = Collections.emptyList();

                // Act
                Graph<RailNode, RailLine> graph = service.buildDirectedGraph(nodes, lines);

                // Assert
                assertEquals(2, graph.numVertices());
                assertTrue(graph.vertices().contains(nodeA));
                assertTrue(graph.vertices().contains(nodeB));
                assertEquals(0, graph.numEdges());
        }

        @Test
        void shouldCreateDirectedEdgeWithCorrectDistance() {
                RailNode nodeA = new RailNode("A", "Station A", 0, 0, 0, 0);
                RailNode nodeB = new RailNode("B", "Station B", 1, 1, 10, 10);

                Map<String, RailNode> nodes = Map.of("A", nodeA, "B", nodeB);
                RailLine line = new RailLine("A", "B", 125.5, 100, 20.0);

                Graph<RailNode, RailLine> graph =
                        service.buildDirectedGraph(nodes, List.of(line));

                assertEquals(1, graph.numEdges());
                assertNotNull(graph.edge(nodeA, nodeB));
                assertEquals(125.5, graph.edge(nodeA, nodeB).getWeight().getDistance());
        }

        @Test
        void shouldNotCreateEdgeIfFromStationDoesNotExist() {
                // Arrange
                RailNode nodeB = new RailNode("B", "Station B", 1, 1, 10, 10);

                Map<String, RailNode> nodes = Map.of("B", nodeB);
                RailLine line = new RailLine("A", "B", 50.0, 80, 15.0);

                // Act
                Graph<RailNode, RailLine> graph = service.buildDirectedGraph(nodes, List.of(line));

                // Assert
                assertEquals(1, graph.numVertices());
                assertEquals(0, graph.numEdges());
        }

        @Test
        void shouldNotCreateEdgeIfToStationDoesNotExist() {
                // Arrange
                RailNode nodeA = new RailNode("A", "Station A", 0, 0, 0, 0);

                Map<String, RailNode> nodes = Map.of("A", nodeA);
                RailLine line = new RailLine("A", "B", 70.0, 60, 10.0);

                // Act
                Graph<RailNode, RailLine> graph = service.buildDirectedGraph(nodes, List.of(line));

                // Assert
                assertEquals(1, graph.numVertices());
                assertEquals(0, graph.numEdges());
        }

        @Test
        void shouldRespectGraphDirection() {
                RailNode nodeA = new RailNode("A", "Station A", 0, 0, 0, 0);
                RailNode nodeB = new RailNode("B", "Station B", 1, 1, 10, 10);

                Map<String, RailNode> nodes = Map.of("A", nodeA, "B", nodeB);

                RailLine lineAB = new RailLine("A", "B", 10.0, 100, 5.0);
                RailLine lineBA = new RailLine("B", "A", 20.0, 100, 7.0);

                Graph<RailNode, RailLine> graph =
                        service.buildDirectedGraph(nodes, List.of(lineAB, lineBA));

                assertEquals(2, graph.numEdges());
                assertEquals(10.0, graph.edge(nodeA, nodeB).getWeight().getDistance());
                assertEquals(20.0, graph.edge(nodeB, nodeA).getWeight().getDistance());
        }

        @Test
        void shouldBuildGraphWithEightNodesAndMultiplePaths() {
                RailNode a = new RailNode("A", "Station A", 0, 0, 0, 0);
                RailNode b = new RailNode("B", "Station B", 1, 1, 10, 10);
                RailNode c = new RailNode("C", "Station C", 2, 2, 20, 20);
                RailNode d = new RailNode("D", "Station D", 3, 3, 30, 30);
                RailNode e = new RailNode("E", "Station E", 4, 4, 40, 40);
                RailNode f = new RailNode("F", "Station F", 5, 5, 50, 50);
                RailNode g = new RailNode("G", "Station G", 6, 6, 60, 60);
                RailNode h = new RailNode("H", "Station H", 7, 7, 70, 70);

                Map<String, RailNode> nodes = Map.of(
                        "A", a, "B", b, "C", c, "D", d,
                        "E", e, "F", f, "G", g, "H", h
                );

                List<RailLine> lines = List.of(
                        new RailLine("A", "B", 10, 100, 5),
                        new RailLine("A", "C", 12, 100, 5),
                        new RailLine("B", "D", 15, 100, 6),
                        new RailLine("C", "D", 14, 100, 6),
                        new RailLine("D", "E", 20, 100, 7),
                        new RailLine("E", "F", 18, 100, 7),
                        new RailLine("F", "G", 22, 100, 8),
                        new RailLine("G", "H", 25, 100, 9),
                        new RailLine("H", "A", 30, 100, 10)
                );

                Graph<RailNode, RailLine> graph =
                        service.buildDirectedGraph(nodes, lines);

                assertEquals(8, graph.numVertices());
                assertEquals(9, graph.numEdges());
                assertEquals(10, graph.edge(a, b).getWeight().getDistance());
                assertEquals(14, graph.edge(c, d).getWeight().getDistance());
                assertEquals(30, graph.edge(h, a).getWeight().getDistance());
        }

        @Test
        void shouldBuildGraphWithTenNodesAndHubStructure() {
                RailNode a = new RailNode("A", "Station A", 0, 0, 0, 0);
                RailNode b = new RailNode("B", "Station B", 1, 1, 10, 10);
                RailNode c = new RailNode("C", "Station C", 2, 2, 20, 20);
                RailNode d = new RailNode("D", "Station D", 3, 3, 30, 30);
                RailNode e = new RailNode("E", "Station E", 4, 4, 40, 40);
                RailNode f = new RailNode("F", "Station F", 5, 5, 50, 50);
                RailNode g = new RailNode("G", "Station G", 6, 6, 60, 60);
                RailNode h = new RailNode("H", "Station H", 7, 7, 70, 70);
                RailNode i = new RailNode("I", "Station I", 8, 8, 80, 80);
                RailNode j = new RailNode("J", "Station J", 9, 9, 90, 90);

                Map<String, RailNode> nodes = Map.of(
                        "A", a, "B", b, "C", c, "D", d, "E", e,
                        "F", f, "G", g, "H", h, "I", i, "J", j
                );

                List<RailLine> lines = List.of(
                        new RailLine("A", "B", 5, 100, 2),
                        new RailLine("A", "C", 6, 100, 2),
                        new RailLine("A", "D", 7, 100, 3),
                        new RailLine("A", "E", 8, 100, 3),
                        new RailLine("A", "F", 9, 100, 4),
                        new RailLine("B", "G", 10, 100, 4),
                        new RailLine("C", "H", 11, 100, 4),
                        new RailLine("D", "I", 12, 100, 5),
                        new RailLine("E", "J", 13, 100, 5)
                );

                Graph<RailNode, RailLine> graph =
                        service.buildDirectedGraph(nodes, lines);

                assertEquals(10, graph.numVertices());
                assertEquals(9, graph.numEdges());
                assertEquals(5, graph.edge(a, b).getWeight().getDistance());
                assertEquals(13, graph.edge(e, j).getWeight().getDistance());
        }

        @Test
        void shouldBuildComplexGraphWithTwelveNodesAndCycles() {
                // Arrange
                RailNode a = new RailNode("A", "Station A", 0, 0, 0, 0);
                RailNode b = new RailNode("B", "Station B", 1, 1, 10, 10);
                RailNode c = new RailNode("C", "Station C", 2, 2, 20, 20);
                RailNode d = new RailNode("D", "Station D", 3, 3, 30, 30);
                RailNode e = new RailNode("E", "Station E", 4, 4, 40, 40);
                RailNode f = new RailNode("F", "Station F", 5, 5, 50, 50);
                RailNode g = new RailNode("G", "Station G", 6, 6, 60, 60);
                RailNode h = new RailNode("H", "Station H", 7, 7, 70, 70);
                RailNode i = new RailNode("I", "Station I", 8, 8, 80, 80);
                RailNode j = new RailNode("J", "Station J", 9, 9, 90, 90);
                RailNode k = new RailNode("K", "Station K", 10, 10, 100, 100);
                RailNode l = new RailNode("L", "Station L", 11, 11, 110, 110);

                Map<String, RailNode> nodes = new HashMap<>();
                nodes.put("A", a);
                nodes.put("B", b);
                nodes.put("C", c);
                nodes.put("D", d);
                nodes.put("E", e);
                nodes.put("F", f);
                nodes.put("G", g);
                nodes.put("H", h);
                nodes.put("I", i);
                nodes.put("J", j);
                nodes.put("K", k);
                nodes.put("L", l);

                List<RailLine> lines = List.of(
                                new RailLine("A", "B", 5, 100, 1),
                                new RailLine("B", "C", 6, 100, 1),
                                new RailLine("C", "D", 7, 100, 1),
                                new RailLine("D", "A", 8, 100, 1),   // ciclo 1
                                new RailLine("C", "E", 9, 100, 2),
                                new RailLine("E", "F", 10, 100, 2),
                                new RailLine("F", "C", 11, 100, 2),  // ciclo 2
                                new RailLine("D", "G", 12, 100, 3),
                                new RailLine("G", "H", 13, 100, 3),
                                new RailLine("H", "I", 14, 100, 3),
                                new RailLine("I", "J", 15, 100, 3),
                                new RailLine("J", "K", 16, 100, 4),
                                new RailLine("K", "L", 17, 100, 4)
                );

                // Act
                Graph<RailNode, RailLine> graph = service.buildDirectedGraph(nodes, lines);

                // Assert
                assertEquals(12, graph.numVertices());
                assertEquals(13, graph.numEdges());

                assertEquals(8, graph.edge(d, a).getWeight().getDistance());
                assertEquals(11, graph.edge(f, c).getWeight().getDistance());
                assertEquals(17, graph.edge(k, l).getWeight().getDistance());
        }
}



