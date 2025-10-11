package utils;

import domain.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;

public class CsvReader {

    private static List<String[]> readCsv(String path) {
        List<String[]> rows = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (int i = 1; i < lines.size(); i++) { // ignora cabeçalho
                rows.add(lines.get(i).split(","));
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler " + path + ": " + e.getMessage());
        }
        return rows;
    }

    public static List<Bay> readBays(String path) {
    List<Bay> list = new ArrayList<>();
    try {
        List<String> lines = Files.readAllLines(Paths.get(path));

        if (lines.isEmpty()) {
            throw new RuntimeException("O ficheiro bays.csv está vazio!");
        }

        for (int i = 1; i < lines.size(); i++) { // Ignora o cabeçalho
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            // bays.csv usa ';' como separador
            String[] row = line.split(";");
            if (row.length < 4) {
                System.err.println("⚠️ Linha inválida ignorada em bays.csv: " + line);
                continue;
            }

            list.add(new Bay(
                    row[0].trim(),
                    Integer.parseInt(row[1].trim()),
                    Integer.parseInt(row[2].trim()),
                    Integer.parseInt(row[3].trim())
            ));
        }

    } catch (IOException e) {
        throw new RuntimeException("Erro ao ler bays.csv: " + e.getMessage());
    }
    return list;
}


    public static List<Item> readItems(String path) {
        List<Item> list = new ArrayList<>();
        for (String[] row : readCsv(path)) {
            list.add(new Item(row[0], row[1], row[2], row[3],
                    Double.parseDouble(row[4]), Double.parseDouble(row[5])));
        }
        return list;
    }

    public static List<Wagon> readWagons(String path) {
        List<Wagon> list = new ArrayList<>();
        for (String[] row : readCsv(path)) {
            list.add(new Wagon(row[0], row[1], row[2],
                    Integer.parseInt(row[3]),
                    row[4].isEmpty() ? null : row[4],
                    LocalDateTime.parse(row[5])));
        }
        return list;
    }

    public static List<Order> readOrders(String ordersPath, String orderLinesPath) {
        // Placeholder simplificado — podes depois integrar as linhas no objeto Order
        List<Order> list = new ArrayList<>();
        for (String[] row : readCsv(ordersPath)) {
            list.add(new Order(row[0], row[1], Integer.parseInt(row[2])));
        }
        return list;
    }

    public static List<Return> readReturns(String path) {
        List<Return> list = new ArrayList<>();
        for (String[] row : readCsv(path)) {
            list.add(new Return(row[0], row[1],
                    Integer.parseInt(row[2]), row[3],
                    LocalDateTime.parse(row[4]),
                    row.length > 5 && !row[5].isEmpty() ? row[5] : null));
        }
        return list;
    }
}

