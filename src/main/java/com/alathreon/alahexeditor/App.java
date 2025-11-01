package com.alathreon.alahexeditor;

import com.alathreon.alahexeditor.controller.EventKind;
import com.alathreon.alahexeditor.controller.HexEditorController;
import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.Parser;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.persistence.Persistence;
import com.alathreon.alahexeditor.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.alathreon.alahexeditor.util.IOUtil.alertError;

public class App extends Application {

    private FileData fileData;

    private FileTemplate fileTemplate;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent parent = fxmlLoader.load(getClass().getResourceAsStream("/HexEditor.fxml"));
        HexEditorController controller = fxmlLoader.getController();
        Persistence persistence = Persistence.create();

        Scene scene = new Scene(parent);
        scene.getAccelerators().putAll(getShortcuts(controller));
        primaryStage.setScene(scene);
        primaryStage.show();

        initLoadData(controller, persistence);
        initEvents(primaryStage, controller, persistence);
    }

    private void initLoadData(HexEditorController controller, Persistence persistence) {
        List<Path> recentlyOpened = persistence.recentlyOpened().read();
        controller.setRecentlyOpened(recentlyOpened);
        FileData tmp = null;
        if(!recentlyOpened.isEmpty()) {
            tmp = IOUtil.read(recentlyOpened.getFirst());
        }
        if(tmp != null) {
            fileData = tmp;
        } else {
            fileData = new FileData(null, new ByteView(new byte[0]));
        }
        controller.setData(fileData);

        List<Path> recentlyOpenedTemplates = persistence.recentlyOpenedTemplates().read();
        controller.setRecentlyOpenedTemplates(recentlyOpenedTemplates);
        FileTemplate tmpTemplate = null;
        if(!recentlyOpenedTemplates.isEmpty()) {
            tmpTemplate = IOUtil.readTemplate(persistence.mapper(), recentlyOpenedTemplates.getFirst());
        }
        if(tmpTemplate != null) {
            fileTemplate = tmpTemplate;
            loadParseData(controller);
        }
    }

    private void initEvents(Stage primaryStage, HexEditorController controller, Persistence persistence) {
        controller.setOnNew(() -> {
            fileData =  new FileData(null, new ByteView(new byte[0]));
            controller.setData(fileData);
        });
        controller.setOnPromptOpen(() -> {
            FileData tmp = IOUtil.promptOpen(primaryStage);
            if(tmp != null) {
                fileData = tmp;
                controller.setData(fileData);
                addRecentlyOpened(controller, persistence, tmp.path());
            }
        });
        controller.setOnRecentOpen(path -> {
            FileData tmp = IOUtil.read(path);
            if(tmp != null) {
                fileData = tmp;
                controller.setData(fileData);
                addRecentlyOpened(controller, persistence, path);
            }
        });
        controller.setOnSave(() -> {
            fileData = IOUtil.save(primaryStage, fileData);
            if(fileData.path() != null) {
                addRecentlyOpened(controller, persistence, fileData.path());
            }
        });
        controller.setOnPromptSave(() -> {
            fileData = IOUtil.promptSave(primaryStage, fileData);
            if(fileData.path() != null) {
                addRecentlyOpened(controller, persistence, fileData.path());
            }
        });

        controller.setOnPromptOpenTemplate(() -> {
            FileTemplate tmp = IOUtil.promptOpenTemplate(persistence.mapper(), primaryStage);
            if(tmp != null) {
                fileTemplate = tmp;
                loadParseData(controller);
                addRecentlyOpenedTemplate(controller, persistence, tmp.path());
            }
        });
        controller.setOnRecentOpenTemplate(path -> {
            FileTemplate tmp = IOUtil.readTemplate(persistence.mapper(), path);
            if(tmp != null) {
                fileTemplate = tmp;
                loadParseData(controller);
                addRecentlyOpenedTemplate(controller, persistence, path);
            }
        });

        controller.setOnLengthIncremented(toAdd -> {
            fileData = new FileData(fileData.path(), fileData.data().withIncreasedLength(toAdd));
            controller.setData(fileData);
        });

        controller.setOnCopy(positions -> {
            List<Position> toCopy = positions.toList();
            ByteView copied = fileData.data().withAll(toCopy);
            IOUtil.copyToClipboard(copied);
        });
        controller.setOnPaste(position -> {
            if(position == null) {
                position = new Position(0, 0);
            }
            ByteView toPaste = IOUtil.pasteFromClipboard();
            if(toPaste == null) return;
            ByteView bytes = fileData.data().withInsert(toPaste, position);
            fileData = new FileData(fileData.path(), bytes);
            controller.setData(fileData);
        });
        controller.setOnCut(positions -> {
            List<Position> toCopy = positions.toList();
            ByteView copied = fileData.data().withAll(toCopy);
            IOUtil.copyToClipboard(copied);
            ByteView cut = fileData.data().withoutAll(new HashSet<>(toCopy));
            fileData = new FileData(fileData.path(), cut);
            controller.setData(fileData);
        });
        controller.setOnDelete(positions -> {
            List<Position> toCopy = positions.toList();
            ByteView cut = fileData.data().withoutAll(new HashSet<>(toCopy));
            fileData = new FileData(fileData.path(), cut);
            controller.setData(fileData);
        });

        controller.setOnQuit(Platform::exit);
    }

    private Map<KeyCombination, Runnable> getShortcuts(HexEditorController controller) {
        return Map.ofEntries(
                Map.entry(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.NEW)),
                Map.entry(new KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.PROMPT_OPEN_TEMPLATE)),
                Map.entry(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.PROMPT_OPEN)),
                Map.entry(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.SAVE)),
                Map.entry(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.PROMPT_SAVE)),
                Map.entry(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.CUT)),
                Map.entry(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.COPY)),
                Map.entry(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.PASTE)),
                Map.entry(new KeyCodeCombination(KeyCode.DELETE), () -> controller.fireEvent(EventKind.DELETE)),
                Map.entry(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.SELECT_ALL)),
                Map.entry(new KeyCodeCombination(KeyCode.A, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.UNSELECT_ALL)),
                Map.entry(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN), () -> controller.fireEvent(EventKind.QUIT))
        );
    }

    private void addRecentlyOpened(HexEditorController controller, Persistence persistence, Path path) {
        controller.setRecentlyOpened(persistence.recentlyOpened().add(path));
    }
    private void addRecentlyOpenedTemplate(HexEditorController controller, Persistence persistence, Path path) {
        controller.setRecentlyOpenedTemplates(persistence.recentlyOpenedTemplates().add(path));
    }

    private void loadParseData(HexEditorController controller) {
        try {
            List<Pair<String, ParseObject>> parsed = new Parser().parse(fileTemplate.template(), fileData.data());
            controller.setParsedData(parsed);
        } catch (ParseException e) {
            alertError("Couldn't parse data.", e);
        }
    }
}
