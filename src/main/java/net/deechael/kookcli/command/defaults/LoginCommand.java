package net.deechael.kookcli.command.defaults;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.deechael.kookcli.ConsoleSender;
import net.deechael.kookcli.KookCli;
import net.deechael.kookcli.command.Argument;
import net.deechael.kookcli.command.Command;

public final class LoginCommand {

    public static void register(CommandDispatcher<ConsoleSender> commandDispatcher) {
        commandDispatcher.register(Command
                .of("login")
                .then(Command.of("help")
                        .executes(context -> {
                            System.out.println("login - Login your account\n" +
                                    "       login token <token> - Login as bot\n" +
                                    "       login user <phone> - Login as user");
                            return 1;
                        }))
                .then(Command.of("token").then(
                        RequiredArgumentBuilder.<ConsoleSender, String>argument("token", StringArgumentType.string())
                                .executes(context -> {
                                    if (KookCli.isLogged()) {
                                        KookCli.getLogger().error("Has logged in!");
                                        return 1;
                                    }
                                    KookCli.login(StringArgumentType.getString(context, "token"));
                                    return 1;
                                })
                ))
                .then(Command.of("user").then(
                        Argument.of("phone", StringArgumentType.string())
                                .executes(context -> {
                                    if (KookCli.isLogged()) {
                                        KookCli.getLogger().error("Has logged in!");
                                        return 1;
                                    }
                                    String phone = StringArgumentType.getString(context, "phone");
                                    String password = KookCli.getLineReader().readLine("Password> ", (char) 0);
                                    KookCli.login(phone, password);
                                    return 1;
                                })
                ))
        );
    }

    private LoginCommand() {
    }

}
