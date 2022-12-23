package net.deechael.kookcli.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.deechael.kookcli.ConsoleSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Argument<T> extends ArgumentBuilder<ConsoleSender, Argument<T>> {

    private final String name;
    private final ArgumentType<T> type;
    private SuggestionProvider<ConsoleSender> suggestionsProvider = null;
    private Suggestion suggestion;

    private Argument(final String name, final ArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    public Argument<T> suggests(final Suggestion suggestion) {
        this.suggestionsProvider = ((context, builder) -> {
            SuggestionBuilder build = new SuggestionBuilder();
            suggestion.suggest(build);
            build.getResult().forEach(builder::suggest);
            return builder.buildFuture();
        });
        return getThis();
    }

    public SuggestionProvider<ConsoleSender> getSuggestionsProvider() {
        return suggestionsProvider;
    }

    @Override
    protected Argument<T> getThis() {
        return this;
    }

    public ArgumentType<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public CommandNode<ConsoleSender> build() {
        final ArgumentCommandNode<ConsoleSender, T> result = new Node<>(getName(), getType(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), getSuggestionsProvider(), this.suggestion);

        for (final CommandNode<ConsoleSender> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }

    public static <T> Argument<T> of(String name, ArgumentType<T> type) {
        return new Argument<>(name, type);
    }

    public static class Node<T> extends ArgumentCommandNode<ConsoleSender, T> {

        private final Suggestion suggestion;

        public Node(String name, ArgumentType<T> type, Command<ConsoleSender> command, Predicate<ConsoleSender> requirement, CommandNode<ConsoleSender> redirect, RedirectModifier<ConsoleSender> modifier, boolean forks, SuggestionProvider<ConsoleSender> customSuggestions, Suggestion suggestion) {
            super(name, type, command, requirement, redirect, modifier, forks, customSuggestions);
            this.suggestion = suggestion;
        }

        public List<String> getSuggestion() {
            if (suggestion == null)
                return new ArrayList<>();
            SuggestionBuilder builder = new SuggestionBuilder();
            suggestion.suggest(builder);
            return builder.getResult();
        }

    }

}
