package net.deechael.kookcli.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PluginDescriptionFile {

    private final static String NAME_REGEX = "([a-zA-Z])([a-zA-Z0-9-_])*";

    private final File pluginFile;

    private final String name;
    private final String main;
    private final String description;
    private final String version;
    private final List<String> authors = new ArrayList<>();
    private final List<String> dependencies = new ArrayList<>();

    public PluginDescriptionFile(File pluginFile, JsonObject jsonObject) {
        this.pluginFile = pluginFile;
        this.name = jsonObject.get("name").getAsString();
        this.main = jsonObject.get("main").getAsString();
        this.description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : "";
        this.version = jsonObject.get("version").getAsString();
        if (jsonObject.has("author"))
            this.authors.add(jsonObject.get("author").getAsString());
        if (jsonObject.has("authors"))
            for (JsonElement element : jsonObject.getAsJsonArray("authors"))
                this.authors.add(element.getAsString());
        if (jsonObject.has("dependencies"))
            for (JsonElement element : jsonObject.getAsJsonArray("dependencies"))
                this.authors.add(element.getAsString());
        if (!Pattern.matches(NAME_REGEX, this.name))
            throw new RuntimeException("Cannot load plugin: " + this.name);
    }

    public String getName() {
        return name;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

}
