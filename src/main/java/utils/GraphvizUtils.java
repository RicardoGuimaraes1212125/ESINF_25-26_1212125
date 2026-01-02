package utils;

import java.io.IOException;

public class GraphvizUtils {

    /**
     * Gera um arquivo SVG a partir de um arquivo DOT usando o Graphviz.
     *
     * @param dotFilePath caminho do arquivo .dot
     * @param svgFilePath caminho do arquivo .svg que será gerado
     * @throws IOException se ocorrer erro de IO
     * @throws InterruptedException se o processo do Graphviz for interrompido
     */
    public static void generateSvgFromDot(String dotFilePath, String svgFilePath)
            throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                "dot",       
                "-Tsvg",      
                dotFilePath,
                "-o",
                svgFilePath
        );

        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Erro ao gerar SVG, código de saída: " + exitCode);
        }
    }
}

