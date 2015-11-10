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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import poe.trade.assist.scraper.SearchPageScraper.SearchResultItem;
import poe.trade.assist.util.SearchResultItemDialog;
import poe.trade.assist.util.SwingUtil;

public class ResultPane extends VBox {
	
	ListView<SearchResultItem> listView = new ListView<SearchResultItem>();
	Label statusLabel = new Label("Ready");
	Button runNowButton = new Button("Run now");
	TextField noOfMinsTextField = new TextField();
	TextField soundFileTextField = new TextField();
	Hyperlink website = new Hyperlink("http://thirdy.github.io/poe.trade.assist/");
	ProgressIndicator progressIndicator = new ProgressIndicator(-1.0f);
	
	public ResultPane() {
//		listView.setMinWidth(600);
		listView.setMaxWidth(Double.MAX_VALUE);
		noOfMinsTextField.setPromptText("Minutes to sleep");
		noOfMinsTextField.setPrefWidth(120);
		soundFileTextField.setPromptText("Path to sound file, can be mp3, acc, wav");
		soundFileTextField.setPrefWidth(415);
		soundFileTextField.setOnMouseClicked(e -> openSoundFileChooseDialog());
		soundFileTextField.setText(new File("notification.wav").getAbsolutePath());
		setupTooltip();
		setupSelectionListener();
		Region veilOfTheNight = new Region();
		veilOfTheNight.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3)");
		progressIndicator.setVisible(false);
		veilOfTheNight.visibleProperty().bind(progressIndicator.visibleProperty());
		
		website.setOnAction(e -> SwingUtil.openUrlViaBrowser(website.getText()));
		listView.setOnMouseClicked(this::listViewClicked);
		
		final Label label = new Label("Results");
		label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		setSpacing(5);
		setPadding(new Insets(10, 0, 0, 10));
		HBox hBox = new HBox(3, runNowButton, noOfMinsTextField, soundFileTextField);
		hBox.setAlignment(Pos.CENTER_LEFT);
		StackPane stackPane = new StackPane(listView, veilOfTheNight, progressIndicator);
		stackPane.setMaxWidth(Double.MAX_VALUE);
		getChildren().addAll(label, stackPane, hBox, new VBox(new TextFlow(new Label("Status: "), statusLabel), 
				new TextFlow(new Text("Check for the latest updates at: "), website)));
		setMaxWidth(Double.MAX_VALUE);
		setMargin(stackPane, new Insets(0, 11, 0, 0));
		VBox.setVgrow(stackPane, Priority.ALWAYS);
	}

	private void listViewClicked(MouseEvent me) {
		SearchResultItem item = listView.getSelectionModel().getSelectedItem();
		if (me.getClickCount() > 1 && item != null) {
			SearchResultItemDialog.show(item);
		}
	}

	private void openSoundFileChooseDialog() {
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Open Sound File");
		 fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
		         new ExtensionFilter("All Files", "*.*"));
		 File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
		 if (selectedFile != null) {
			 soundFileTextField.setText(selectedFile.getAbsolutePath());
		 }
	}

	private void setupSelectionListener() {
		listView.getSelectionModel().selectedItemProperty().addListener((obrv, oldVal, newVal) -> {
			if (newVal != null) {
				SwingUtil.copyToClipboard(newVal.toString());
			}
		});
	}

	private void setupTooltip() {
		Tooltip tooltip = new Tooltip();
		listView.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			if (n != null) {
				SearchResultItem item = listView.getSelectionModel().getSelectedItem();
				String msg = item != null ? item.toStringObject() : "";
				tooltip.setText(msg);
				listView.setTooltip(tooltip);
			} else {
				listView.setTooltip(null);
			}
		});
	}
}
