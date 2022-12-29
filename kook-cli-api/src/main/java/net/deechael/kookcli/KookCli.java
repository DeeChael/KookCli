package net.deechael.kookcli;

import com.google.gson.JsonObject;
import net.deechael.kookcli.plugin.PluginManager;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public final class KookCli {

    private final static RuntimeException NOT_IMPLEMENTED = new RuntimeException("Not implemented");

    public static void login(String token) {
        throw NOT_IMPLEMENTED;
    }

    public static void login(String phone, String password) {
        throw NOT_IMPLEMENTED;
    }

    public static void logout() {
        throw NOT_IMPLEMENTED;
    }

    public static boolean isBot() {
        throw NOT_IMPLEMENTED;
    }

    public static JsonObject getRequest(String url, Map<String, String> params) {
        throw NOT_IMPLEMENTED;
    }

    public static List<JsonObject> getPageableRequest(String url, Map<String, String> params) {
        throw NOT_IMPLEMENTED;
    }

    public static JsonObject postRequest(String url, JsonObject params) {
        throw NOT_IMPLEMENTED;
    }

    public static void setCurrentChannel(String currentChannel) {
        throw NOT_IMPLEMENTED;
    }

    public static void setCurrentGuild(String currentGuild) {
        throw NOT_IMPLEMENTED;
    }

    public static String getCurrentChannel() {
        throw NOT_IMPLEMENTED;
    }

    public static String getCurrentGuild() {
        throw NOT_IMPLEMENTED;
    }

    public static void exit() {
        throw NOT_IMPLEMENTED;
    }

    public static LineReader getLineReader() {
        throw NOT_IMPLEMENTED;
    }

    public static Terminal getTerminal() {
        throw NOT_IMPLEMENTED;
    }

    public static Logger getLogger() {
        throw NOT_IMPLEMENTED;
    }

    public static PluginManager getPluginManager() {
        throw NOT_IMPLEMENTED;
    }

    private KookCli() {}

}
