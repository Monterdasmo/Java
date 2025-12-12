package ua.kpi.iotask5.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public final class ReportExporter {
    private ReportExporter() {}

    public static void exportText(String path, String content, boolean gzip) throws IOException {
        OutputStream os = new FileOutputStream(path);
        if (gzip) os = new GZIPOutputStream(os);

        try (Writer w = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            w.write(content);
        }
    }
}
