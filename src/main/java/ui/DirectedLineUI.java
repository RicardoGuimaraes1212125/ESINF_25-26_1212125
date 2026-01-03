package ui;

import controllers.DirectedLineController;
import dto.DirectedLineResultDTO;
import domain.RailNode;
import domain.RailLine;
import graph.Graph;
import utils.GraphvizExporter;

import java.util.Scanner;

public class DirectedLineUI {

    private final Graph<RailNode, RailLine> graph;
    private final Scanner sc;

    public DirectedLineUI(Graph<RailNode, RailLine> graph, Scanner sc) {
        this.graph = graph;
        this.sc = sc;
    }

    public void run() {

        DirectedLineController controller = new DirectedLineController();
        DirectedLineResultDTO result = controller.execute(graph);

        System.out.println("\n=== US11 – Directed Line Upgrade Plan ===");

        if (!result.hasCycle()) {
            System.out.println("\nUpgrade order:");
            int i = 1;
            for (RailNode station : result.getUpgradeOrder()) {
                System.out.println(i++ + " - " + formatStation(station));
            }
        } else {
            System.out.println("\n CYCLES DETECTED!");
            System.out.println("Number of cycles found: " + result.getCycleCount());
            
            System.out.println("\nStations involved in cycles:");
            for (RailNode station : result.getCycleStations()) {
                System.out.println("  - " + formatStation(station));
            }

            System.out.println("\nDependency links in cycles:");
            for (RailLine link : result.getCycleLinks()) {
                System.out.println("  -> " + link.getFromStationId() + " -> " + link.getToStationId() 
                                 + " (distance: " + link.getDistance() + " km)");
            }
        }

        System.out.println("\nTime Complexity: " + result.getComplexity());

        askForExport(result);

        System.out.println("\nPress ENTER to return...");
        sc.nextLine();
    }

    private void askForExport(DirectedLineResultDTO result) {

        System.out.print("\nDo you want to export the graph (Graphviz)? (y/n): ");
        String option = sc.nextLine().trim().toLowerCase();
        if (!option.equals("y")) return;

        System.out.print("Enter output file (default: us11_railway.dot): ");
        String path = sc.nextLine().trim();
        if (path.isEmpty()) path = "us11_railway.dot";

        try {
            GraphvizExporter.exportDirectedGraph(
                    graph,
                    result.getCycleStations(),
                    path
            );

            System.out.println("\nGraph exported successfully.");
            System.out.println("neato -Tsvg " + path + " -o us11_railway.svg");

        } catch (Exception e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    private String formatStation(RailNode node) {
        return node.getId() + " | " + node.getName();
    }
}
