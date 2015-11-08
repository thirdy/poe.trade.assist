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

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import poe.trade.assist.scraper.SearchPageScraper.SearchResultItem;
import poe.trade.assist.util.SwingUtil;

public class ResultPane extends VBox {
	
	ListView<SearchResultItem> listView = new ListView<SearchResultItem>();
	Label statusLabel = new Label("Ready");
	
	public ResultPane() {
		listView.setMinWidth(600);
		setupTooltip();
		setupSelectionListener();
		
		final Label label = new Label("Results");
		label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		setSpacing(5);
		setPadding(new Insets(10, 0, 0, 10));
		getChildren().addAll(label, listView, statusLabel);
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
		tooltip.textProperty().bind(
				Bindings.createStringBinding(() -> {
					SearchResultItem item = listView.getSelectionModel().getSelectedItem();
					return item != null ? item.toStringObject() : "";
				}, listView.getSelectionModel().selectedItemProperty()));
		listView.setTooltip(tooltip);
	}
}
