package net.deechael.kookcli.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.deechael.kookcli.KookCli;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.fusesource.jansi.Ansi;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public class Console extends SimpleTerminalConsole {

    public Console() {
    }

    @Override
    protected boolean isRunning() {
        return true;
    }

    @Override
    protected void runCommand(String s) {
        try {
            KookCli.getCommandDispatcher().execute(s, KookCli.getConsoleSender());
        } catch (CommandSyntaxException e) {
            System.out.println(Ansi.ansi().fgRed().a(e.getMessage()).reset());
        }
    }

    @Override
    protected void shutdown() {
        KookCli.exit();
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        builder.appName("KookCli")
                .completer(new ConsoleCommandCompleter(KookCli.getCommandDispatcher()));
        LineReader reader = super.buildReader(builder);
        KookCli.setLineReader(reader);
        return reader;
    }

}
