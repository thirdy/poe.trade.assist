package poe.trade.assist.fx;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import poe.trade.assist.Search;

// man this code is simple just too long
public class EditingCheckboxCell extends TableCell<Search, Boolean> {

	private CheckBox checkbox;

	public EditingCheckboxCell() {
		addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    TableCell c = (TableCell) event.getSource();
                    if (!isEmpty()) {
                    	EditingCheckboxCell.super.startEdit();
        				createCheckbox();
        				setText(null);
        				setGraphic(checkbox);
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
		setText(getItem().toString());
		setGraphic(null);
	}

	@Override
	public void updateItem(Boolean item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (checkbox != null) {
					checkbox.setSelected(item);
				}
				setText(null);
				setGraphic(checkbox);
			} else {
				setText(item.toString());
				setGraphic(null);
			}
		}
	}

	private void createCheckbox() {
		checkbox = new CheckBox();
		checkbox.setSelected(getItem());
		checkbox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		checkbox.focusedProperty()
				.addListener((ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) -> {
					if (!arg2) {
						commitEdit(checkbox.isSelected());
					}
				});
	}

}