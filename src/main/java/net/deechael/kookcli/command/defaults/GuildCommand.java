package net.deechael.kookcli.command.defaults;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import net.deechael.kookcli.ConsoleSender;
import net.deechael.kookcli.KookCli;
import net.deechael.kookcli.command.Command;
import net.deechael.kookcli.network.Routes;

import java.util.HashMap;
import java.util.List;

public final class GuildCommand {

    public static void register(CommandDispatcher<ConsoleSender> commandDispatcher) {
        commandDispatcher.register(Command.of("guild")
                .then(Command.of("help")
                        .executes(context -> {
                            System.out.println("guild - Guild operations\n" +
                                    "       guild list - List all joined guilds\n" +
                                    "       guild goto - Goto a guild\n" +
                                    "       guild leave - Leave a guild\n" +
                                    "       guild join <Invite Code> - Join a guild");
                            return 1;
                        })
                )
                .then(Command.of("list")
                        .executes(context -> {
                            if (!KookCli.isLogged()) {
                                KookCli.getLogger().error("Please login first");
                                return 1;
                            }
                            List<JsonObject> guilds = KookCli.getPagableRequest(Routes.GUILD_LIST, new HashMap<>());
                            StringBuilder message = new StringBuilder();
                            for (int i = guilds.size() - 1; i >= 0; i--) {
                                message.append(guilds.get(i).get("name").getAsString()).append("\n");
                            }
                            System.out.println(message.append("Total: ").append(guilds.size()));
                            return 1;
                        }))
                .then(Command.of("goto")
                        .executes(context -> {
                            if (!KookCli.isLogged()) {
                                KookCli.getLogger().error("Please login first");
                                return 1;
                            }
                            List<JsonObject> guilds = KookCli.getPagableRequest(Routes.GUILD_LIST, new HashMap<>());
                            StringBuilder message = new StringBuilder();
                            for (int i = guilds.size() - 1; i >= 0; i--) {
                                message.append(i).append(" - ").append(guilds.get(i).get("name").getAsString()).append("\n");
                            }
                            String guildIndex = KookCli.getLineReader().readLine("==> ");
                            return 1;
                        }))
        );
    }

    private GuildCommand() {
    }

}