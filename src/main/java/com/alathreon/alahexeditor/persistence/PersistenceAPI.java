package com.alathreon.alahexeditor.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class PersistenceAPI {
    private static final String FOLDER = ".AlaHexEditor";
    Path folderPath() {
        return Path.of(System.getProperty("user.home")).resolve(FOLDER);
    }
    Path path(String name) {
        return folderPath().resolve(name);
    }
    void ensureExists(String name) throws IOException {
        Files.createDirectories(folderPath());
        Path path = path(name);
        if(!Files.exists(path)) {
            Files.writeString(path, "");
        }
    }
    void reset(String name) {
        try {
            ensureExists(name);
            Files.writeString(path(name), "");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    List<String> read(String name) {
        try {
            ensureExists(name);
            return Files.readAllLines(path(name));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    void write(String name, List<? extends Object> lines) {
        try {
            ensureExists(name);
            Files.write(path(name), lines.stream().map(Object::toString).toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    <T> T resetIfError(String name, T ifError, Function<String, T> function) {
        try {
            return function.apply(name);
        } catch (Exception e) {
            reset(name);
        }
        return ifError;
    }
}
