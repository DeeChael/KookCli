package net.deechael.kookcli.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class DependencyClassLoader extends URLClassLoader {

    public DependencyClassLoader(URL[] urls) {
        super(urls, Thread.currentThread().getContextClassLoader());
    }

}
