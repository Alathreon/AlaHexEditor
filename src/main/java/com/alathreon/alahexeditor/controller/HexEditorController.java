package com.alathreon.alahexeditor.controller;

import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.component.CustomTextFieldTableCell;
import com.alathreon.alahexeditor.util.FileData;
import com.alathreon.alahexeditor.util.Pair;
import com.alathreon.alahexeditor.util.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class HexEditorController implements Initializable {
    private static final int ROW_WIDTH = 16;
    private static final int START_COL_HEX = 1;
    private static final int END_COL_HEX = 16;
    private sealed interface TreeItemElement {
        record TreeItemRoot() implements TreeItemElement {}
        record TreeItemObject(String name, ParseObject object) implements TreeItemElement {}
        record TreeItemField(String name, String value) implements TreeItemElement {}
    }

    @FXML private TableView<ByteView> table;
    @FXML private Menu openRecentMenu;
    @FXML private Menu openRecentTemplateMenu;
    @FXML private TreeView<TreeItemElement> treeView;

    private Runnable onNew;
    private Runnable onPromptOpen;
    private Runnable onPromptOpenTemplate;
    private Consumer<Path> onRecentOpen;
    private Consumer<Path> onRecentOpenTemplate;
    private Runnable onPromptSave;
    private Runnable onSave;
    private Consumer<Stream<Position>> onCut;
    private Consumer<Stream<Position>> onCopy;
    private Consumer<Position> onPaste;
    private Consumer<Stream<Position>> onDelete;
    private Runnable onQuit;

    private Consumer<Integer> onLengthIncremented;

    private ByteView bytes;

    @FXML
    private void onNew(ActionEvent actionEvent) {
        this.onNew.run();
    }
    @FXML
    private void onPromptOpen(ActionEvent event) {
        this.onPromptOpen.run();
    }
    @FXML
    private void onPromptOpenTemplate(ActionEvent event) {
        this.onPromptOpenTemplate.run();
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
        this.onCut.accept(findSelectedBytes());
    }
    @FXML
    private void onCopy(ActionEvent actionEvent) {
        this.onCopy.accept(findSelectedBytes());
    }
    @FXML
    private void onPaste(ActionEvent actionEvent) {
        Position position = table.getSelectionModel().getSelectedCells()
                .stream()
                .findFirst()    // Empty if no selection
                .map(this::fromByteTablePosition)    // Empty if invalid selection
                .orElse(new Position(0, 0));
        this.onPaste.accept(position);
    }
    @FXML
    private void onDelete(ActionEvent actionEvent) {
        this.onDelete.accept(findSelectedBytes());
    }
    @FXML
    private void onSelectAll(ActionEvent actionEvent) {
        table.getSelectionModel().selectAll();
    }
    @FXML
    private void onUnselectAll(ActionEvent actionEvent) {
        table.getSelectionModel().clearSelection();
    }
    @FXML
    private void onQuit(ActionEvent actionEvent) {
        this.onQuit.run();
    }

    public void setOnNew(Runnable onNew) {
        this.onNew = onNew;
    }
    public void setOnPromptOpen(Runnable onPromptOpen) {
        this.onPromptOpen = onPromptOpen;
    }
    public void setOnPromptOpenTemplate(Runnable onPromptOpenTemplate) {
        this.onPromptOpenTemplate = onPromptOpenTemplate;
    }
    public void setOnRecentOpen(Consumer<Path> onRecentOpen) {
        this.onRecentOpen = onRecentOpen;
    }
    public void setOnRecentOpenTemplate(Consumer<Path> onRecentOpenTemplate) {
        this.onRecentOpenTemplate = onRecentOpenTemplate;
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
    public void setOnQuit(Runnable onQuit) {
        this.onQuit = onQuit;
    }

    public void fireEvent(EventKind eventKind) {
        switch (eventKind) {
            case NEW -> onNew(null);
            case PROMPT_OPEN -> onPromptOpen(null);
            case PROMPT_OPEN_TEMPLATE -> onPromptOpenTemplate(null);
            case PROMPT_SAVE -> onPromptSave(null);
            case SAVE -> onSave(null);
            case CUT -> onCut(null);
            case COPY -> onCopy(null);
            case PASTE -> onPaste(null);
            case DELETE -> onDelete(null);
            case SELECT_ALL -> onSelectAll(null);
            case UNSELECT_ALL -> onUnselectAll(null);
            case QUIT -> onQuit(null);
            case null -> throw new NullPointerException();
        }
    }

    public void setOnLengthIncremented(Consumer<Integer> onLengthIncremented) {
        this.onLengthIncremented = onLengthIncremented;
    }

    public void setData(FileData fileData) {
        table.getItems().clear();
        if(fileData == null) return;
        this.bytes = fileData.data();
        ByteView byteView = fileData.data();
        for(int i = 0; i <= byteView.length() / ROW_WIDTH; i++) {
            if(i == byteView.length() / ROW_WIDTH && byteView.length() % ROW_WIDTH == 0) break;
            table.getItems().add(byteView.subView(i * ROW_WIDTH, Math.min(ROW_WIDTH, byteView.length() - i * ROW_WIDTH)));
        }
        table.getItems().add(new ByteView(new byte[0]));    // Ghost row, to scroll past last row
    }
    public void setParsedData(List<Pair<String, ParseObject>> objects) {
        treeView.getRoot().getChildren().clear();
        treeView.getRoot().setValue(new TreeItemElement.TreeItemRoot());
        loadParsed(treeView.getRoot(), objects);
        treeView.getRoot().setExpanded(true);
        if(treeView.getRoot().getChildren().size() == 1) {
            treeView.getRoot().getChildren().getFirst().setExpanded(true);
        }
    }
    public void setRecentlyOpened(List<Path> recentlyOpened) {
        openRecentMenu.getItems().clear();
        for (Path path : recentlyOpened) {
            MenuItem menuItem = new MenuItem(path.getFileName().toString());
            menuItem.setOnAction(e -> onRecentOpen.accept(path));
            openRecentMenu.getItems().add(menuItem);
        }
    }
    public void setRecentlyOpenedTemplates(List<Path> recentlyOpened) {
        openRecentTemplateMenu.getItems().clear();
        for (Path path : recentlyOpened) {
            MenuItem menuItem = new MenuItem(path.getFileName().toString());
            menuItem.setOnAction(e -> onRecentOpenTemplate.accept(path));
            openRecentTemplateMenu.getItems().add(menuItem);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setEditable(true);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(p -> true);
        table.setSortPolicy(byteViewTableView -> false);
        addColumn("    ", 4, v -> "%04X".formatted(v.length() != 0 ? v.offset() : table.getItems().size() * ROW_WIDTH - ROW_WIDTH));
        for(int i = 0; i < ROW_WIDTH; i++) {
            int j = i;
            TableColumn<ByteView, String> column = addColumn("%X".formatted(i), 2, v -> v.length() <= j ? "  " : v.subView(j, 1).toString());
            column.setCellFactory(tableColumn -> actionSetCellFactory(j));
            column.setOnEditCommit(event -> actionSetOnEditCommit(j, event));
        }
        addColumn("0123456789ABCDEF", ROW_WIDTH, ByteView::toUTF8String);
        table.addEventFilter(ScrollEvent.SCROLL, e -> {
            TablePosition<ByteView, ?> editingCell = table.getEditingCell();
            if(editingCell != null) {
                int delta = e.getDeltaY() > 0 ? 1 : -1;
                if(e.isShortcutDown()) {
                    delta *= 16;
                }
                int col = editingCell.getColumn() - START_COL_HEX;
                ByteView view = table.getItems().get(editingCell.getRow());
                view.set(col, (byte) ((view.get(col) + delta) % 256));
                e.consume();
                table.refresh();
            }
        });
        treeView.setRoot(new TreeItem<>());
        treeView.setFixedCellSize(Region.USE_COMPUTED_SIZE);treeView.setFixedCellSize(-1);
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(TreeItemElement item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(null);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String s = switch (item) {
                        case TreeItemElement.TreeItemObject(String name, ParseObject object) ->
                                String.format("%s (offset: 0x%X, size: %d bytes)",
                                        name, object.metadata().offset(), object.metadata().length());
                        case TreeItemElement.TreeItemField(String name, String value) ->
                                String.format("%s: %s", name, value);
                        case TreeItemElement.TreeItemRoot _ -> "Data";
                    };
                    setText(s);
                }
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                TreeItemElement value = newSelection.getValue();
                if(value instanceof TreeItemElement.TreeItemObject(_, ParseObject object)) {
                    int offset = object.metadata().offset();
                    int offEnd = offset + object.metadata().length();
                    int rowStartIdx = offset / ROW_WIDTH;
                    int colStartIdx = offset % ROW_WIDTH;
                    int rowEndIdx = offEnd / ROW_WIDTH;
                    int colEndIdx = offEnd % ROW_WIDTH;
                    TableColumn<ByteView, ?> colStart = table.getColumns().get(colStartIdx + START_COL_HEX);
                    TableColumn<ByteView, ?> colEnd = table.getColumns().get(colEndIdx);
                    TableColumn<ByteView, ?> colFirst = table.getColumns().get(START_COL_HEX);
                    TableColumn<ByteView, ?> colLast = table.getColumns().get(END_COL_HEX);
                    table.getSelectionModel().clearSelection();
                    if(rowStartIdx == rowEndIdx) {
                        table.getSelectionModel().selectRange(rowStartIdx, colStart, rowStartIdx, colEnd);
                    } else {
                        table.getSelectionModel().selectRange(rowStartIdx, colStart, rowStartIdx, colLast);
                        if(rowEndIdx - rowStartIdx >= 2) {
                            table.getSelectionModel().selectRange(rowStartIdx+1, colFirst, rowEndIdx-1, colLast);
                        }
                        table.getSelectionModel().selectRange(rowEndIdx, colFirst, rowEndIdx, colEnd);
                    }
                }
            }
        });
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
            toFit += event.getTableView().getItems().get(event.getTablePosition().getRow()-1).neededToFit(ROW_WIDTH-1, "00");
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
    private Stream<Position> findSelectedBytes() {
        return table.getSelectionModel().getSelectedCells().stream().map(this::fromValidByteTablePosition).filter(Objects::nonNull);
    }
    private Position fromValidByteTablePosition(TablePosition tablePosition) {
        Position position = fromByteTablePosition(tablePosition);
        if(position != null && !bytes.isIn(position)) {
            position = null;
        }
        return position;
    }
    private Position fromByteTablePosition(TablePosition tablePosition) {
        if(tablePosition.getColumn() < START_COL_HEX || tablePosition.getColumn() > END_COL_HEX) return null;
        return new Position(tablePosition.getRow(), tablePosition.getColumn() - START_COL_HEX);
    }
    private void loadParsed(TreeItem<TreeItemElement> root, List<Pair<String, ParseObject>> objects) {
        for(Pair<String, ParseObject> entry : objects) {
            TreeItem<TreeItemElement> item = new TreeItem<>(new TreeItemElement.TreeItemObject(entry.key(), entry.value()));
            root.getChildren().add(item);
            for (Pair<String, String> displayField : entry.value().data().displayFields()) {
                item.getChildren().add(new TreeItem<>(new TreeItemElement.TreeItemField(displayField.key(), displayField.value())));
            }
            List<Pair<String, ParseObject>> children = entry.value().data().children();
            if(!children.isEmpty()) {
                loadParsed(item, children);
            }
        }
    }
}
