package net.deechael.kookcli.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.deechael.kookcli.KookCli;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ConsoleCommandCompleter implements Completer {

    private final CommandDispatcher<ConsoleSender> commandDispatcher;

    public ConsoleCommandCompleter(CommandDispatcher<ConsoleSender> commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String buffer = line.line();
        boolean prefix = true;
        if (buffer.isEmpty() || buffer.charAt(0) != '/') {
            buffer = '/' + buffer;
            prefix = false;
        }

        final String input = buffer;
        StringReader stringReader = new StringReader(input);
        if (stringReader.canRead() && stringReader.peek() == '/')
            stringReader.skip();

        try {
            ParseResults<ConsoleSender> results = this.commandDispatcher.parse(stringReader, KookCli.getConsoleSender());
            Suggestions tabComplete = this.commandDispatcher.getCompletionSuggestions(results).get();
            for (Suggestion suggestion : tabComplete.getList()) {
                String completion = suggestion.getText();
                if (completion.isEmpty())
                    continue;
                boolean hasPrefix = prefix || completion.charAt(0) != '/';
                candidates.add(new Candidate(hasPrefix ? completion : completion.substring(1)));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            KookCli.getLogger().error("Failed to tab complete", e);
        }
    }

}
