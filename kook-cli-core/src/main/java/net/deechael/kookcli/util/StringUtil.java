package net.deechael.kookcli.util;

public final class StringUtil {

    public static String strip(String string) {
        while (string.endsWith(" "))
            string = string.substring(0, string.length() - 1);
        return string;
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private StringUtil() {
    }

}
