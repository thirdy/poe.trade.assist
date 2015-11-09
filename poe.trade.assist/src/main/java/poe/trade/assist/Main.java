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
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import poe.trade.assist.service.SearchService;
import poe.trade.assist.util.Dialogs;
 
public class Main extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) {
    	StackPane root = new StackPane();
    	
    	List<Search> searchList = loadSearchListFromFile();
 
    	SearchService searchService = new SearchService();
        SearchPane searchPane = new SearchPane(searchList);
        ResultPane resultPane = new ResultPane();
        
        searchService.searchesProperty().bind(searchPane.dataProperty());
        resultPane.statusLabel.textProperty().bind(searchService.messageProperty());
        resultPane.runNowButton.setOnAction(e -> searchService.restart() );
        searchService.minsToSleepProperty().bind(resultPane.noOfMinsTextField.textProperty());
        setupResultPaneBinding(searchPane, resultPane, searchService);
        
        stage.setOnCloseRequest(we -> {
        	List<Search> list = new ArrayList<>(searchPane.table.getItems());
			if (list != null) {
				Gson gson = new Gson();
	        	String json = gson.toJson(list);
	        	saveSearchesToFile(json);
			}
        });
        
        searchService.restart();
        
        HBox container = new HBox(5, searchPane, resultPane);
        root.getChildren().addAll(container);
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image("/assist.png"));
        stage.setTitle("poe.trade.assist v3");
        stage.setWidth(1150);
        stage.setHeight(550);
        stage.setScene(scene);
        stage.show();
    }

	private List<Search> loadSearchListFromFile() {
		List<Search> list = Arrays.asList(new Search("Tabula 30c", "http://poe.trade/search/oremarohokinon"));
		// 1ex aegis http://poe.trade/search/atamiomimetami
		// kaom's heart http://poe.trade/search/kuwahamigaruri
		
		File file = getSearchFile();
		try {
			String json = FileUtils.readFileToString(file);
			if (StringUtils.isNotBlank(json)) {
				Gson gson = new Gson();
				Type listType = new TypeToken<ArrayList<Search>>() { }.getType();
				list = gson.fromJson(json, listType);
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	private void setupResultPaneBinding(SearchPane searchPane, ResultPane resultPane, SearchService searchService) {
		searchPane.table.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			if (n != null && n.getResultList() != null) {
				resultPane.listView.setItems(
						FXCollections.observableArrayList(n.getResultList())
					);
			}
		});
		searchService.setCallback(noOfItemsFound -> {
			Search search = searchPane.table.getSelectionModel().getSelectedItem();
        	if (search != null && search.getResultList() != null) {
        		Platform.runLater(() -> resultPane.listView.setItems(
        				FXCollections.observableArrayList(search.getResultList())
					));
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
 

}