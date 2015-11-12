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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import poe.trade.assist.scraper.BackendClient;

public class SearchFormService extends Service<String> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private StringProperty url = new SimpleStringProperty();

	public StringProperty urlProperty() {
		return url;
	}

	private static final BackendClient backendClient = new BackendClient();

	@Override
	protected Task<String> createTask() {
		return new Task<String>() {

			@Override
			protected String call() throws Exception {
				String url = SearchFormService.this.url.get();
				if (isNotBlank(url)) {
					String html = doDownload(url);
					// FileUtils.writeStringToFile(new File(search.getName()),
					// html);
					return html;
				}
				return "Something went really wrong";
			}
		};
	}

	public static String doDownload(String url) {
		int count = 0;
		int maxTries = 3;
		String errors = "Errors in " + maxTries + " retries..";
		while (true) {
			try {
				return backendClient.get(url);
			} catch (Exception e) {
				e.printStackTrace();
				errors += System.lineSeparator() + e.getMessage();
				if (++count == maxTries)
					break;
			}
		}
		return errors;
	}

}
