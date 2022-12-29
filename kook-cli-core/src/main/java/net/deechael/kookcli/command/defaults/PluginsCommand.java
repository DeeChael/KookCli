package net.deechael.kookcli.command.defaults;

import com.mojang.brigadier.CommandDispatcher;
import net.deechael.kookcli.KookCli;
import net.deechael.kookcli.command.Command;
import net.deechael.kookcli.command.ConsoleSender;

public final class PluginsCommand {

    public static void register(CommandDispatcher<ConsoleSender> commandDispatcher) {
        commandDispatcher.register(Command.of("plugins")
                .executes(context -> {
                    StringBuilder builder = new StringBuilder("Plugins: ");
                    for (String plugin : KookCli.getPluginManager().loadedPlugins())
                        builder.append(plugin).append(", ");
                    KookCli.getLogger().info(builder.substring(0, builder.length() - 2));
                    return 1;
                })
        );
    }

    private PluginsCommand() {}

}
