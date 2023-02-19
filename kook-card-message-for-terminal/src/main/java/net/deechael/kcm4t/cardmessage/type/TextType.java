package net.deechael.kcm4t.cardmessage.type;

import java.util.Objects;

public enum TextType {

    PLAIN_TEXT("plain-text"),
    KMARKDOWN("kmarkdown");

    private final String s;

    TextType(String s) {
        this.s = s;
    }

    public String value() {
        return this.s;
    }

    public static TextType of(String s) {
        return Objects.equals(s, "kmarkdown") ? KMARKDOWN : PLAIN_TEXT;
    }

}
