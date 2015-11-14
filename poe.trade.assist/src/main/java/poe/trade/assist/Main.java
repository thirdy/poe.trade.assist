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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
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
    
    private SearchPane searchPane;
	private ResultPane resultPane;

	public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) {
    	BorderPane root = new BorderPane();
    	
    	List<Search> searchList = loadSearchListFromFile();
 
    	AutoSearchService autoSearchService = new AutoSearchService();
        searchPane = new SearchPane(searchList);
        resultPane = new ResultPane(this);
        
        autoSearchService.searchesProperty().bind(searchPane.dataProperty());
        resultPane.statusLabel.textProperty().bind(autoSearchService.messageProperty());
        resultPane.runNowButton.setOnAction(e -> autoSearchService.restart() );
        autoSearchService.minsToSleepProperty().bind(resultPane.noOfMinsTextField.textProperty());
        setupResultPaneBinding(searchPane, resultPane, autoSearchService);
        if(searchList.size() > 0) searchPane.table.getSelectionModel().select(0);
        
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
        stage.setTitle("poe.trade.assist v5 (Durian)");
//        stage.setWidth(1200);
//        stage.setHeight(550);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

	private void saveSearchList(SearchPane searchPane) {
		List<Search> list = new ArrayList<>(searchPane.table.getItems());
		if (list != null) {
			Gson gson = new Gson();
			List<SearchPersist> persistList = list.stream().map(e -> e.toSearchPersist()).collect(toList());
			String json = gson.toJson(persistList);
			saveSearchesToFile(json);
		}
	}

	private List<Search> loadSearchListFromFile() {
		List<Search> list = Arrays.asList(
				new Search("Lakishu's Blade 3L", "http://poe.trade/search/nokagatasasaha", false, "price_in_chaos"),
				new Search("Tabula 5c", "http://poe.trade/search/adeyubetedamit", true, "price_in_chaos")
		);
		// 1ex aegis http://poe.trade/search/atamiomimetami
		// kaom's heart http://poe.trade/search/kuwahamigaruri
		
		File file = getSearchFile();
		try {
			String json = FileUtils.readFileToString(file);
			if (StringUtils.isNotBlank(json)) {
				Gson gson = new Gson();
				Type listType = new TypeToken<ArrayList<SearchPersist>>() { }.getType();
				List<SearchPersist> persistList = gson.fromJson(json, listType);
				list = persistList.stream().map(e -> e.toSearch()).collect(toList());
			}
		} catch (IOException e) {
			e.printStackTrace();
			Dialogs.showError(e);
		}
		return list;
	}

	private void saveSearchesToFile(String json) {
		File file = getSearchFile();
		try {
			FileUtils.writeStringToFile(file, json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File getSearchFile() {
		File file = new File("search.json");
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
		searchPane.table.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
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
			refreshResultColumn();
			Search search = searchPane.table.getSelectionModel().getSelectedItem();
        	if (search != null && search.getResultList() != null && search.getAutoSearch()) {
        		Platform.runLater(() -> resultPane.setSearch(search));
			}
        	if (noOfItemsFound > 0) {
        		String soundPath = resultPane.soundFileTextField.getText();
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

	public void refreshResultColumn() {
		// workaround http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
		// yeah java sucks on some parts
		Platform.runLater(() -> {
			searchPane.getResultColumn().setVisible(false);
			searchPane.getResultColumn().setVisible(true);
		});
	}
	
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
				refreshResultColumn();
				});
			task.setOnFailed(e -> {
				Dialogs.showError(task.getException());
				refreshResultColumn();
				});
			new Thread(task).start();
		} else {
			resultPane.setSearch(search);
		}
	}


}