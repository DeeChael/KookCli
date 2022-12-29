package net.deechael.kookcli.plugin;

import java.io.File;

public abstract class Plugin {

    private File dataFolder;
    private PluginDescriptionFile pluginDescriptionFile;

    public Plugin() {
    }

    protected void init(PluginDescriptionFile pluginDescriptionFile) {
        this.dataFolder = new File("plugins/" + pluginDescriptionFile.getName());
        if (!this.dataFolder.exists())
            this.dataFolder.mkdirs();
        this.pluginDescriptionFile = pluginDescriptionFile;
    }

    public void onEnable() {}

    public void onDisable() {}

    public File getDataFolder() {
        return this.dataFolder;
    }

    public String getName() {
        return this.pluginDescriptionFile.getName();
    }

}
