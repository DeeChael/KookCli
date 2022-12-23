package net.deechael.kookcli.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.deechael.kookcli.ConsoleSender;

public class Command {

    public static LiteralArgumentBuilder<ConsoleSender> of(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

}
