package net.deechael.kookcli;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.deechael.kookcli.command.Argument;
import net.deechael.kookcli.command.Command;
import net.deechael.kookcli.command.defaults.ExitCommand;
import net.deechael.kookcli.command.defaults.LoginCommand;
import net.deechael.kookcli.util.StringUtil;
import org.fusesource.jansi.Ansi;
import org.jline.builtins.Completers;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private final static CommandDispatcher<ConsoleSender> COMMAND_DISPATCHER = new CommandDispatcher<>();

    private final static ConsoleSender SENDER = new ConsoleSender();

    private static List<Completers.TreeCompleter.Node> generateNodes(CommandNode<ConsoleSender> root) {
        List<Completers.TreeCompleter.Node> nodes = new ArrayList<>();
        for (CommandNode<ConsoleSender> commandNode : root.getChildren()) {
            List<Object> objects = new ArrayList<>();
            if (commandNode instanceof Argument.Node<?>) {
                objects.add(new StringsCompleter(((Argument.Node<?>) commandNode).getSuggestion())); // cannot complete if user input non-literal arguments
            } else {
                objects.add(commandNode.getName());
            }
            objects.addAll(generateNodes(commandNode));
            nodes.add(Completers.TreeCompleter.node(objects.toArray()));
        }
        return nodes;
    }

    private static Completer generateCompleter() {
        return new Completers.TreeCompleter(generateNodes(COMMAND_DISPATCHER.getRoot()));
    }

    private static void registerCommands() {
        LoginCommand.register(COMMAND_DISPATCHER);
        ExitCommand.register(COMMAND_DISPATCHER);
    }

    public static void main(String[] args) throws IOException {
        registerCommands();
        // TODO: Load plugins here
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        LineReader lineReader = LineReaderBuilder
                .builder()
                .terminal(terminal)
                .history(new DefaultHistory())
                .completer(generateCompleter())
                .build();
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int i) {
                terminal.writer().write(i);
            }
        }));
        KookCli.setTerminal(terminal);
        KookCli.setLineReader(lineReader);
        while (true) {
            try {
                String command = lineReader.readLine("> ");
                COMMAND_DISPATCHER.execute(StringUtil.strip(command), SENDER);
            } catch (UserInterruptException exception) {
                System.out.println(Ansi.ansi().fgRed().a("Please exit with command \"exit\"").reset());
            } catch (CommandSyntaxException e) {
                System.out.println(Ansi.ansi().fgRed().a(e.getMessage()).reset());
            }
        }
    }

}