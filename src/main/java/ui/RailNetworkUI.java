package ui;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import domain.RailNode;
import domain.RailLine;
import graph.Graph;
import services.RailGraphBuilderService;
import utils.StationsCsvReader;
import utils.LinesCsvReader;

public class RailNetworkUI {

    private final Scanner sc;
    private Graph<RailNode, RailLine> railwayGraph;


    public RailNetworkUI(Scanner sc) {
        this.sc = sc;
        this.railwayGraph = null;
    }

    public void run() {

        boolean exit = false;

        while (!exit) {
            System.out.println("\n=========== RAILWAY NETWORK MENU ===========");
            System.out.println("1 - Directed Line Upgrade Plan (US11)");
            System.out.println("2 - Minimal Backbone Network (US12)");
            System.out.println("3 - Import Railway Network (CSV to Graph)");
            System.out.println("0 - Back");
            System.out.println("=============================================");
            System.out.print("Choose an option: ");

            String option = sc.nextLine().trim();

            switch (option) {
                case "1":
                    startUS11();
                    break;
                case "2":
                    startUS12();
                    break;
                case "3":
                    importRailwayNetwork();
                    break;
                case "0":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void startUS11() {

        if (railwayGraph == null || railwayGraph.numVertices() == 0) {
            System.out.println("\nRailway graph not loaded.");
            System.out.println("Please import the network first (option 3).");
            return;
        }

        DirectedLineUI ui = new DirectedLineUI(railwayGraph, sc);
        ui.run();
    }

    private void startUS12() {

        if (railwayGraph == null || railwayGraph.numVertices() == 0) {
            System.out.println("\nRailway graph not loaded.");
            System.out.println("Please import the network first (option 3).");
            return;
        }

        MinimalBackboneUI ui = new MinimalBackboneUI(railwayGraph, sc);
        ui.run();
    }


    private void importRailwayNetwork() {

        try {
            StationsCsvReader stationReader = new StationsCsvReader();
            LinesCsvReader lineReader = new LinesCsvReader();
            RailGraphBuilderService builder = new RailGraphBuilderService();

            Map<String, RailNode> stations =
                    stationReader.readStations("src/main/resources/data/stations.csv");

            List<RailLine> lines =
                    lineReader.readLines("src/main/resources/data/lines.csv");

            railwayGraph = builder.buildDirectedGraph(stations, lines);

            System.out.println("\nImport successful.");
            System.out.println("Stations loaded: " + railwayGraph.numVertices());
            System.out.println("Directed lines: " + railwayGraph.numEdges());

        } catch (Exception e) {
            System.out.println("Import failed: " + e.getMessage());
            railwayGraph = null;
        }

        System.out.println("\nPress ENTER to return.");
        sc.nextLine();
    }
}
