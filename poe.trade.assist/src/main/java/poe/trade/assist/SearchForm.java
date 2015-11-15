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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import poe.trade.assist.scraper.BackendClient;
import poe.trade.assist.service.SearchFormService;
import poe.trade.assist.util.Dialogs;
import poe.trade.assist.util.SwingUtil;

/**
 * @author thirdy
 *
 */
public class SearchForm extends Tab {
	
	private Map<String, String> cache = new HashMap<>();
	
	private WebView webView = new WebView();
	private WebEngine webEngine = webView.getEngine();
	
	private SearchFormService searchFormService = new SearchFormService();
	
	private ObjectProperty<Search> search = new SimpleObjectProperty<>();
	public ObjectProperty<Search> searchProperty() {return search;}
	
	public SearchForm(String title, Main main, ResultPane resultPane) {
		super(title);
		setOnSelectionChanged(e -> {
			if (this.isSelected()) {
				loadUp();
			}
		});
		search.addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				if (this.isSelected()) {
					loadUp();
				}
			}
		});
		webEngine.getLoadWorker().stateProperty().addListener((observ, oldVal, newVal) -> {
			if (newVal.equals(Worker.State.SUCCEEDED)) {
//				System.out.println("LOAOOOOOOOOOOOOOOOOOOOOOD");
//				JSObject window = (JSObject) webEngine.executeScript("window");
//				webEngine.executeScript("onBluemarlineReady()");
//				window.setMember("assistcallback", this);
//				System.out.println(search.get().getUrl());
//				System.out.println(webEngine.getLocation());
				if (StringUtils.isNotBlank(webEngine.getLocation()) 
						&& !search.get().getUrl().equalsIgnoreCase(webEngine.getLocation())) {
					search.get().setUrl(webEngine.getLocation());
//					main.refreshResultColumn();
//					main.selectFromSearchList(search.get());
//					SearchForm.this.getTabPane().getSelectionModel().clearAndSelect(0);
				}
			}
		});
//		searchPostService.setOnSucceeded(e -> {
//			search.get().setUrl(searchPostService.getValue());
//			main.refreshResultColumn();
//			resultPane.setSearch(search.get());
//			SearchForm.this.getTabPane().getSelectionModel().clearAndSelect(2);
//		});
		Region veilOfTheNight = new Region();
		veilOfTheNight.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3)");
		ProgressIndicator progressIndicator = new ProgressIndicator(-1.0f);
		veilOfTheNight.visibleProperty().bind(progressIndicator.visibleProperty());
		progressIndicator.visibleProperty().bind(searchFormService.runningProperty());
//		progressIndicator.visibleProperty().bind(searchFormService.runningProperty().or(searchPostService.runningProperty()));
		setContent(new StackPane(webView, veilOfTheNight, progressIndicator));
	}
	
	private void loadUp() {
		String _url = search.get().getUrl();
		String url = StringUtils.isBlank(_url) ? "http://poe.trade" : _url;
		
		String html = cache.get(url);
		if (html == null) {
			searchFormService.urlProperty().setValue(url);
			searchFormService.setOnSucceeded(e -> {
				String searchFormHtml = searchFormService.getValue();
				cache.put(url, searchFormHtml);
				loadToWebView(searchFormHtml);
			});
			searchFormService.setOnFailed(e -> {
				webEngine.loadContent(searchFormService.getException().getMessage());
			});
			searchFormService.restart();
		} else {
			System.out.println("LOADING FROM CACHE");
			loadToWebView(html);
		}
	}


	private void loadToWebView(String html) {
		html = removeAllExceptSearchForm(html);
		webEngine.loadContent(html);
	}

	private String removeAllExceptSearchForm(String html) {
		String htmlDirectory = htmlDirectory();
		Document doc = Jsoup.parse(html);
		
		// Remove stuff outside of id="main"
//		doc.body().children().stream().filter(e -> !"main".equalsIgnoreCase(e.id())).forEach(e -> e.remove());
		
		Element head = doc.head();

		// Replace everthing in the <head>
		head.children().stream().forEach(e -> e.remove());
		head.appendElement("meta").attr("charset", "utf-8");
		head.appendElement("meta").attr("name", "viewport").attr("content", "width=device-width");
		head.appendElement("title").text("poe.trade.assist");
		head.appendElement("script").attr("type", "text/javascript").attr("src", htmlDirectory + "packed.js");
		head.appendElement("link").attr("rel", "stylesheet").attr("href", htmlDirectory + "packed_dark.css");
		
		// Show search form
		Optional.ofNullable(doc.getElementById("search-form")).ifPresent(e ->  e.attr("style", "") );

		Optional.ofNullable(doc.getElementById("search")).ifPresent(e ->  e.attr("action", "http://poe.trade/search") );
//		
//		Element mainElement = doc.getElementById("main");
//		Element topDivContainer = mainElement.child(0);
//
//		// Remove everthing that is not id="content" or h2
//		topDivContainer.children().stream()
//			.filter(e -> !"content".equalsIgnoreCase(e.id()))
//			.filter(e -> !e.tag().getName().equalsIgnoreCase("h2"))
//			.forEach(e -> e.remove());
//		
//		// Clean up stuff inside id="content"
//		
//			// Remove "Show search form", "search/import"
//			Optional<Element> searchFormElem = doc.getElementsByTag("a").stream().filter(e -> e.hasClass("button") && e.hasClass("secondary") && e.hasClass("expand")).findFirst();
//			searchFormElem.ifPresent(e -> e.remove());
//			
//			Optional<Element> searchOrImportDiv = doc.getElementsByTag("div").stream().filter(e -> e.hasClass("row") && e.hasClass("form-choose-action")).findFirst();
//			searchOrImportDiv.ifPresent(e -> e.remove());
//
//			// Remove search results
			Elements searchResultBlocks = doc.getElementsByClass("search-results-block");
			if (searchResultBlocks.size() > 0) {
				searchResultBlocks.get(0).remove();
			}

		 // append assist as the last element in body
//		 doc.body().appendElement("script").attr("type", "text/javascript").attr("src", htmlDirectory + "assist.js");
			
		String cleanHtml = doc.toString();
		return cleanHtml;
	}
	
	private String htmlDirectory() {
		try {
			return new File("html").toURI().toURL().toString();
		} catch (MalformedURLException e1) {
			throw new RuntimeException(e1);
		}
	}

//	// --- CALL BACK FROM HTML ---//
//	public void copyToClipboard(String s) {
//		SwingUtil.copyToClipboard(s);
//	}
//
//	public void searchClicked(String payload) {
//		System.out.println("xSearch Clicked, payload: " + payload);
//		try {
//			payload = Arrays.asList(StringUtils.split(payload, "&")).stream()
//					.map(token -> encode(token))
//					.collect(Collectors.joining("&"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println("Encoded payload: " + payload);
//		searchPostService.payload.set(payload);
//		searchPostService.restart();
//	}
//	private String encode(String token) {
//		String[] split = token.split("=");
//		if (split.length > 1) {
//			try {
//				split[1] = URLEncoder.encode(split[1], "UTF-8");
//				// damn, java encode is diffent from html encode?
//				split[1] = StringUtils.replace(split[1], "%2B", "+");
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//		return 
//				split.length > 1 ? 
//						StringUtils.join(split, '=') :
//							token;
//	}
//	// --- END CALL BACK FROM HTML ---//
//	SearchPostService searchPostService = new SearchPostService();
//	private static class SearchPostService extends Service<String> {
//		BackendClient backendClient = new BackendClient();
//		StringProperty payload = new SimpleStringProperty();
//		@Override
//		protected Task<String> createTask() {
//			return new Task<String>() {
//
//				@Override
//				protected String call() throws Exception {
//					int count = 0;
//					int maxTries = 3;
//					while(true) {
//					    try {
//					    	return backendClient.post(payload.get());
//					    } catch (Exception e) {
//					    	e.printStackTrace();
//					        if (++count == maxTries) break;
//					    }
//					}
//					return null;
//				}
//			};
//		}
//	}
}
