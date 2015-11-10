package poe.trade.assist.fx;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import poe.trade.assist.Search;

// man this code is simple just too long
public class EditingTextFieldCell extends TableCell<Search, String> {

	private TextField textField;

	public EditingTextFieldCell() {
		addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    TableCell c = (TableCell) event.getSource();
                    if (!isEmpty()) {
                    	EditingTextFieldCell.super.startEdit();
        				createTextField();
        				setText(null);
        				setGraphic(textField);
        				textField.selectAll();
        			}
                }
            }
        });
	}

	@Override
	public void startEdit() {
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText((String) getItem());
		setGraphic(null);
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setText(null);
				setGraphic(textField);
			} else {
				setText(getString());
				setGraphic(null);
			}
		}
	}

	private void createTextField() {
		textField = new TextField(getString());
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		textField.focusedProperty()
				.addListener((ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) -> {
					if (!arg2) {
						commitEdit(textField.getText());
					}
				});
	}

	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}
}