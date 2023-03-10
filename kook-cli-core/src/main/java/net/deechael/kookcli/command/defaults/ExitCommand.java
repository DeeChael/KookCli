package net.deechael.kookcli.command.defaults;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.deechael.kookcli.KookCli;
import net.deechael.kookcli.command.ConsoleSender;

public final class ExitCommand {

    public static void register(CommandDispatcher<ConsoleSender> commandDispatcher) {
        commandDispatcher.register(LiteralArgumentBuilder.<ConsoleSender>literal("exit").executes(context -> {
            KookCli.exit();
            return 1;
        }));
    }

    private ExitCommand() {
    }

}
