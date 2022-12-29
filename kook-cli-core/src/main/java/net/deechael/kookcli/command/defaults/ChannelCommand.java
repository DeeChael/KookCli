package net.deechael.kookcli.command.defaults;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import net.deechael.kookcli.KookCli;
import net.deechael.kookcli.command.Command;
import net.deechael.kookcli.command.ConsoleSender;
import net.deechael.kookcli.network.Routes;
import net.deechael.kookcli.util.ParamsUtil;
import net.deechael.kookcli.util.StringUtil;

import java.util.List;

public final class ChannelCommand {

    public static void register(CommandDispatcher<ConsoleSender> commandDispatcher) {
        commandDispatcher.register(Command.of("channel")
                .then(Command.of("help")
                        .executes(context -> {
                            System.out.println("channel - Channel operations\n" +
                                    "       channel list - List all channels in this guild\n" +
                                    "       channel goto - Goto a channel\n");
                            return 1;
                        }))
                .then(Command.of("list")
                        .executes(context -> {
                            if (!KookCli.isLogged()) {
                                KookCli.getLogger().error("Please login first");
                                return 1;
                            }
                            if (KookCli.getCurrentGuild() == null) {
                                KookCli.getLogger().error("You are not in a guild");
                                return 1;
                            }
                            List<JsonObject> channels = KookCli.getPageableRequest(Routes.CHANNEL_LIST, ParamsUtil.params(ParamsUtil.param("guild_id", KookCli.getCurrentGuild())));
                            StringBuilder message = new StringBuilder();
                            for (int i = channels.size() - 1; i >= 0; i--) {
                                message.append(channels.get(i).get("name").getAsString()).append("\n");
                            }
                            System.out.println(message.append("Total: ").append(channels.size()));
                            return 1;
                        })
                ).then(Command.of("goto")
                        .executes(context -> {
                            if (!KookCli.isLogged()) {
                                KookCli.getLogger().error("Please login first");
                                return 1;
                            }
                            if (KookCli.getCurrentGuild() == null) {
                                KookCli.getLogger().error("You are not in a guild");
                                return 1;
                            }
                            List<JsonObject> channels = KookCli.getPageableRequest(Routes.CHANNEL_LIST, ParamsUtil.params(ParamsUtil.param("guild_id", KookCli.getCurrentGuild())));
                            StringBuilder message = new StringBuilder();
                            for (int i = channels.size() - 1; i >= 0; i--) {
                                message.append(i).append(" - ").append(channels.get(i).get("name").getAsString()).append("\n");
                            }
                            System.out.println(message);
                            String channelIndex = KookCli.getLineReader().readLine("==> ");
                            if (!StringUtil.isInteger(channelIndex)) {
                                return 1;
                            }
                            int index = Integer.parseInt(channelIndex);
                            if (index < 0 || index >= channels.size()) {
                                return 1;
                            }
                            KookCli.setCurrentChannel(channels.get(index).get("id").getAsString());
                            System.out.println("Successfully get into " + channels.get(index).get("name").getAsString());
                            return 1;
                        })
                )
        );
    }

    private ChannelCommand() {
    }

}
