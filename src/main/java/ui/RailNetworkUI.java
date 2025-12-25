package ui;

import java.util.List;
import java.util.Scanner;

import domain.StationConnection;
import graph.Graph;
import services.RailGraphBuilderService;
import utils.RailNetworkCsvReader;

public class RailNetworkUI {

    private final Scanner sc;
    private Graph<String, Double> railwayGraph;

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
                    // US12 futura
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

    private void importRailwayNetwork() {

        System.out.println("\n=== Import Railway Network ===");

        System.out.print("Enter CSV path (leave empty for default): ");
        String path = sc.nextLine().trim();

        if (path.isEmpty()) {
            path = "C:\\Users\\35193\\Desktop\\ESINF_25-26_1212125\\src\\main\\resources\\data\\station_to_station.csv";
        }

        try {
            RailNetworkCsvReader reader = new RailNetworkCsvReader();
            List<StationConnection> connections = reader.readCsv(path);

            RailGraphBuilderService builder = new RailGraphBuilderService();
            railwayGraph = builder.buildDirectedGraph(connections);

            System.out.println("\nImport successful.");
            System.out.println("Stations loaded: " + railwayGraph.numVertices());
            System.out.println("Directed connections: " + railwayGraph.numEdges());

        } catch (Exception e) {
            System.out.println("Error importing railway network: " + e.getMessage());
            railwayGraph = null;
        }

        System.out.println("\nPress ENTER to return.");
        sc.nextLine();
    }
}
