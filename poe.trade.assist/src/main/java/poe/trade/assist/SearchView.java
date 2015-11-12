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
import java.net.MalformedURLException;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author thirdy
 *
 */
public class SearchView extends StackPane {
	
	private WebView webView = new WebView();
	private WebEngine webEngine = webView.getEngine();
	
	private ObjectProperty<Search> search = new SimpleObjectProperty<>();
	public ObjectProperty<Search> searchProperty() {return search;}
	
	public SearchView() {
		search.addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				String searchFormHtml = newVal.getHtml();
				searchFormHtml = addHeadElements(searchFormHtml);
				webEngine.loadContent(searchFormHtml);
			}
		});
		getChildren().add(webView);
	}

	private String addHeadElements(String html) {
		String htmlDirectory = htmlDirectory();
		Document doc = Jsoup.parse(html);
		Element head = doc.head();

		// Replace everthing in the <head>
		head.children().stream().forEach(e -> e.remove());
		head.appendElement("meta").attr("charset", "utf-8");
		head.appendElement("meta").attr("name", "viewport").attr("content", "width=device-width");
		head.appendElement("title").text("poe.trade.assist");
		head.appendElement("script").attr("type", "text/javascript").attr("src", htmlDirectory + "packed.js");
		head.appendElement("link").attr("rel", "stylesheet").attr("href", htmlDirectory + "packed_dark.css");
		
		return doc.toString();
	}

	private String htmlDirectory() {
		try {
			return new File("html").toURI().toURL().toString();
		} catch (MalformedURLException e1) {
			throw new RuntimeException(e1);
		}
	}

}
