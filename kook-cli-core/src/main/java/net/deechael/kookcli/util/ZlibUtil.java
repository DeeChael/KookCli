package net.deechael.kookcli.util;

import net.deechael.kookcli.KookCli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Inflater;

public final class ZlibUtil {

    public static byte[] decompress(byte[] data) {
        byte[] output;
        Inflater inflater = new Inflater();
        inflater.reset();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        try {
            byte[] result = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(result);
                outputStream.write(result, 0, count);
            }
            output = outputStream.toByteArray();
        } catch (Exception e) {
            output = data;
            KookCli.getLogger().error("Error when decompressing", e);
        } finally {
            try {
                outputStream.close();
                inflater.end();
            } catch (IOException e) {
                KookCli.getLogger().error("Error when decompressing", e);
            }
        }

        return output;
    }

    private ZlibUtil() {
    }

}
