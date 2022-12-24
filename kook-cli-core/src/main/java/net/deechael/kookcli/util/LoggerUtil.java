package net.deechael.kookcli.util;

public final class LoggerUtil {

    // public static Logger getLogger(Class<?> clazz, Level level) {
    //     ch.qos.logback.classic.Logger lgr = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(clazz);
    //     LoggerContext loggerContext = lgr.getLoggerContext();
    //     loggerContext.reset();
//
    //     PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    //     encoder.setContext(loggerContext);
    //     encoder.setPattern("[%date] [%thread] [%level] %message%n");
    //     encoder.start();
//
    //     ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
    //     appender.setContext(loggerContext);
    //     appender.setEncoder(encoder);
    //     appender.start();
//
    //     lgr.addAppender(appender);
    //     lgr.setLevel(level);
    //     return lgr;
    // }

    private LoggerUtil() {
    }

}
