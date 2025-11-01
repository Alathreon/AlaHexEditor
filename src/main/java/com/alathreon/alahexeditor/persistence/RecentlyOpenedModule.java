package com.alathreon.alahexeditor.persistence;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecentlyOpenedModule {
    private static final String FILE_NAME = "recently_opened";
    private static final String EXTENSION = ".txt";
    private static final int MAX_ENTRY_COUNT = 20;
    private final PersistenceAPI api;
    private final String suffix;

    public RecentlyOpenedModule(PersistenceAPI api, String suffix) {
        this.api = api;
        this.suffix = suffix;
    }

    private String getName() {
        return (suffix == null ? FILE_NAME : FILE_NAME + "." + suffix) + EXTENSION;
    }

    public List<Path> read() {
        return api.resetIfError(getName(), List.of(),
                name -> api.read(name).stream().map(Path::of).toList());
    }
    public List<Path> add(Path newPath) {
        List<Path> paths = new ArrayList<>(read());
        if(paths.contains(newPath)) {
            paths.remove(newPath);
        } else if(paths.size() > MAX_ENTRY_COUNT-1) {
            paths.removeLast();
        }
        paths.addFirst(newPath);
        api.write(getName(), paths);
        return paths;
    }
}
