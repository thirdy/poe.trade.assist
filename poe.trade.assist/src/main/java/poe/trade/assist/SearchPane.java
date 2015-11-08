/*
 * Copyright (C) 2015 thirdy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package poe.trade.assist;

import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

public class SearchPane extends VBox {

	private ListProperty<Search> data;
	public ListProperty<Search> dataProperty(){return data;}

	TableColumn<Search, String> nameCol = new TableColumn<>("Name");
	TableColumn<Search, String> urlCol = new TableColumn<>("URL");

	final TextField addName = new TextField();
	final TextField addURL = new TextField();
	final Button addButton = new Button("Add");
	final Button remButton = new Button("Rem");
	TableView<Search> table = new TableView<>();

	public SearchPane(List<Search> searchList) {
		data = new SimpleListProperty<>(
				FXCollections.observableArrayList(searchList));
		
		table.setEditable(true);
		table.setItems(data);
		table.setMaxWidth(500);

		setupColumns();
		setupAddFields();

		addButton.setOnAction((ActionEvent e) -> {
			data.add(new Search(addName.getText(), addURL.getText()));
			addName.clear();
			addURL.clear();
		});
		
		remButton.setOnAction((ActionEvent e) -> {
			int index = table.getSelectionModel().getSelectedIndex();
			if (index != -1) {
				table.getItems().remove(index);
			}
		});

		final HBox hb = new HBox();
		hb.getChildren().addAll(addName, addURL, addButton, remButton);
		hb.setSpacing(3);

		final Label label = new Label("Search List");
		label.setFont(Font.font("Arial", FontWeight.BOLD, 14));

		setSpacing(5);
		setPadding(new Insets(10, 0, 0, 10));
		getChildren().addAll(label, table, hb);

	}

	private void setupAddFields() {
		addName.setPromptText("Name");
		addName.setMinWidth(100);
		addURL.setPromptText("URL");
		addURL.setMinWidth(250);
	}

	@SuppressWarnings("unchecked")
	private void setupColumns() {
		Callback<TableColumn<Search, String>, TableCell<Search, String>> cellFactory = (
				TableColumn<Search, String> p) -> new EditingCell();

		nameCol.setMinWidth(190);
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setCellFactory(cellFactory);
		nameCol.setOnEditCommit((CellEditEvent<Search, String> t) -> {
			((Search) t.getTableView().getItems().get(t.getTablePosition().getRow())).name.set(t.getNewValue());
		});

		urlCol.setMinWidth(300);
		urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
		urlCol.setCellFactory(cellFactory);
		urlCol.setOnEditCommit((CellEditEvent<Search, String> t) -> {
			((Search) t.getTableView().getItems().get(t.getTablePosition().getRow())).url.set(t.getNewValue());
		});

		table.getColumns().addAll(nameCol, urlCol);
	}

	private static class EditingCell extends TableCell<Search, String> {

		private TextField textField;

		public EditingCell() {
			addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() > 1) {
                        System.out.println("double clicked!");
                        TableCell c = (TableCell) event.getSource();
                        if (!isEmpty()) {
                        	EditingCell.super.startEdit();
            				createTextField();
            				setText(null);
            				setGraphic(textField);
            				textField.selectAll();
            			}
                        System.out.println("Cell text: " + c.getText());
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
}
