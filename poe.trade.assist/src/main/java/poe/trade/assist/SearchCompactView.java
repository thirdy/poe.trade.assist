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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import poe.trade.assist.fx.TableViewPlus;
import poe.trade.assist.scraper.SearchPageScraper.SearchResultItem;

/**
 * @author thirdy
 *
 */
public class SearchCompactView extends StackPane {
	
	TableViewPlus<SearchResultItem> searchTable;
	
	private ListProperty<SearchResultItem> data;
	public ListProperty<SearchResultItem> dataProperty(){return data;}
	
	public SearchCompactView() {
		data = new SimpleListProperty<>(
				FXCollections.observableArrayList());
		setupSearchTable();
		getChildren().add(searchTable);
	}
	
	private void setupSearchTable() {
		searchTable = new TableViewPlus<>(newHashMap(
				"id", "id",
				"buyout", "buyout",
				"name", "name",
				"ign", "ign",
				"socketsRaw", "socketsRaw",
				"quality", "quality",
				"physDmgRangeAtMaxQuality", "physDmgRangeAtMaxQuality",
				"physDmgAtMaxQuality", "physDmgAtMaxQuality",
				"eleDmgRange", "eleDmgRange",
				"attackSpeed", "attackSpeed",
				"dmgAtMaxQuality", "dmgAtMaxQuality",
				"crit", "crit",
				"eleDmg", "eleDmg",
				"armourAtMaxQuality", "armourAtMaxQuality",
				"evasionAtMaxQuality", "evasionAtMaxQuality",
				"energyShieldAtMaxQuality", "energyShieldAtMaxQuality",
				"block", "block",
				"reqLvl", "reqLvl",
				"reqStr", "reqStr",
				"reqInt", "reqInt",
				"reqDex", "reqDex",
				"ageAndHighLvl", "ageAndHighLvl",
				"league", "league",
				"seller", "seller",
				"thread", "thread",
				"sellerid", "sellerid",
				"threadUrl", "threadUrl",
				"imageUrl", "imageUrl"
				),
				null,
				data);
		
		searchTable.setEditable(false);
		searchTable.setPrefWidth(500);
//		searchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
//		modMappingTable.setMaxHeight(Double.MAX_VALUE);
//		modMappingTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//		modMappingTable.setId("modMappingTable");
	}

	private Map<String, String> newHashMap(String ... string) {
		Map<String, String> m = new LinkedHashMap<>();
		for (int i = 0; i < string.length; ) {
			m.put(string[i], string[i+1]);
			i += 2;
		}
		return m;
	}

}
