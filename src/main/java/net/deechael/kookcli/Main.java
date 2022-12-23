package net.deechael.kookcli;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.deechael.kookcli.command.Argument;
import net.deechael.kookcli.command.Command;
import net.deechael.kookcli.command.defaults.ExitCommand;
import net.deechael.kookcli.command.defaults.GuildCommand;
import net.deechael.kookcli.command.defaults.LoginCommand;
import net.deechael.kookcli.command.defaults.LogoutCommand;
import net.deechael.kookcli.util.StringUtil;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.Locale;

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
        LogoutCommand.register(COMMAND_DISPATCHER);
        GuildCommand.register(COMMAND_DISPATCHER);
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
        }) {
            @Override
            public void println(int i) {
                terminal.writer().println(i);
                terminal.writer().flush();
            }

            @Override
            public void println(char c) {
                terminal.writer().println(c);
                terminal.writer().flush();
            }

            @Override
            public void println(long l) {
                terminal.writer().println(l);
                terminal.writer().flush();
            }

            @Override
            public void println(float v) {
                terminal.writer().println(v);
                terminal.writer().flush();
            }

            @Override
            public void println(double v) {
                terminal.writer().println(v);
                terminal.writer().flush();
            }

            @Override
            public void println(@Nullable Object o) {
                terminal.writer().println(o);
                terminal.writer().flush();
            }

            @Override
            public void println(@Nullable String s) {
                terminal.writer().println(s);
                terminal.writer().flush();
            }

            @Override
            public void println(boolean b) {
                terminal.writer().println(b);
                terminal.writer().flush();
            }

            @Override
            public void println(@NotNull char[] chars) {
                terminal.writer().println(chars);
                terminal.writer().flush();
            }

            @Override
            public void print(int i) {
                terminal.writer().print(i);
                terminal.writer().flush();
            }

            @Override
            public void print(char c) {
                terminal.writer().print(c);
                terminal.writer().flush();
            }

            @Override
            public void print(long l) {
                terminal.writer().print(l);
                terminal.writer().flush();
            }

            @Override
            public void print(float v) {
                terminal.writer().print(v);
                terminal.writer().flush();
            }

            @Override
            public void print(double v) {
                terminal.writer().print(v);
                terminal.writer().flush();
            }

            @Override
            public void print(@Nullable Object o) {
                terminal.writer().print(o);
                terminal.writer().flush();
            }

            @Override
            public void print(@Nullable String s) {
                terminal.writer().print(s);
                terminal.writer().flush();
            }

            @Override
            public void print(boolean b) {
                terminal.writer().print(b);
                terminal.writer().flush();
            }

            @Override
            public void print(@NotNull char[] chars) {
                terminal.writer().print(chars);
                terminal.writer().flush();
            }

            @Override
            public PrintStream printf(@NotNull String s, Object... objects) {
                terminal.writer().printf(s, objects);
                terminal.writer().flush();
                return this;
            }

            @Override
            public PrintStream printf(Locale locale, @NotNull String s, Object... objects) {
                terminal.writer().printf(locale, s, objects);
                terminal.writer().flush();
                return this;
            }
        });
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