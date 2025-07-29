package com.alathreon.alahexeditor;

import com.alathreon.alahexeditor.controller.HexEditorController;
import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.util.FileData;
import com.alathreon.alahexeditor.util.IOUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private FileData fileData = new FileData(null, new ByteView(new byte[1024]));
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent parent = fxmlLoader.load(getClass().getResourceAsStream("/HexEditor.fxml"));
        HexEditorController controller = fxmlLoader.getController();
        controller.setOnPromptOpen(() -> {
            FileData tmp = IOUtil.promptOpen(primaryStage);
            if(tmp != null) {
                fileData = tmp;
                controller.setData(fileData);
            }
        });
        controller.setOnSave(() -> fileData = IOUtil.save(primaryStage, fileData));
        controller.setOnPromptSave(() -> fileData = IOUtil.promptSave(primaryStage, fileData));
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
