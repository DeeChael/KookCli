package net.deechael.kookcli.command.defaults;

import com.mojang.brigadier.CommandDispatcher;
import net.deechael.kookcli.KookCli;
import net.deechael.kookcli.command.Command;
import net.deechael.kookcli.command.ConsoleSender;

public final class LogoutCommand {

    public static void register(CommandDispatcher<ConsoleSender> commandDispatcher) {
        commandDispatcher.register(Command.of("logout")
                .requires(sender -> KookCli.isLogged())
                .executes(context -> {
                    if (!KookCli.isLogged()) {
                        System.out.println("You haven't logged yet");
                        return 1;
                    }
                    KookCli.logout();
                    System.out.println("Logged out successfully");
                    return 1;
                }));
    }

    private LogoutCommand() {
    }

}
