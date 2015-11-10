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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import poe.trade.assist.util.SwingUtil;

public class SearchPane extends VBox {

	private ListProperty<Search> data;
	public ListProperty<Search> dataProperty(){return data;}

	TableColumn<Search, String> nameCol = new TableColumn<>("Name");
	TableColumn<Search, String> urlCol = new TableColumn<>("URL");
	TableColumn<Search, Boolean> autoSearchCol = new TableColumn<>("Auto");
	TableColumn<Search, String> resultCol = new TableColumn<>("Result");

	final TextField addName = new TextField();
	final TextField addURL = new TextField();
	final CheckBox addAuto = new CheckBox("Auto");
	final Button addButton = new Button("Add");
	final Button remButton = new Button("Rem");
	TableView<Search> table = new TableView<>();
	
	Label info = new Label("poe.trade.assist is fan made tool and is not affiliated with Grinding Gear Games in any way. " + System.lineSeparator() + "This software 100% free and open source under GPLv2 license.");

	public SearchPane(List<Search> searchList) {
		data = new SimpleListProperty<>(
				FXCollections.observableArrayList(searchList));
		
		table.setEditable(false);
		table.setItems(data);
		table.setPrefWidth(500);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		info.setWrapText(true);

		setupColumns();
		setupAddFields();
		setupTableClickListener();

		addButton.setOnAction((ActionEvent e) -> {
			data.add(new Search(addName.getText(), addURL.getText(), addAuto.isSelected()));
			addName.clear();
			addURL.clear();
			addAuto.setSelected(false);
		});
		
		remButton.setOnAction((ActionEvent e) -> {
			int index = table.getSelectionModel().getSelectedIndex();
			if (index != -1) {
				table.getItems().remove(index);
			}
		});

		final HBox hb = new HBox();
		HBox.setHgrow(addName, Priority.ALWAYS);
		hb.getChildren().addAll(addName, addURL, addAuto, addButton, remButton);
		hb.setSpacing(3);

		final Label label = new Label("Search List");
		label.setFont(Font.font("Arial", FontWeight.BOLD, 14));

		setSpacing(5);
		setPadding(new Insets(10, 0, 0, 10));
		getChildren().addAll(label, table, hb, info);
		VBox.setVgrow(table, Priority.ALWAYS);
		setMaxWidth(Double.MAX_VALUE);
	}

	private void setupTableClickListener() {
		table.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    Search search = table.getSelectionModel().getSelectedItem();
                    if (search != null) {
						SwingUtil.openUrlViaBrowser(search.getUrl());
					}
                }
            }
        });
	}

	private void setupAddFields() {
		addName.setPromptText("Name");
//		addName.setMinWidth(100);
		addURL.setPromptText("URL");
		addURL.setMinWidth(200);
	}

	@SuppressWarnings("unchecked")
	private void setupColumns() {
//		Callback<TableColumn<Search, String>, TableCell<Search, String>> cellTextFieldFactory = (
//				TableColumn<Search, String> p) -> new EditingTextFieldCell();
//		Callback<TableColumn<Search, Boolean>, TableCell<Search, Boolean>> cellCheckboxFactory = (
//						TableColumn<Search, Boolean> p) -> new EditingCheckboxCell();

		nameCol.setMinWidth(180);
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
//		nameCol.setCellFactory(cellTextFieldFactory);
//		nameCol.setOnEditCommit((CellEditEvent<Search, String> t) -> {
//			((Search) t.getTableView().getItems().get(t.getTablePosition().getRow())).name.set(t.getNewValue());
//		});

		urlCol.setMinWidth(210);
		urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
//		urlCol.setCellFactory(cellTextFieldFactory);
//		urlCol.setOnEditCommit((CellEditEvent<Search, String> t) -> {
//			((Search) t.getTableView().getItems().get(t.getTablePosition().getRow())).url.set(t.getNewValue());
//		});
		
		autoSearchCol.setMinWidth(35);
		autoSearchCol.setCellValueFactory(new PropertyValueFactory<>("autoSearch"));
//		autoSearchCol.setCellFactory(cellCheckboxFactory);
//		autoSearchCol.setOnEditCommit((CellEditEvent<Search, Boolean> t) -> {
//			((Search) t.getTableView().getItems().get(t.getTablePosition().getRow())).autoSearch.set(t.getNewValue());
//		});
		
		resultCol.setMinWidth(35);
		resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));

		table.getColumns().addAll(nameCol, urlCol, autoSearchCol, resultCol);
	}

	public TableColumn getResultColumn() {
		return resultCol;
	}
}
