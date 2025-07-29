package com.alathreon.alahexeditor.controller;

import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.component.CustomTextFieldTableCell;
import com.alathreon.alahexeditor.util.FileData;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

public class HexEditorController implements Initializable {
    @FXML private TableView<ByteView> table;
    private Runnable onPromptOpen;
    private Runnable onPromptSave;
    private Runnable onSave;
    @FXML
    private void onPromptOpen(ActionEvent event) {
        this.onPromptOpen.run();
    }
    @FXML
    private void onPromptSave(ActionEvent event) {
        this.onPromptSave.run();
    }
    @FXML
    private void onSave(ActionEvent event) {
        this.onSave.run();
    }

    public void setOnPromptOpen(Runnable onPromptOpen) {
        this.onPromptOpen = onPromptOpen;
    }
    public void setOnPromptSave(Runnable onPromptSave) {
        this.onPromptSave = onPromptSave;
    }
    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }
    public void setData(FileData fileData) {
        table.getItems().clear();
        if(fileData == null) return;
        ByteView view = fileData.data();
        for(int i = 0; i <= view.length() / 16; i++) {
            table.getItems().add(view.subView(i * 16, Math.min(16, view.length() - i * 16)));
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setEditable(true);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(p -> true);
        table.setSortPolicy(byteViewTableView -> false);
        addColumn("    ", 4, v -> "%04X".formatted(v.offset()));
        for(int i = 0; i < 16; i++) {
            int j = i;
            TableColumn<ByteView, String> column = addColumn("%X".formatted(i), 2, v -> v.length() <= j ? "  " : v.subView(j, 1).toString());
            column.setCellFactory(tableColumn -> actionSetCellFactory(j));
            column.setOnEditCommit(event -> actionSetOnEditCommit(j, event));
        }
        addColumn("0123456789ABCDEF", 16, ByteView::toUTF8String);
    }
    private TableCell<ByteView,String> actionSetCellFactory(int col) {
        CustomTextFieldTableCell cell = new CustomTextFieldTableCell();
        HexEditorTextFormatterFilter filter = new HexEditorTextFormatterFilter(col, table, cell);
        cell.setOnTextFieldCreated(textField -> textField.setTextFormatter(new TextFormatter<>(filter)));
        return cell;
    }
    private void actionSetOnEditCommit(int col, TableColumn.CellEditEvent<ByteView, String> event) {
        String s = switch (event.getNewValue().length()) {
            case 0 -> "00";
            case 1 -> "0" + event.getNewValue();
            case 2 -> event.getNewValue();
            default -> event.getNewValue().substring(0, 2);
        };
        event.getRowValue().set(col, s);
        table.refresh();
    }
    private TableColumn<ByteView, String> addColumn(String text, int expectedChars, Function<ByteView, String> mapper) {
        TableColumn<ByteView, String> c = new TableColumn<>();
        c.setResizable(false);
        c.setText(text);
        c.setEditable(true);
        c.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        c.setPrefWidth(getSize(expectedChars));
        table.getColumns().add(c);
        return c;
    }
    private double getSize(int chars) {
        Font font = Font.font("Courier New");
        Label label = new Label(" ".repeat(chars));
        label.setFont(font);
        new Scene(new StackPane(label));
        label.applyCss();
        return label.prefWidth(-1) + 10;
    }
}
