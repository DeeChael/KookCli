package net.deechael.kcm4t.utils;

import java.util.ArrayList;
import java.util.List;

public class Container<T> {

    private T value = null;

    private T defaultValue = null;

    private final List<Class<? extends T>> classes = new ArrayList<>();

    public Container() {
    }

    public Container(T initialValue) {
        this.value = initialValue;
    }

    public <E extends T> Container<T> accessible(Class<E> clazz) {
        if (clazz != null)
            this.classes.add(clazz);
        return this;
    }

    public T getValue() {
        return value == null ? this.defaultValue : this.value;
    }

    public Container<T> withDefault(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public void setValue(T value) {
        if (!this.classes.contains(value.getClass()))
            throw new RuntimeException("Unacceptable class type");
        this.value = value;
    }

}
