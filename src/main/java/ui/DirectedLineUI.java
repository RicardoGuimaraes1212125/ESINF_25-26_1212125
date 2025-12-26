package ui;

import controllers.DirectedLineController;
import dto.DirectedLineResultDTO;
import graph.Graph;
import utils.GraphvizExporter;

import java.util.Scanner;

public class DirectedLineUI {

    private final Graph<String, Double> graph;
    private final Scanner sc;

    public DirectedLineUI(Graph<String, Double> graph, Scanner sc) {
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
            for (String station : result.getUpgradeOrder()) {
                System.out.println(i++ + " - " + station.split("\\|")[1].trim());
            }
        } else {
            System.out.println("\nCycles detected involving stations:");
            for (String station : result.getCycleStations()) {
                System.out.println("• " + station.split("\\|")[1].trim());
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
        if (path.isEmpty())
            path = "us11_railway.dot";

        try {
            GraphvizExporter.exportDirectedGraph(graph,result.getCycleStations(),path
            );

            System.out.println("\nGraph exported successfully.");
            System.out.println("Generate SVG with:");
            System.out.println("  neato -Tsvg " + path + " -o us11_railway.svg");

        } catch (Exception e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
}
