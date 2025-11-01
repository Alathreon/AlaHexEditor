package com.alathreon.alahexeditor.util;

import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.persistence.Persistence;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOUtil {
    public static final String TEMPLATE_EXTENSION = ".alahex.template.json";

    private IOUtil() {
    }

    public static void alertError(String title, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("An error happened");
        alert.setContentText(ex.getMessage());
        alert.show();
    }
    public static FileData read(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new FileData(path, new ByteView(bytes));
        } catch (IOException e) {
            alertError("Opening a file", e);
        }
        return null;
    }
    public static FileData promptOpen(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load a binary file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            Path path = selectedFile.toPath();
            try {
                byte[] bytes = Files.readAllBytes(path);
                return new FileData(selectedFile.toPath(), new ByteView(bytes));
            } catch (IOException e) {
                alertError("Opening a file", e);
            }
        }
        return null;
    }
    public static FileData promptSave(Stage stage, FileData fileData) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save a binary file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        if(fileData.path() != null) {
            fileChooser.setInitialDirectory(fileData.path().getParent().toFile());
            fileChooser.setInitialFileName(fileData.path().getFileName().toString());
        }
        File selectedFile = fileChooser.showSaveDialog(stage);
        if(selectedFile != null) {
            return save(stage, new FileData(selectedFile.toPath(), fileData.data()));
        } else {
            return fileData;
        }
    }
    public static FileData save(Stage stage, FileData fileData) {
        if(fileData.path() == null) {
            return promptSave(stage, fileData);
        }
        try {
            Files.write(fileData.path(), fileData.data().getBytes());
        } catch (IOException e) {
            alertError("Saving a file", e);
        }
        return fileData;
    }
    public static void copyToClipboard(ByteView byteView) {
        StringSelection stringSelection = new StringSelection(byteView.toFormmatedString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
    public static ByteView pasteFromClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            String clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);
            return ByteView.fromFormattedString(clipboardText);
        } catch (UnsupportedFlavorException | IOException e) {
            alertError("Pasting from clipboard", e);
            return null;
        }
    }

    public static FileTemplate readTemplate(ObjectMapper mapper,Path path) {
        try {
            String json = Files.readString(path);
            return new FileTemplate(path, parseTemplate(mapper, json));
        } catch (IOException e) {
            alertError("Opening a template file", e);
        }
        return null;
    }
    public static FileTemplate promptOpenTemplate(ObjectMapper mapper, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load a template");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("AlaHxEditor Templates", "*" + TEMPLATE_EXTENSION));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            Path path = selectedFile.toPath();
            try {
                String json = Files.readString(path);
                return new FileTemplate(path, parseTemplate(mapper, json));
            } catch (IOException | UncheckedIOException e) {
                alertError("Opening a template", e);
            }
        }
        return null;
    }
    public static FileTemplate promptSaveTemplate(ObjectMapper mapper, FileTemplate fileTemplate, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save a fileTemplate");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("AlaHxEditor Templates", "*" + TEMPLATE_EXTENSION));
        if(fileTemplate.path() != null) {
            fileChooser.setInitialDirectory(fileTemplate.path().getParent().toFile());
            fileChooser.setInitialFileName(fileTemplate.path().getFileName().toString());
        }
        File selectedFile = fileChooser.showSaveDialog(stage);
        if(selectedFile != null) {
            return saveTemplate(mapper, new FileTemplate(selectedFile.toPath(), fileTemplate.template()), stage);
        } else {
            return fileTemplate;
        }
    }
    public static FileTemplate saveTemplate(ObjectMapper mapper, FileTemplate fileTemplate, Stage stage) {
        if(fileTemplate.path() == null) {
            return promptSaveTemplate(mapper, fileTemplate, stage);
        }
        try {
            Files.writeString(fileTemplate.path(), writeTemplate(mapper, fileTemplate.template()));
        } catch (IOException e) {
            alertError("Saving a file", e);
        }
        return fileTemplate;
    }

    public static Template parseTemplate(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, Template.class);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
    public static String writeTemplate(ObjectMapper mapper, Template template) {
        try {
            return mapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
