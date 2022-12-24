package net.deechael.kookcli.command;

import java.util.ArrayList;
import java.util.List;

public class SuggestionBuilder {

    private final List<String> result = new ArrayList<>();

    public List<String> getResult() {
        return result;
    }

    public SuggestionBuilder suggest(final String text) {
        result.add(text);
        return this;
    }

    public SuggestionBuilder suggest(final int value) {
        result.add(String.valueOf(value));
        return this;
    }

    public SuggestionBuilder suggest(final double value) {
        result.add(String.valueOf(value));
        return this;
    }

    public SuggestionBuilder suggest(final char value) {
        result.add(String.valueOf(value));
        return this;
    }

    public SuggestionBuilder suggest(final boolean value) {
        result.add(String.valueOf(value));
        return this;
    }

}
