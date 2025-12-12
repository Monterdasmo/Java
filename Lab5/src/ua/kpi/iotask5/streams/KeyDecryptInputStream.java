package ua.kpi.iotask5.streams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class KeyDecryptInputStream extends FilterInputStream {
    private final int key;

    public KeyDecryptInputStream(InputStream in, char keyChar) {
        super(in);
        this.key = keyChar;
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b == -1) return -1;
        return (b - key) & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = super.read(b, off, len);
        if (n == -1) return -1;
        for (int i = 0; i < n; i++) {
            b[off + i] = (byte) ((b[off + i] - key) & 0xFF);
        }
        return n;
    }
}
