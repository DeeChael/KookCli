package net.deechael.kookcli.plugin;

import java.io.File;

public abstract class Plugin {

    public Plugin() {
    }

    protected void init(PluginDescriptionFile pluginDescriptionFile) {

    }

    public void onEnable() {}

    public void onDisable() {}

    public File getDataFolder() {
        throw new RuntimeException("Not implemented");
    }

    public String getName() {
        throw new RuntimeException("Not implemented");
    }

}
