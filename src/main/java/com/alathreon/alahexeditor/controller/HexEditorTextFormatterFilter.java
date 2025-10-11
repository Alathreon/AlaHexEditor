package com.alathreon.alahexeditor.controller;

import com.alathreon.alahexeditor.component.CustomTextFieldTableCell;
import com.alathreon.alahexeditor.util.ByteView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.function.UnaryOperator;

public record HexEditorTextFormatterFilter(int col, TableView<ByteView> table, CustomTextFieldTableCell cell) implements UnaryOperator<TextFormatter.Change> {
    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        if(change.getControlNewText().length() > 2) {
            int nextCol = (col + 1) % 16;
            int row = nextCol == 0 ? cell.getIndex() + 1 : cell.getIndex();
            if(row < table.getItems().size()) {
                cell.commitEdit(change.getControlNewText().toUpperCase());
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> {
                    TablePosition<ByteView, ?> position = new TablePosition<>(table, row, table.getColumns().get(nextCol + 1));
                    table.getFocusModel().focus(position);
                    table.getSelectionModel().clearAndSelect(position.getRow(), position.getTableColumn());
                    table.scrollTo(row);
                    table.edit(row, table.getColumns().get(nextCol + 1));
                    Platform.runLater(() -> {
                        CustomTextFieldTableCell cellAt = getCellAt(position);
                        if(cellAt == null) return;
                        TextField field = cellAt.textField();
                        if (field == null) return;
                        field.setText(change.getText().toUpperCase());
                        field.positionCaret(1);
                    });
                }));
                timeline.setDelay(Duration.millis(10));
                timeline.play();
            }
            return null;
        }
        if(change.getControlNewText().matches("[0-9a-fA-F]*| {2}")) {
            change.setText(change.getText().toUpperCase());
            return change;
        }
        return null;
    }
    private CustomTextFieldTableCell getCellAt(TablePosition position) {
        for (Node node : position.getTableView().lookupAll(".table-row-cell")) {
            if (node instanceof TableRow tableRow) {
                if (tableRow.getIndex() == position.getRow()) {
                    for (Node cell : tableRow.lookupAll(".table-cell")) {
                        if (cell instanceof CustomTextFieldTableCell tableCell) {
                            if (tableCell.getTableColumn() == position.getTableColumn()) {
                                return tableCell;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
