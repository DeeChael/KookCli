package net.deechael.kookcli;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import net.deechael.kookcli.command.Console;
import net.deechael.kookcli.command.ConsoleSender;
import net.deechael.kookcli.command.defaults.*;
import net.deechael.kookcli.network.Routes;
import net.deechael.kookcli.plugin.PluginManager;
import net.deechael.kookcli.util.ZlibUtil;
import okhttp3.*;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class KookCli {

    private final static CommandDispatcher<ConsoleSender> COMMAND_DISPATCHER = new CommandDispatcher<>();

    private final static ConsoleSender SENDER = new ConsoleSender();

    private final static Gson GSON = new Gson();

    private final static PluginManager PLUGIN_MANAGER = new PluginManager();

    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .pingInterval(28, TimeUnit.SECONDS)
            .build();

    private final static Receiver RECEIVER = new Receiver();

    // private final static Logger LOGGER = LoggerUtil.getLogger(KookCli.class, Level.INFO);
    private final static Logger LOGGER = LoggerFactory.getLogger("KookCli");
    private static Terminal terminal;
    private static LineReader lineReader;

    private static boolean logged_in;

    private static String auth;

    private static WebSocket webSocket;

    private static String currentGuild;
    private static String currentChannel;

    public static ConsoleSender getConsoleSender() {
        return SENDER;
    }

    public static CommandDispatcher<ConsoleSender> getCommandDispatcher() {
        return COMMAND_DISPATCHER;
    }

    private static void registerCommands() {
        CommandDispatcher<ConsoleSender> commandDispatcher = KookCli.getCommandDispatcher();
        PluginsCommand.register(commandDispatcher);
        LoginCommand.register(commandDispatcher);
        LogoutCommand.register(commandDispatcher);
        InfoCommand.register(commandDispatcher);
        GuildCommand.register(commandDispatcher);
        ChannelCommand.register(commandDispatcher);
        SendCommand.register(commandDispatcher);
        ExitCommand.register(commandDispatcher);
    }

    public static void main() {
        SysOutOverSLF4J.registerLoggingSystem("org.apache.logging");
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        registerCommands();

        Console console = new Console();

        PLUGIN_MANAGER.load();

        console.start();
    }

    public static PluginManager getPluginManager() {
        return PLUGIN_MANAGER;
    }

    public static boolean isLogged() {
        return logged_in;
    }

    public static void login(String token) {
        if (isLogged())
            throw new RuntimeException("Has logged in!");
        auth = "Bot " + token;
        logged_in = true;
        startWebsocket();
    }

    public static void login(String phone, String password) {
        JsonObject params = new JsonObject();
        params.addProperty("mobile", phone);
        params.addProperty("mobile_prefix", "86");
        params.addProperty("password", password);
        params.addProperty("remember", false);
        Request req = new Request.Builder()
                .post(RequestBody
                        .create(GSON.toJson(params),
                                MediaType.get("application/json")))
                .header("Content-type", "application/json")
                .url(Routes.AUTH_LOGIN).build();
        Call call = HTTP_CLIENT.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Thread.currentThread().setName("websocket");
                LOGGER.error("Failed to login", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Thread.currentThread().setName("websocket");
                LOGGER.debug("Logged successfully");
                JsonObject body = JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject();
                if (!body.has("token")) {
                    LOGGER.error("Failed to fetch token");
                    return;
                }
                auth = body.get("token").getAsString();
                JsonObject userInfo = body.getAsJsonObject("user");
                LOGGER.info("Login as " + userInfo.get("username").getAsString() + "#" + userInfo.get("identify_num").getAsString());
                if (auth == null)
                    return;
                logged_in = true;
                startWebsocket();
            }
        });
    }

    public static void logout() {
        if (webSocket != null) {
            webSocket.close(1000, "normal");
        }
        webSocket = null;
        auth = null;
        logged_in = false;
        currentGuild = null;
        currentChannel = null;
    }

    public static boolean isBot() {
        return auth != null && auth.startsWith("Bot");
    }

    public static JsonObject getRequest(String url, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder(url);
        if (params.size() > 0) {
            stringBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            url = new StringBuilder(stringBuilder.reverse().substring(1)).reverse().toString();
        }

        Request req = new Request.Builder()
                .get()
                .header("Content-type", "application/json")
                .header("Authorization", auth)
                .header("Cookie", cookieHeader(Collections.singletonList(new Cookie.Builder().name("auth").domain("kookapp.cn").value(auth).build())))
                .url(url).build();
        return call(url, req);
    }

    public static List<JsonObject> getPageableRequest(String url, Map<String, String> params) {
        params.put("page", "1");
        params.put("page_size", "50");
        JsonObject firstData = getRequest(url, params);
        int total = firstData.getAsJsonObject("data").getAsJsonObject("meta").get("total").getAsInt();
        List<JsonObject> objects = new ArrayList<>();
        for (JsonElement element : firstData.getAsJsonObject("data").getAsJsonArray("items")) {
            objects.add(element.getAsJsonObject());
        }
        if (total > 50) {
            int pages = total % 50 == 0 ? total / 50 : total / 50 + 1;
            for (int page = 2; page <= pages; page++) {
                params.put("page", "" + page);
                JsonObject data = getRequest(url, params);
                for (JsonElement element : data.getAsJsonObject("data").getAsJsonArray("items")) {
                    objects.add(element.getAsJsonObject());
                }
            }
        }
        return objects;
    }

    public static JsonObject postRequest(String url, JsonObject params) {
        Request req = new Request.Builder()
                .post(RequestBody
                        .create(GSON.toJson(params),
                                MediaType.get("application/json")))
                .header("Authorization", auth)
                .header("Cookie", cookieHeader(Collections.singletonList(new Cookie.Builder().name("auth").domain("kookapp.cn").value(auth).build())))
                .header("Content-type", "application/json")
                .url(url).build();
        return call(url, req);
    }

    private static JsonObject call(String url, Request req) {
        Call call = HTTP_CLIENT.newCall(req);
        try {
            Response response = call.execute();
            JsonObject body = JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject();
            if (body.get("code").getAsInt() != 0)
                LOGGER.error("Failed to request, response: " + body);
            return body;
        } catch (IOException e) {
            LOGGER.error("Failed to request: " + url, e);
            throw new RuntimeException(e);
        }
    }

    public static void setCurrentChannel(String currentChannel) {
        KookCli.currentChannel = currentChannel;
    }

    public static void setCurrentGuild(String currentGuild) {
        KookCli.currentGuild = currentGuild;
        currentChannel = null;
    }

    public static String getCurrentChannel() {
        return currentChannel;
    }

    public static String getCurrentGuild() {
        return currentGuild;
    }

    public static void exit() {
        logout();
        PLUGIN_MANAGER.unload();
        System.exit(0);
    }

    public static LineReader getLineReader() {
        return lineReader;
    }

    public static Terminal getTerminal() {
        return terminal;
    }

    public static void setLineReader(LineReader lineReader) {
        KookCli.lineReader = lineReader;
    }

    public static void setTerminal(Terminal terminal) {
        KookCli.terminal = terminal;
    }

    private static void startWebsocket() {
        Request req = new Request.Builder()
                .post(RequestBody.create(new byte[0], MediaType.get("application/json")))
                .header("Authorization", auth)
                .header("Cookie", cookieHeader(Collections.singletonList(new Cookie.Builder().name("auth").domain("kookapp.cn").value(auth).build())))
                .header("Content-type", "application/json")
                .url(Routes.GATEWAY).build();
        Call call = HTTP_CLIENT.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Thread.currentThread().setName("websocket");
                LOGGER.error("Failed to fetch the websocket url", e);
                logged_in = false;
                auth = null;
                webSocket = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Thread.currentThread().setName("websocket");
                LOGGER.debug("Fetched websocket url successfully");
                JsonObject body = JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject();
                String url = body.getAsJsonObject("data").get("url").getAsString();
                LOGGER.debug("Websocket url: " + url);
                webSocket = HTTP_CLIENT.newWebSocket(new Request.Builder().get().url(url).build(), RECEIVER);
                LOGGER.info("Login successfully!");
            }
        });
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    private static String cookieHeader(List<Cookie> cookies) {
        StringBuilder cookieHeader = new StringBuilder();
        int i = 0;

        for (int size = cookies.size(); i < size; ++i) {
            if (i > 0) {
                cookieHeader.append("; ");
            }

            Cookie cookie = cookies.get(i);
            cookieHeader.append(cookie.name()).append('=').append(cookie.value());
        }

        return cookieHeader.toString();
    }

    private KookCli() {
    }

    private static class Receiver extends WebSocketListener {

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            Thread.currentThread().setName("websocket");
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            JsonObject object = JsonParser.parseString(text).getAsJsonObject();
            LOGGER.debug("Received: " + object);
            if (!object.has("s"))
                return;
            if (object.get("s").getAsInt() != 0) {
            }

        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            this.onMessage(webSocket, new String(ZlibUtil.decompress(bytes.toByteArray())));
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            LOGGER.debug("WebSocket connection has been closed");
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable throwable, Response response) {
            LOGGER.error("Failed to connect to websocket", throwable);
        }

    }

}
