package com.alathreon.alahexeditor.persistence;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecentlyOpenedModule {
    private static final String FILE_NAME = "recently_opened.txt";
    private static final int MAX_ENTRY_COUNT = 20;
    private final PersistenceAPI api;

    public RecentlyOpenedModule(PersistenceAPI api) {
        this.api = api;
    }

    public List<Path> read() {
        return api.resetIfError(FILE_NAME, List.of(),
                name -> api.read(name).stream().map(Path::of).toList());
    }
    public List<Path> add(Path newPath) {
        List<Path> paths = new ArrayList<>(read());
        if(paths.contains(newPath)) {
            paths.remove(newPath);
        } else if(paths.size() > MAX_ENTRY_COUNT-1) {
            paths.removeLast();
        }
        paths.add(0, newPath);
        api.write(FILE_NAME, paths);
        return paths;
    }
}
