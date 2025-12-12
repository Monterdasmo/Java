package ua.kpi.iotask5.streams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class KeyEncryptOutputStream extends FilterOutputStream {
    private final int key;

    public KeyEncryptOutputStream(OutputStream out, char keyChar) {
        super(out);
        this.key = keyChar;
    }

    @Override
    public void write(int b) throws IOException {
        int enc = (b + key) & 0xFF; // для байтового потоку
        super.write(enc);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] tmp = new byte[len];
        for (int i = 0; i < len; i++) {
            tmp[i] = (byte) ((b[off + i] + key) & 0xFF);
        }
        super.write(tmp, 0, len);
    }
}
