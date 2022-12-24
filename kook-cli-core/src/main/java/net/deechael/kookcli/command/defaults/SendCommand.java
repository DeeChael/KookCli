package net.deechael.kookcli.command.defaults;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.deechael.kookcli.KookCli;
import net.deechael.kookcli.command.Argument;
import net.deechael.kookcli.command.Command;
import net.deechael.kookcli.command.ConsoleSender;
import net.deechael.kookcli.network.Routes;
import net.deechael.kookcli.util.ParamsUtil;

public final class SendCommand {

    public static void register(CommandDispatcher<ConsoleSender> commandDispatcher) {
        commandDispatcher.register(Command.of("send")
                .then(Command.of("text")
                        .then(Argument.of("content", StringArgumentType.string())
                                .executes(context -> {
                                    if (!KookCli.isLogged()) {
                                        KookCli.getLogger().error("Please login first");
                                        return 1;
                                    }
                                    if (KookCli.getCurrentGuild() == null) {
                                        KookCli.getLogger().error("You are not in a guild");
                                        return 1;
                                    }
                                    if (KookCli.getCurrentChannel() == null) {
                                        KookCli.getLogger().error("You are not in a guild");
                                        return 1;
                                    }
                                    KookCli.postRequest(Routes.MESSAGE_CREATE,
                                            ParamsUtil.json(
                                                    ParamsUtil.param("channel_id", KookCli.getCurrentChannel()),
                                                    ParamsUtil.param("type", "1"),
                                                    ParamsUtil.param("content", StringArgumentType.getString(context, "content"))
                                            ));
                                    return 1;
                                })
                        )
                )
        );
    }

    private SendCommand() {
    }

}
