package net.deechael.kookcli.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FileUtil {

    public static void copy(InputStream inputStream, OutputStream outputStream, boolean close) {
        try {
            byte[] buf = new byte[2048];
            int r;
            while (-1 != (r = inputStream.read(buf))) {
                outputStream.write(buf, 0, r);
            }
            if (!close)
                return;
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readAllBytes(InputStream inputStream, boolean close) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            if (close)
                inputStream.close();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readLines(InputStream inputStream, boolean close) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            if (close)
                inputStream.close();
            List<String> lines = new ArrayList<>();
            Collections.addAll(lines, new String(bytes).split("\n"));
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileUtil() {
    }

}
