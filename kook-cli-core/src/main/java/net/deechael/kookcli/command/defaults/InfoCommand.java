package net.deechael.kookcli.command.defaults;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import net.deechael.kookcli.KookCli;
import net.deechael.kookcli.command.Command;
import net.deechael.kookcli.command.ConsoleSender;
import net.deechael.kookcli.network.Routes;
import net.deechael.kookcli.util.ParamsUtil;

import java.util.HashMap;

public final class InfoCommand {

    public static void register(CommandDispatcher<ConsoleSender> commandDispatcher) {
        commandDispatcher.register(
                Command.of("info")
                        .executes(context -> {
                            if (!KookCli.isLogged()) {
                                System.out.println("================\n" +
                                        "Hasn't logged in yet\n" +
                                        "================");
                                return 1;
                            }
                            JsonObject data = KookCli.getRequest(Routes.USER_ME, new HashMap<>());
                            data = data.getAsJsonObject("data");
                            StringBuilder builder = new StringBuilder("================\n");
                            builder.append("Login Type: ").append(KookCli.isBot() ? "Bot" : "User").append("\n");
                            builder.append("Login As: ").append(data.get("username").getAsString()).append("#").append(data.get("identify_num").getAsString()).append("\n");
                            if (KookCli.getCurrentGuild() != null) {
                                JsonObject guildData = KookCli.getRequest(Routes.GUILD_VIEW, ParamsUtil.params(ParamsUtil.param("guild_id", KookCli.getCurrentGuild()))).getAsJsonObject("data");
                                builder.append("Current Guild: ").append(guildData.get("name").getAsString()).append("\n");
                                if (KookCli.getCurrentChannel() != null) {
                                    JsonObject channelData = KookCli.getRequest(Routes.CHANNEL_VIEW, ParamsUtil.params(ParamsUtil.param("target_id", KookCli.getCurrentChannel()))).getAsJsonObject("data");
                                    builder.append("Current Channel: ").append(channelData.get("name").getAsString()).append("\n");
                                }
                            }
                            builder.append("================");
                            System.out.println(builder);
                            return 1;
                        })
        );
    }

    private InfoCommand() {
    }

}
