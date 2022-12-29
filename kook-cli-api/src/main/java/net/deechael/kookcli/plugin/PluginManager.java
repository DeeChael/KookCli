package net.deechael.kookcli.plugin;

import java.io.File;
import java.util.List;

public class PluginManager {

    public void load() {
        throw new RuntimeException("Not implemented");
    }

    public void unload() {
        throw new RuntimeException("Not implemented");
    }

    public Plugin loadPlugin(File file) {
        throw new RuntimeException("Not implemented");
    }

    public void disablePlugin(Plugin plugin) {
        throw new RuntimeException("Not implemented");
    }

    public Plugin getPlugin(String name) {
        throw new RuntimeException("Not implemented");
    }

    public List<String> loadedPlugins() {
        throw new RuntimeException("Not implemented");
    }

}
