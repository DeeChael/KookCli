package net.deechael.kookcli.command;

@FunctionalInterface
public interface Suggestion {

    void suggest(SuggestionBuilder suggestion);

}
