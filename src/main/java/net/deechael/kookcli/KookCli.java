package net.deechael.kookcli;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.deechael.kookcli.network.Routes;
import net.deechael.kookcli.util.LoggerUtil;
import net.deechael.kookcli.util.ZlibUtil;
import okhttp3.*;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class KookCli {

    private final static Gson GSON = new Gson();

    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .pingInterval(28, TimeUnit.SECONDS)
            .build();

    private final static Receiver RECEIVER = new Receiver();

    private final static Logger LOGGER = LoggerUtil.getLogger(KookCli.class, Level.INFO);
    private static Terminal terminal;
    private static LineReader lineReader;

    private static boolean logged_in;

    private static String auth;

    private static WebSocket webSocket;

    private static String currentGuild;
    private static String currentChannel;

    public static boolean isLogged() {
        return logged_in;
    }

    public static void login(String token) {
        if (isLogged())
            throw new RuntimeException("Has logged in!");
        auth = "Bot " + token;
        startWebsocket();
        logged_in = true;
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
                LOGGER.error("Failed to login", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LOGGER.debug("Fetched websocket url successfully");
                JsonObject body = JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject();
                if (!body.has("token")) {
                    LOGGER.error("Failed to fetch token");
                    return;
                }
                auth = body.get("token").getAsString();
                JsonObject userInfo = body.getAsJsonObject("user");
                LOGGER.info("Login as " + userInfo.get("username").getAsString() + "#" + userInfo.get("identify_num").getAsString());
            }
        });
        if (auth == null)
            return;
        startWebsocket();
        logged_in = true;
    }

    public static void setCurrentChannel(String currentChannel) {
        KookCli.currentChannel = currentChannel;
    }

    public static void setCurrentGuild(String currentGuild) {
        KookCli.currentGuild = currentGuild;
    }

    public static String getCurrentChannel() {
        return currentChannel;
    }

    public static String getCurrentGuild() {
        return currentGuild;
    }

    public static void exit() {
        if (webSocket != null) {
            webSocket.close(1000, "normal");
        }
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
                .header("Cookie", cookieHeader(Collections.singletonList(new Cookie.Builder().name("auth").value(auth).build())))
                .header("Content-type", "application/json")
                .url(Routes.GATEWAY).build();
        Call call = HTTP_CLIENT.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOGGER.error("Failed to fetch the websocket url", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
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

        for(int size = cookies.size(); i < size; ++i) {
            if (i > 0) {
                cookieHeader.append("; ");
            }

            Cookie cookie = cookies.get(i);
            cookieHeader.append(cookie.name()).append('=').append(cookie.value());
        }

        return cookieHeader.toString();
    }

    private KookCli() {}

    private static class Receiver extends WebSocketListener {

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            JsonObject object = JsonParser.parseString(text).getAsJsonObject();
            System.out.println(object);
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
