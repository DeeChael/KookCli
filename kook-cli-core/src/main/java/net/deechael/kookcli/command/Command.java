package net.deechael.kookcli.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class Command {

    public static LiteralArgumentBuilder<ConsoleSender> of(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

}
