package com.alathreon.alahexeditor.util;

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
import java.nio.file.Files;
import java.nio.file.Path;

public class IOUtil {
    private IOUtil() {
    }

    private static void alertError(String title, Exception ex) {
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
}
