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
package poe.trade.assist.service;

import static java.lang.String.format;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import poe.trade.assist.Search;
import poe.trade.assist.scraper.BackendClient;
import poe.trade.assist.util.Dialogs;

public class SearchService extends Service<Void> {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private ListProperty<Search> searches = new SimpleListProperty<>();
    public ListProperty<Search> searchesProperty() {return searches;}
    
    private StringProperty minsToSleep = new SimpleStringProperty();
    public StringProperty minsToSleepProperty() {return minsToSleep;}
    
    private BackendClient backendClient = new BackendClient();

    // Just a way to provide notifications when searches' elements has been updated
	private Runnable callback;
    
    public SearchService() {
		setOnSucceeded(e -> restart());
		setOnFailed	 (e -> {
			getException().printStackTrace();
			Dialogs.showError(getException());
			restart();
		});
	}
    
	@Override
    protected Task<Void> createTask() {
        return new Task<Void>() {  
        	
			@Override protected Void call() throws Exception {
            		for (Search search : searches) {
            			update(format("Downloading... %s %s", search.getName(), search.getUrl()));
            			String html = doDownload(search.getUrl());
            			update(format("%s for %s %s", 
            					html.isEmpty() ? "Failure" : "Success",
            					search.getName(), search.getUrl()));
            			search.setHtml(html);
            			search.parseHtml();
					}
            		callback.run();
            		
            		String sleepMins = minsToSleep.get();
					int mins = 60 * (NumberUtils.isParsable(sleepMins) ? Integer.parseInt(sleepMins) : 5);
            		
					for (int i = mins; i >= 0; i--) {
						update("Sleeping... " + i);
						Thread.sleep(1000);
					}
            	return null;
            }

			private void update(String msg) {
				Platform.runLater(() -> updateMessage(msg));
			}
        };
	}

	private String doDownload(String url) {
		int count = 0;
		int maxTries = 3;
		while(true) {
		    try {
		    	return backendClient.get(url);
		    } catch (Exception e) {
		    	e.printStackTrace();
		        if (++count == maxTries) break;
		    }
		}
		
		return "";
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}
	

}
