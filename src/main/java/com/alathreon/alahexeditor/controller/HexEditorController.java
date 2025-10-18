package com.alathreon.alahexeditor.controller;

import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.component.CustomTextFieldTableCell;
import com.alathreon.alahexeditor.util.FileData;
import com.alathreon.alahexeditor.util.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class HexEditorController implements Initializable {
    private static Position fromTablePosition(TablePosition tablePosition) {
        if(tablePosition.getColumn() < 1 || tablePosition.getColumn() > 16) return null;
        return new Position(tablePosition.getRow(), tablePosition.getColumn()-1);
    }

    @FXML private TableView<ByteView> table;
    @FXML private Menu openRecentMenu;
    private Runnable onPromptOpen;
    private Consumer<Path> onRecentOpen;
    private Runnable onPromptSave;
    private Runnable onSave;
    private Consumer<Stream<Position>> onCut;
    private Consumer<Stream<Position>> onCopy;
    private Consumer<Position> onPaste;
    private Consumer<Stream<Position>> onDelete;
    private Consumer<Integer> onLengthIncremented;

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
    @FXML
    private void onUndo(ActionEvent actionEvent) {
        // TODO
    }
    @FXML
    private void onRedo(ActionEvent actionEvent) {
        // TODO
    }
    @FXML
    private void onCut(ActionEvent actionEvent) {
        this.onCut.accept(findSelected());
    }
    @FXML
    private void onCopy(ActionEvent actionEvent) {
        this.onCopy.accept(findSelected());
    }
    @FXML
    private void onPaste(ActionEvent actionEvent) {
        Position position = table.getSelectionModel().getSelectedCells()
                .stream()
                .findFirst()    // Empty if no selection
                .map(HexEditorController::fromTablePosition)    // Empty if invalid selection
                .orElse(new Position(0, 0));
        this.onPaste.accept(position);
    }
    @FXML
    private void onDelete(ActionEvent actionEvent) {
        this.onDelete.accept(findSelected());
    }
    @FXML
    private void onSelectAll(ActionEvent actionEvent) {
        table.getSelectionModel().selectAll();
    }
    @FXML
    private void onUnselectAll(ActionEvent actionEvent) {
        table.getSelectionModel().clearSelection();
    }

    public void setOnPromptOpen(Runnable onPromptOpen) {
        this.onPromptOpen = onPromptOpen;
    }
    public void setOnRecentOpen(Consumer<Path> onRecentOpen) {
        this.onRecentOpen = onRecentOpen;
    }
    public void setOnPromptSave(Runnable onPromptSave) {
        this.onPromptSave = onPromptSave;
    }
    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }
    public void setOnCut(Consumer<Stream<Position>> onCut) {
        this.onCut = onCut;
    }
    public void setOnCopy(Consumer<Stream<Position>> onCopy) {
        this.onCopy = onCopy;
    }
    public void setOnPaste(Consumer<Position> onPaste) {
        this.onPaste = onPaste;
    }
    public void setOnDelete(Consumer<Stream<Position>> onDelete) {
        this.onDelete = onDelete;
    }
    public void setOnLengthIncremented(Consumer<Integer> onLengthIncremented) {
        this.onLengthIncremented = onLengthIncremented;
    }

    public void setData(FileData fileData) {
        table.getItems().clear();
        if(fileData == null) return;
        ByteView byteView = fileData.data();
        for(int i = 0; i <= byteView.length() / 16; i++) {
            if(i == byteView.length() / 16 && byteView.length() % 16 == 0) break;
            table.getItems().add(byteView.subView(i * 16, Math.min(16, byteView.length() - i * 16)));
        }
        table.getItems().add(new ByteView(new byte[0]));    // Ghost row, to scroll past last row
    }
    public void setRecentlyOpened(List<Path> recentlyOpened) {
        openRecentMenu.getItems().clear();
        for (Path path : recentlyOpened) {
            MenuItem menuItem = new MenuItem(path.getFileName().toString());
            menuItem.setOnAction(e -> onRecentOpen.accept(path));
            openRecentMenu.getItems().add(menuItem);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setEditable(true);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(p -> true);
        table.setSortPolicy(byteViewTableView -> false);
        addColumn("    ", 4, v -> "%04X".formatted(v.length() != 0 ? v.offset() : table.getItems().size() * 16 - 16));
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
        String newValue = event.getNewValue().trim();
        String s = switch (newValue.length()) {
            case 0 -> "00";
            case 1 -> "0" + newValue;
            case 2 -> newValue;
            default -> newValue.substring(0, 2);
        };
        ByteView view = event.getRowValue();
        int toFit = view.neededToFit(col, s);
        if(view.length() == 0 && event.getTablePosition().getRow() > 0) {
            toFit += event.getTableView().getItems().get(event.getTablePosition().getRow()-1).neededToFit(15, "00");
        }
        if(toFit > 0) {
            onLengthIncremented.accept(toFit);
            view = event.getTableView().getItems().get(event.getTablePosition().getRow());
        }
        view.set(col, s);
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
    private Stream<Position> findSelected() {
        return table.getSelectionModel().getSelectedCells().stream().map(HexEditorController::fromTablePosition).filter(Objects::nonNull);
    }
}
