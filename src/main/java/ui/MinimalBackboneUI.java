package ui;

import controllers.MinimalBackboneController;
import domain.RailLine;
import domain.RailNode;
import graph.Graph;
import utils.GraphvizExporter;

import java.util.Scanner;

public class MinimalBackboneUI {

    private final Graph<RailNode, RailLine> railwayGraph;
    private final Scanner sc;

    public MinimalBackboneUI(Graph<RailNode, RailLine> railwayGraph, Scanner sc) {
        this.railwayGraph = railwayGraph;
        this.sc = sc;
    }

    public void run() {

        System.out.println("\n========= MINIMAL BACKBONE NETWORK (US12) =========");

        if (railwayGraph == null || railwayGraph.numVertices() == 0) {
            System.out.println("Railway graph not loaded.");
            waitReturn();
            return;
        }

        System.out.println("Stations: " + railwayGraph.numVertices());
        System.out.println("Lines: " + railwayGraph.numEdges());

        System.out.print("\nGenerate Minimal Backbone Network? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
            waitReturn();
            return;
        }

        try {
            MinimalBackboneController controller = new MinimalBackboneController();
            Graph<RailNode, RailLine> backbone =
                    controller.computeMinimalBackbone(railwayGraph);

            GraphvizExporter.exportUndirectedBackbone(backbone, "minimal_backbone.dot");

            System.out.println("\nMinimal Backbone generated.");
            System.out.println("neato minimal_backbone.dot -Tsvg -o minimal_backbone.svg");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        waitReturn();
    }

    private void waitReturn() {
        System.out.println("\nPress ENTER to return.");
        sc.nextLine();
    }
}
