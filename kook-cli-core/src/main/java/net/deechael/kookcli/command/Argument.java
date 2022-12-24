package net.deechael.kookcli.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

public class Argument {


    public static <T> RequiredArgumentBuilder<ConsoleSender, T> of(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

}
