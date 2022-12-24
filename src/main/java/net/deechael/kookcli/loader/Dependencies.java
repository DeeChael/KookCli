package net.deechael.kookcli.loader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Dependencies {

    public static void check() throws MalformedURLException {
        File folder = new File("libraries");
        if (!folder.exists())
            folder.mkdirs();
        List<String> libraries = readLines(Objects.requireNonNull(Dependencies.class.getResourceAsStream("/libraries.txt")), true);
        List<URL> urls = new ArrayList<>();
        for (String library : libraries) {
            if (library.startsWith("//"))
                continue;
            File libraryFile = new File(folder, library);
            urls.add(libraryFile.toURI().toURL());
            if (libraryFile.exists())
                continue;
            try {
                copy("/libraries/" + library, libraryFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        load(urls.toArray(new URL[0]));
    }

    public static void load(URL[] urls) {
        ClassLoader classLoader = new DependencyClassLoader(urls);
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            Class<?> kookCliClass = Thread.currentThread().getContextClassLoader().loadClass("net.deechael.kookcli.KookCli");
            kookCliClass.getMethod("main").invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copy(String path, File destination) throws IOException {
        if (!destination.exists()) {
            try {
                destination.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        copy(Dependencies.class.getResourceAsStream(path), new FileOutputStream(destination), true);
    }

    private static void copy(InputStream inputStream, OutputStream outputStream, boolean close) {
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

    private static List<String> readLines(InputStream inputStream, boolean close) {
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

    private Dependencies() {
    }

}
