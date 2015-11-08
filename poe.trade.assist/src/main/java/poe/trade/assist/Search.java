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

import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import poe.trade.assist.scraper.SearchPageScraper;
import poe.trade.assist.scraper.SearchPageScraper.SearchResultItem;

public class Search {

    public Search(String name, String url) {
    	this.name.set(name);
    	this.url.set(url);
	}
	SimpleStringProperty name = new SimpleStringProperty();
	SimpleStringProperty url = new SimpleStringProperty();
    
	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public String getUrl() {
		return url.get();
	}
	public void setUrl(String url) {
		this.url.set(url);
	}
	
	private transient String html;
	private transient List<SearchResultItem> resultList = Arrays.asList(SearchResultItem.message("Waiting for autodownload..."));
	
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	
	public void parseHtml() {
		try {
			SearchPageScraper searchPageScraper = new SearchPageScraper(html);
			List<SearchResultItem> items = searchPageScraper.parse();
			resultList = items;
		} catch (Exception e) {
			e.printStackTrace();
			resultList = Arrays.asList(SearchResultItem.message("Error in parsing: " + e.getMessage()));
		}
	}
    

	public List<SearchResultItem> getResultList() {
		return resultList;
	}
	

}