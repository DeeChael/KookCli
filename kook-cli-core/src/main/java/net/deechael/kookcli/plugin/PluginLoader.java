package net.deechael.kookcli.plugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class PluginLoader extends URLClassLoader {

    private final Plugin plugin;
    private final PluginDescriptionFile pluginDescriptionFile;

    public PluginLoader(File file, ClassLoader classLoader) {
        super(new URL[0], classLoader);
        try {
            JarFile jarFile = new JarFile(file);
            InputStream inputStream = jarFile.getInputStream(jarFile.getJarEntry("kookcli.plugin.json"));
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            inputStream.close();

            this.pluginDescriptionFile = new PluginDescriptionFile(file, jsonObject);

            this.addURL(file.toURI().toURL());
            Class<? extends Plugin> mainClass = this.loadClass(this.pluginDescriptionFile.getMain()).asSubclass(Plugin.class);
            this.plugin = mainClass.newInstance();
            this.plugin.init(this.pluginDescriptionFile);
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public PluginDescriptionFile getPluginDescriptionFile() {
        return pluginDescriptionFile;
    }

}
