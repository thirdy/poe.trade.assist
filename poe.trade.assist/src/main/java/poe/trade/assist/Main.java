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
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import poe.trade.assist.Search.SearchPersist;
import poe.trade.assist.scraper.SearchPageScraper.SearchResultItem;
import poe.trade.assist.service.AutoSearchService;
import poe.trade.assist.util.Dialogs;
 
public class Main extends Application {
    
    private static final String LOCAL_SEARCH_FILE_NAME = "search.csv";
	private SearchPane searchPane;
	private ResultPane resultPane;
	final TextField searchFileTextField = new TextField("https://docs.google.com/spreadsheets/d/1V8r0mIn5njpmVYwFWpqnptAMI6udrIaqhCby1i79UGw/edit?usp=sharing");
	public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) {
    	BorderPane root = new BorderPane();
    	
    	searchFileTextField.setPromptText("Search CSV File URL or blank");
		searchFileTextField.setTooltip(new Tooltip("Any url to a valid poe.trade.assist CSV search file. Can be googlespreadsheet URL. If left blank, will load search.csv file instead"));
		searchFileTextField.setMinWidth(330);
		HBox.setHgrow(searchFileTextField, Priority.ALWAYS);
    	
    	List<Search> searchList = loadSearchListFromFile();
 
    	AutoSearchService autoSearchService = new AutoSearchService();
        searchPane = new SearchPane(searchList);
        resultPane = new ResultPane(searchFileTextField, this);
        
        autoSearchService.searchesProperty().bind(searchPane.dataProperty());
        
        EventHandler<ActionEvent> reloadAction = e -> {
        	System.out.println("Loading search file: " + searchFileTextField.getText());
        	List<Search> newList = loadSearchListFromFile();
        	searchPane.dataProperty().clear();
        	searchPane.dataProperty().addAll(newList);
        };
        
		searchFileTextField.setOnAction(reloadAction);
        resultPane.reloadButton.setOnAction(reloadAction);
        
        resultPane.runNowButton.setOnAction(e -> autoSearchService.restart() );
//        autoSearchService.minsToSleepProperty().bind(resultPane.noOfMinsTextField.textProperty());
        setupResultPaneBinding(searchPane, resultPane, autoSearchService);
        if(searchList.size() > 0) searchPane.searchTable.getSelectionModel().select(0);
        
        stage.setOnCloseRequest(we -> saveSearchList(searchPane));
        
        autoSearchService.restart();
        
//        HBox container = new HBox(5, searchPane, resultPane);
        SplitPane container = new SplitPane(searchPane, resultPane);
        container.setDividerPositions(0.1);
        HBox.setHgrow(searchPane, Priority.ALWAYS);
        HBox.setHgrow(resultPane, Priority.ALWAYS);
        container.setMaxWidth(Double.MAX_VALUE);
//        root.getChildren().addAll(container);
        root.setCenter(container);
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image("/48px-Durian.png"));
        stage.titleProperty().bind(
        		new SimpleStringProperty("poe.trade.assist v5 (Durian) - ")
        			.concat(autoSearchService.messageProperty()) );
//        stage.setWidth(1200);
//        stage.setHeight(550);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        searchPane.searchTable.requestFocus();
    }

	private void saveSearchList(SearchPane searchPane) {
		List<Search> list = new ArrayList<>(searchPane.searchTable.getItems());
		if (list != null) {
//			Gson gson = new Gson();
			List<String[]> persistList 
				= list.stream()
					.map(e -> e.toSearchPersist())
					.map(e -> e.toCSVArray())
					.collect(toList());
			if (!persistList.isEmpty()) {
				File file = getSearchFile();
				try (BufferedWriter br =
		                   new BufferedWriter(new FileWriter(file))) {
					CSVPrinter json = CSVFormat.RFC4180
							.withHeader("Name","Tags","URL","Auto","Sort")
							.print(br);
					json.printRecords(persistList);
					json.flush();				
					//			String json = gson.toJson(persistList);
			    } catch (IOException e1) {
					// TODO Auto-generated catch block
			    	Dialogs.showError(e1);
					e1.printStackTrace();
				}
			}
			
//			saveSearchesToFile(json);
		}
	}

	private List<Search> loadSearchListFromFile() {
		List<Search> list = Arrays.asList(
				new Search("Lakishu's Blade 3L", "http://poe.trade/search/nokagatasasaha", "uniques,melee", false, "price_in_chaos"),
				new Search("Tabula 5c", "http://poe.trade/search/adeyubetedamit", "watch,uniques", true, "price_in_chaos")
		);
		// 1ex aegis http://poe.trade/search/atamiomimetami
		// kaom's heart http://poe.trade/search/kuwahamigaruri
		
		List<CSVRecord> csv = loadCSVRaw();
		
		list = csv.stream()
				.map(e -> new SearchPersist(
						e.get("Name"),
						e.get("Tags"),
						e.get("URL"),
						Boolean.valueOf(e.get("Auto")),
						e.get("Sort")
						).toSearch())
				.collect(toList());
			
//		if (StringUtils.isNotBlank(csv)) {
//			Gson gson = new Gson();
//			Type listType = new TypeToken<ArrayList<SearchPersist>>() { }.getType();
//			List<SearchPersist> persistList = gson.fromJson(csv, listType);
//			list = persistList.stream().map(e -> e.toSearch()).collect(toList());
//		}
		
		return list;
	}

	private List<CSVRecord> loadCSVRaw() {
		List<CSVRecord> records = null;
		try {
			String searchFileFromTextField = StringUtils.trimToEmpty(searchFileTextField.getText());
			if (searchFileFromTextField.isEmpty() || searchFileFromTextField.equalsIgnoreCase(LOCAL_SEARCH_FILE_NAME)) {
				File file = getSearchFile();
				try (BufferedReader br =
		                   new BufferedReader(new FileReader(file))) {
			        CSVParser csvParser = CSVFormat.RFC4180
			        		.withHeader()
			        		.parse(br);
			        records = csvParser.getRecords();
			    }
			} else {
				String url = searchFileFromTextField;
				if (url.contains("google") &&
						url.contains("/edit")) {
					// handle google spreadsheet url that is not an export url
					// https://docs.google.com/spreadsheets/d/1V8r0mIn5njpmVYwFWpqnptAMI6udrIaqhCby1i79UGw/edit#gid=0
					url = StringUtils.substringBeforeLast(url, "/edit");
					url += "/export?gid=0&format=csv";
				}
				try (BufferedReader br =
		                   new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
			        CSVParser csvParser = CSVFormat.RFC4180
			        		.withHeader()
			        		.parse(br);
			        records = csvParser.getRecords();
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
			Dialogs.showError(e);
		}
		return records;
	}

	private void saveSearchesToFile(String csv) {
		File file = getSearchFile();
		try {
			FileUtils.writeStringToFile(file, csv);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File getSearchFile() {
		File file = new File(LOCAL_SEARCH_FILE_NAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return file;
	}

	private void setupResultPaneBinding(SearchPane searchPane, ResultPane resultPane, AutoSearchService autoSearchService) {
		searchPane.searchTable.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			if (n != null) {
				List<SearchResultItem> list = n.getResultList();
				if (n.getAutoSearch() && list != null) {
					resultPane.setSearch(n);
				} else if(!n.getAutoSearch()) {
					manualTaskRun(n);
				}
			}
		});
		autoSearchService.setCallback(noOfItemsFound -> {
//			refreshResultColumn();
			Search search = searchPane.searchTable.getSelectionModel().getSelectedItem();
        	if (search != null && search.getResultList() != null && search.getAutoSearch()) {
        		Platform.runLater(() -> resultPane.setSearch(search));
			}
        	if (noOfItemsFound > 0) {
        		String soundPath = resultPane.soundButton.getUserData().toString();
        		File file = new File(soundPath);
                if (file.exists()) {
                	String url;
					try {
						url = file.toURI().toURL().toExternalForm();
						AudioClip sound = new AudioClip(url);
						sound.play();
					} catch (Exception e) {
						e.printStackTrace();
						Dialogs.showError(e);
					}
				}
			}
		});
	}

//	public void refreshResultColumn() {
		// workaround http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
		// yeah java sucks on some parts
//		Platform.runLater(() -> {
//			searchPane.searchTable.getCo.setVisible(false);
//			searchPane.getResultColumn().setVisible(true);
//		});
//	}
	
	public void manualTaskRun(Search search) {
		String url = search.getUrl();
		if (isNotBlank(url)) {
			Task<Search> task = new Task<Search>() {
				@Override
				protected Search call() throws Exception {
					String html = AutoSearchService.doDownload(search.getUrl(), search.getSort());
					search.setHtml(html);
					search.parseHtml();
					return search;
				}
			};
			resultPane.progressIndicator.visibleProperty().unbind();
			resultPane.progressIndicator.visibleProperty().bind(task.runningProperty());
			task.setOnSucceeded(e -> { 
				resultPane.setSearch(task.getValue());
//				refreshResultColumn();
				});
			task.setOnFailed(e -> {
				Dialogs.showError(task.getException());
//				refreshResultColumn();
				});
			new Thread(task).start();
		} else {
			resultPane.setSearch(search);
		}
	}


}