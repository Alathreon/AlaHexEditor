package com.alathreon.alahexeditor;

import com.alathreon.alahexeditor.controller.HexEditorController;
import com.alathreon.alahexeditor.persistence.Persistence;
import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.util.FileData;
import com.alathreon.alahexeditor.util.IOUtil;
import com.alathreon.alahexeditor.util.Position;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

public class App extends Application {
    private FileData fileData;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent parent = fxmlLoader.load(getClass().getResourceAsStream("/HexEditor.fxml"));
        HexEditorController controller = fxmlLoader.getController();
        Persistence persistence = Persistence.create();

        initLoadData(controller, persistence);
        initEvents(primaryStage, controller, persistence);

        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();
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
    private void addRecentlyOpened(HexEditorController controller, Persistence persistence, Path path) {
        controller.setRecentlyOpened(persistence.recentlyOpened().add(path));
    }
}
