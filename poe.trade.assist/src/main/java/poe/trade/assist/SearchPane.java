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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import poe.trade.assist.fx.TableViewPlus;
import poe.trade.assist.util.SwingUtil;

public class SearchPane extends VBox {

	private ListProperty<Search> data;
	public ListProperty<Search> dataProperty(){return data;}

//	TableColumn<Search, String> nameCol = new TableColumn<>("Name");
//	TableColumn<Search, String> urlCol = new TableColumn<>("URL");
//	TableColumn<Search, Boolean> autoSearchCol = new TableColumn<>("Auto");
//	TableColumn<Search, String> resultCol = new TableColumn<>("Result");

	final TextField addName = new TextField();
	final TextField addTags = new TextField();
	final TextField addURL = new TextField();
	final CheckBox addAuto = new CheckBox("Auto");
	final Button addButton = new Button("Add");
	final Button remButton = new Button("Rem");
//	TableView<Search> table = new TableView<>();
	TableViewPlus<Search> searchTable;
	TextField tagFilterField;
	TextField nameFilterField;
	
	Hyperlink website = new Hyperlink("http://thirdy.github.io/poe.trade.assist/");
	Label info = new Label("poe.trade.assist is fan made tool and is not affiliated with Grinding Gear Games in any way. " + System.lineSeparator() + "This software 100% free and open source under GPLv2 license.");

	public SearchPane(List<Search> searchList) {
		data = new SimpleListProperty<>(
				FXCollections.observableArrayList(searchList));
		website.setOnAction(e -> SwingUtil.openUrlViaBrowser(website.getText()));
		setupSearchTable();
		setupFilterTextField();
		info.setWrapText(true);
		
//		setupColumns();
		setupAddFields();
		setupTableClickListener();

		addButton.setOnAction((ActionEvent e) -> {
			data.add(new Search(addName.getText(), addTags.getText(), addURL.getText(), addAuto.isSelected(), "price_in_chaos"));
			addName.clear();
			addTags.clear();
			addURL.clear();
			addAuto.setSelected(false);
		});
		
		remButton.setOnAction((ActionEvent e) -> {
			int index = searchTable.getSelectionModel().getSelectedIndex();
//			if (index != -1) {
//				searchTable.getItems().remove(index);
//			}
			searchTable.remove(index);
		});

		final HBox hb = new HBox(3);
		hb.getChildren().addAll(addAuto, addButton, remButton);
		
		final VBox vb = new VBox(3, addName, addTags, addURL, hb);

		https://docs.google.com/spreadsheets/d/1SWEQbdQxdahrIIQrs8RjpAw2cm_QOc53KDyezS80mwM/pub?gid=0&single=true&output=csv
		setSpacing(5);
		setPadding(new Insets(10, 0, 0, 10));
		getChildren().addAll(tagFilterField, nameFilterField, searchTable, vb, info,
				new TextFlow(new Text("Check for the latest updates at: "), website)
				);
		VBox.setVgrow(searchTable, Priority.ALWAYS);
		setMaxWidth(Double.MAX_VALUE);
	}
	
	private void setupFilterTextField() {
		nameFilterField = new TextField();
		nameFilterField.setMinWidth(200);
		nameFilterField.setPromptText("filter by name");
		
		tagFilterField = new TextField();
		tagFilterField.setMinWidth(200);
		tagFilterField.setPromptText("filter by tags, csv");
		
		tagFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
			searchTable.getFilteredData().setPredicate(tagPredicate().and(namePredicate()));
		});
		
		nameFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
			searchTable.getFilteredData().setPredicate(tagPredicate().and(namePredicate()));
		});
	}
	
	private Predicate<Search> tagPredicate() {
		Predicate<Search> tagPredicate = search -> {
			String filter = tagFilterField.getText();
			if (isBlank(filter)) {
				return true;
			}
			filter = filter.toLowerCase();
			List<String> filterTags = Arrays.asList(split(filter, ','));
			List<String> itemTags = Arrays.asList(split(trimToEmpty(search.getTags()).toLowerCase(), ','));
			System.out.println(itemTags);
			boolean found = itemTags.stream().anyMatch(itemTag -> filterTags.contains(itemTag));
			System.out.println(found);
			return found;
		};
		return tagPredicate;
	}
	
	private Predicate<Search> namePredicate() {
		Predicate<Search> tagPredicate = search -> {
			String filter = nameFilterField.getText();
			if (isBlank(filter)) {
				return true;
			}
			filter = filter.toLowerCase();
			String name = StringUtils.trimToEmpty(search.getName()).toLowerCase();
			boolean found = name.contains(filter);
			return found;
		};
		return tagPredicate;
	}
	
	private void setupSearchTable() {
		searchTable = new TableViewPlus<>(ImmutableMap.of(
				"Name", "name",
				"Result", "result",
				"Auto", "autoSearch",
				"Tags", "tags",
				"URL", "url"
				),
				new double[] {130, 40, 40, 150, 200},
				data);
		
		searchTable.setEditable(false);
		searchTable.setPrefWidth(500);
		searchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
//		modMappingTable.setMaxHeight(Double.MAX_VALUE);
//		modMappingTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//		modMappingTable.setId("modMappingTable");
	}

	private void setupTableClickListener() {
		searchTable.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    Search search = searchTable.getSelectionModel().getSelectedItem();
                    if (search != null) {
						SwingUtil.openUrlViaBrowser(search.getUrl());
					}
                }
            }
        });
	}

	private void setupAddFields() {
		addName.setPromptText("Name");
		addTags.setPromptText("Tags, comma separated");
//		addName.setMinWidth(100);
		addURL.setPromptText("URL");
		addURL.setMinWidth(200);
	}

//	@SuppressWarnings("unchecked")
//	private void setupColumns() {
////		Callback<TableColumn<Search, String>, TableCell<Search, String>> cellTextFieldFactory = (
////				TableColumn<Search, String> p) -> new EditingTextFieldCell();
////		Callback<TableColumn<Search, Boolean>, TableCell<Search, Boolean>> cellCheckboxFactory = (
////						TableColumn<Search, Boolean> p) -> new EditingCheckboxCell();
//
//		nameCol.setMinWidth(150);
//		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
////		nameCol.setCellFactory(cellTextFieldFactory);
////		nameCol.setOnEditCommit((CellEditEvent<Search, String> t) -> {
////			((Search) t.getTableView().getItems().get(t.getTablePosition().getRow())).name.set(t.getNewValue());
////		});
//
//		urlCol.setMinWidth(200);
//		urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
////		urlCol.setCellFactory(cellTextFieldFactory);
////		urlCol.setOnEditCommit((CellEditEvent<Search, String> t) -> {
////			((Search) t.getTableView().getItems().get(t.getTablePosition().getRow())).url.set(t.getNewValue());
////		});
//		
//		autoSearchCol.setMinWidth(40);
//		autoSearchCol.setCellValueFactory(new PropertyValueFactory<>("autoSearch"));
////		autoSearchCol.setCellFactory(cellCheckboxFactory);
////		autoSearchCol.setOnEditCommit((CellEditEvent<Search, Boolean> t) -> {
////			((Search) t.getTableView().getItems().get(t.getTablePosition().getRow())).autoSearch.set(t.getNewValue());
////		});
//		
//		resultCol.setMinWidth(40);
//		resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));
//
//		table.getColumns().addAll(nameCol, autoSearchCol, resultCol, urlCol);
//	}

//	public TableColumn getResultColumn() {
//		return resultCol;
//	}
}
