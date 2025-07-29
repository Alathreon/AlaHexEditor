package com.alathreon.alahexeditor.component;

import com.alathreon.alahexeditor.util.ByteView;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.util.function.Consumer;

/**
 * Copied from {@link TextFieldTableCell}.
 */
public class CustomTextFieldTableCell extends TableCell<ByteView,String> {
    private TextField textField;
    private final StringConverter<String> converter;
    private Consumer<TextField> onTextFieldConsumer;

    public CustomTextFieldTableCell() {
        this.getStyleClass().add("text-field-table-cell");
        converter = new DefaultStringConverter();
    }

    //TODO nullable
    public TextField textField() {
        return textField;
    }

    public void setOnTextFieldCreated(Consumer<TextField> onTextFieldConsumer) {
        this.onTextFieldConsumer = onTextFieldConsumer;
    }

    public StringConverter<String> getConverter() {
        return converter;
    }
    @Override public void startEdit() {
        super.startEdit();
        if (!isEditing()) {
            return;
        }

        if (textField == null) {
            textField = createTextField(this, getConverter());
            onTextFieldConsumer.accept(textField);
        }

        startEdit(this, getConverter(), null, null, textField);
    }

    /** {@inheritDoc} */
    @Override public void cancelEdit() {
        super.cancelEdit();
        cancelEdit(this, getConverter(), null);
    }

    /** {@inheritDoc} */
    @Override public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        updateItem(this, getConverter(), null, null, textField);
    }
    static <T> TextField createTextField(final Cell<T> cell, final StringConverter<T> converter) {
        final TextField textField = new TextField(getItemText(cell, converter));

        // Use onAction here rather than onKeyReleased (with check for Enter),
        // as otherwise we encounter RT-34685
        textField.setOnAction(event -> {
            if (converter == null) {
                throw new IllegalStateException(
                        "Attempting to convert text input into Object, but provided "
                                + "StringConverter is null. Be sure to set a StringConverter "
                                + "in your cell factory.");
            }
            cell.commitEdit(converter.fromString(textField.getText()));
            event.consume();
        });
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                cell.cancelEdit();
                t.consume();
            }
        });
        return textField;
    }

    static <T> void startEdit(final Cell<T> cell,
                              final StringConverter<T> converter,
                              final HBox hbox,
                              final Node graphic,
                              final TextField textField) {
        if (textField != null) {
            textField.setText(getItemText(cell, converter));
        }
        cell.setText(null);

        if (graphic != null) {
            hbox.getChildren().setAll(graphic, textField);
            cell.setGraphic(hbox);
        } else {
            cell.setGraphic(textField);
        }

        textField.selectAll();

        // requesting focus so that key input can immediately go into the
        // TextField (see RT-28132)
        textField.requestFocus();
    }
    static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter, Node graphic) {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(graphic);
    }
    static <T> void updateItem(final Cell<T> cell,
                               final StringConverter<T> converter,
                               final HBox hbox,
                               final Node graphic,
                               final TextField textField) {
        if (cell.isEmpty()) {
            cell.setText(null);
            cell.setGraphic(null);
        } else {
            if (cell.isEditing()) {
                if (textField != null) {
                    textField.setText(getItemText(cell, converter));
                }
                cell.setText(null);

                if (graphic != null) {
                    hbox.getChildren().setAll(graphic, textField);
                    cell.setGraphic(hbox);
                } else {
                    cell.setGraphic(textField);
                }
            } else {
                cell.setText(getItemText(cell, converter));
                cell.setGraphic(graphic);
            }
        }
    }
    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        return converter == null ?
                cell.getItem() == null ? "" : cell.getItem().toString() :
                converter.toString(cell.getItem());
    }
}
