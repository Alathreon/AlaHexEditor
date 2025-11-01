package com.alathreon.alahexeditor.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PersistenceAPI {
    private static final String FOLDER = ".AlaHexEditor";
    Path folderPath() {
        return Path.of(System.getProperty("user.home")).resolve(FOLDER);
    }
    Path path(String name) {
        return folderPath().resolve(name);
    }
    Path path(String folder, String name) {
        return folderPath().resolve(folder).resolve(name);
    }
    void ensureExists(String name) throws IOException {
        Files.createDirectories(folderPath());
        Path path = path(name);
        if(!Files.exists(path)) {
            Files.writeString(path, "");
        }
    }
    void ensureExists(String folder, String name) throws IOException {
        Files.createDirectories(folderPath().resolve(folder));
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
    void reset(String folder, String name) {
        try {
            ensureExists(folder, name);
            Files.writeString(path(folder, name), "");
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
    List<String> read(String folder, String name) {
        try {
            ensureExists(folder, name);
            return Files.readAllLines(path(folder, name));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    String readString(String name) {
        try {
            ensureExists(name);
            return Files.readString(path(name));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    String readString(String folder, String name) {
        try {
            ensureExists(folder, name);
            return Files.readString(path(folder, name));
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
    void write(String folder, String name, List<? extends Object> lines) {
        try {
            ensureExists(folder, name);
            Files.write(path(folder, name), lines.stream().map(Object::toString).toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    void writeString(String name, String s) {
        try {
            ensureExists(name);
            Files.writeString(path(name), s);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    void writeString(String folder, String name, String s) {
        try {
            ensureExists(folder, name);
            Files.writeString(path(folder, name), s);
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
    <T> T resetIfError(String folder, String name, T ifError, BiFunction<String, String, T> function) {
        try {
            return function.apply(folder, name);
        } catch (Exception e) {
            reset(folder, name);
        }
        return ifError;
    }
    List<String> list(String folder) {
        return list(folder, f -> true);
    }
    List<String> list(String folder, Predicate<String> filter) {
        Path path = folderPath().resolve(folder);
        try (Stream<Path> stream = Files.list(path)) {
            return stream.map(path::relativize).map(Path::toString).filter(filter).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
