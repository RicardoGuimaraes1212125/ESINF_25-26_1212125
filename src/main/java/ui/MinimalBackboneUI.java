package ui;

import controllers.MinimalBackboneController;
import graph.Graph;
import utils.GraphvizExporter;

import java.util.Scanner;

public class MinimalBackboneUI {

    private final Graph<String, Double> railwayGraph;
    private final Scanner sc;

    public MinimalBackboneUI(Graph<String, Double> railwayGraph, Scanner sc) {
        this.railwayGraph = railwayGraph;
        this.sc = sc;
    }

    public void run() {

        System.out.println("\n========= MINIMAL BACKBONE NETWORK (US12) =========");

        if (railwayGraph == null || railwayGraph.numVertices() == 0) {
            System.out.println("Railway graph not loaded.");
            System.out.println("Please import the network first.");
            waitReturn();
            return;
        }

        System.out.println("Stations: " + railwayGraph.numVertices());
        System.out.println("Lines: " + railwayGraph.numEdges());
        System.out.print("\nGenerate Minimal Backbone Network? (y/n): ");

        String option = sc.nextLine().trim().toLowerCase();

        if (!option.equals("y")) {
            System.out.println("Operation cancelled.");
            waitReturn();
            return;
        }

        try {
            MinimalBackboneController controller =
                    new MinimalBackboneController();

            Graph<String, Double> backbone =
                    controller.computeMinimalBackbone(railwayGraph);

            GraphvizExporter.exportUndirectedBackbone(
                    backbone,
                    "minimal_backbone.dot"
            );

            System.out.println("\nMinimal Backbone Network successfully generated.");
            System.out.println("DOT file: minimal_backbone.dot");
            System.out.println("To generate SVG run:");
            System.out.println("neato minimal_backbone.dot -Tsvg -o minimal_backbone.svg");

        } catch (Exception e) {
            System.out.println("Error executing US12: " + e.getMessage());
        }

        waitReturn();
    }

    private void waitReturn() {
        System.out.println("\nPress ENTER to return.");
        sc.nextLine();
    }
}

