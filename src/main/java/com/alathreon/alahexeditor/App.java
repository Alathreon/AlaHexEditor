package com.alathreon.alahexeditor;

import com.alathreon.alahexeditor.controller.HexEditorController;
import com.alathreon.alahexeditor.persistence.Persistence;
import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.util.FileData;
import com.alathreon.alahexeditor.util.IOUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private FileData fileData = new FileData(null, new ByteView(new byte[0]));
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent parent = fxmlLoader.load(getClass().getResourceAsStream("/HexEditor.fxml"));
        HexEditorController controller = fxmlLoader.getController();
        Persistence persistence = Persistence.create();
        controller.setRecentlyOpened(persistence.recentlyOpened().read());
        controller.setOnPromptOpen(() -> {
            FileData tmp = IOUtil.promptOpen(primaryStage);
            if(tmp != null) {
                fileData = tmp;
                controller.setData(fileData);
                controller.setRecentlyOpened(persistence.recentlyOpened().add(tmp.path()));
            }
        });
        controller.setOnRecentOpen(path -> {
            FileData tmp = IOUtil.read(path);
            if(tmp != null) {
                fileData = tmp;
                controller.setData(fileData);
                controller.setRecentlyOpened(persistence.recentlyOpened().add(path));
            }
        });
        controller.setOnSave(() -> fileData = IOUtil.save(primaryStage, fileData));
        controller.setOnPromptSave(() -> fileData = IOUtil.promptSave(primaryStage, fileData));
        controller.setOnLengthIncremented(toAdd -> {
            fileData = new FileData(fileData.path(), fileData.data().withIncreasedLength(toAdd));
            controller.setData(fileData);
        });
        controller.setData(fileData);
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
