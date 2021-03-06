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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import poe.trade.assist.scraper.SearchPageScraper;
import poe.trade.assist.scraper.SearchPageScraper.SearchResultItem;

public class Search {

    public Search(String name, String tags, String url, Boolean autoSearch, String sort) {
    	this.name.set(name);
    	this.tags.set(tags);
    	this.url.set(url);
    	this.autoSearch.set(autoSearch);
    	this.sort.set(sort);
	}
	SimpleStringProperty name = new SimpleStringProperty();
	SimpleStringProperty tags = new SimpleStringProperty();
	SimpleStringProperty url = new SimpleStringProperty();
	SimpleBooleanProperty autoSearch = new SimpleBooleanProperty();
	SimpleStringProperty result = new SimpleStringProperty();
	SimpleStringProperty sort = new SimpleStringProperty("price_in_chaos");
	
	public SimpleStringProperty nameProperty() {  return name; }
	public SimpleStringProperty tagsProperty() {  return tags; }
	public SimpleStringProperty urlProperty() {  return url; }
	public SimpleBooleanProperty autoSearchProperty() {  return autoSearch; }
	public SimpleStringProperty resultProperty() {  return result; }
	public SimpleStringProperty sortProperty() {  return sort; }
    
	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public String getTags() {
		return tags.get();
	}
	public void setTags(String tags) {
		this.tags.set(tags);
	}
	public String getUrl() {
		return url.get();
	}
	public void setUrl(String url) {
		this.url.set(url);
	}
	public Boolean getAutoSearch() {
		return autoSearch.get();
	}
	public void setAutoSearch(Boolean autoSearch) {
		this.autoSearch.set(autoSearch);
	}
	public String getResult() {
		return result.get();
	}
	public void setResult(String result) {
		this.result.set(result);
	}
	public String getSort() {
		return sort.get();
	}
	public void setSort(String sort) {
		this.sort.set(sort);
	}
	private String html;
	private List<SearchResultItem> resultList = Arrays.asList(SearchResultItem.message("Waiting for autodownload..."));
	
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
			long oldCount = resultList.stream().filter(r -> r.getId() != null).count();
			setResult(String.format("%d %s", items.size(), oldCount > 0 ? "(" + oldCount + ")" : ""));
			resultList = items;
		} catch (Exception e) {
			e.printStackTrace();
			resultList = Arrays.asList(SearchResultItem.message("Error in parsing: " + e.getMessage()));
			setResult("Error");
		}
	}

	public List<SearchResultItem> getResultList() {
		return resultList;
	}
	
	public static class SearchPersist {
		String name;
		String tags;
		String url;
		Boolean autoSearch;
		String sort;
		
		public SearchPersist() {}
		public SearchPersist(String name, String tags, String url, Boolean autoSearch, String sort) {
			this.name = name;
			this.tags = tags;
			this.url = url;
			this.autoSearch = autoSearch;
			this.sort = sort;
		}

		public Search toSearch() {
			Search search = new Search(name, tags, url, autoSearch, sort);
			return search;
		}
		public String[] toCSVArray() {
			return new String[] {name, tags, url, autoSearch.toString(), sort};
		}
	}
	
	public SearchPersist toSearchPersist() {
		SearchPersist persist = new SearchPersist();
		persist.name = getName();
		persist.tags = getTags();
		persist.url  = getUrl();
		persist.autoSearch = getAutoSearch();
		persist.sort = getSort();
		return persist;
	}

}