package ua.kpi.iotask5.io;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class TextFiles {
    private TextFiles() {}

    public static String findLineWithMaxWords(String path) throws IOException {
        String bestLine = null;
        int bestCount = -1;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                int c = countWords(line);
                if (c > bestCount) {
                    bestCount = c;
                    bestLine = line;
                }
            }
        }
        return bestLine;
    }

    private static int countWords(String line) {
        if (line == null) return 0;
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return 0;
        return trimmed.split("\\s+").length;
    }
}
