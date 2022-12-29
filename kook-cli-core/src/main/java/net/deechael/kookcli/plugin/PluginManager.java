package net.deechael.kookcli.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {

    private final Map<String, PluginLoader> loadedPlugins = new HashMap<>();

    public void load() {
        File pluginFolder = new File("plugins");
        if (!pluginFolder.exists())
            pluginFolder.mkdirs();
        File[] pluginFiles = pluginFolder.listFiles();
        if (pluginFiles == null)
            return;
        for (File pluginFile : pluginFiles) {
            if (!pluginFile.isFile())
                continue;
            if (!pluginFile.getName().endsWith(".jar"))
                continue;
            this.loadPlugin(pluginFile);
        }
        // TODO: boot with dependencies order
        for (PluginLoader pluginLoader : this.loadedPlugins.values()) {
            for (String dependency : pluginLoader.getPluginDescriptionFile().getDependencies()) {
                if (!loadedPlugins.containsKey(dependency))
                    throw new RuntimeException("Missing dependency " + dependency + " for plugin " + pluginLoader.getPluginDescriptionFile().getName());
            }
        }
        this.loadedPlugins.values().stream().map(PluginLoader::getPlugin).forEach(Plugin::onEnable);
    }

    public void unload() {
        this.loadedPlugins.values().stream().map(PluginLoader::getPlugin).forEach(Plugin::onDisable);
    }

    public Plugin loadPlugin(File pluginFile) {
        PluginLoader pluginLoader = new PluginLoader(pluginFile, Thread.currentThread().getContextClassLoader());
        this.loadedPlugins.put(pluginLoader.getPluginDescriptionFile().getName(), pluginLoader);
        return pluginLoader.getPlugin();
    }

    public void disablePlugin(Plugin plugin) {
        PluginLoader pluginLoader = this.loadedPlugins.get(plugin.getName());
        if (pluginLoader == null)
            return;
        plugin.onDisable();
        try {
            pluginLoader.close();
            this.loadedPlugins.remove(plugin.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Plugin getPlugin(String name) {
        return this.loadedPlugins.get(name).getPlugin();
    }

    public List<String> loadedPlugins() {
        return new ArrayList<>(this.loadedPlugins.keySet());
    }

}
