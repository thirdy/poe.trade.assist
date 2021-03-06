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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Properties;

import poe.trade.assist.util.Dialogs;

/**
 * @author thirdy
 *
 */
public class Config extends Properties {

	public static final String SEARCH_MINUTES = "search.minutes";
	public static final String AUTO_ENABLE = "auto.enable";
	public static final String USER_AGENT = "user.agent";
	public static final String COOKIE = "cookie";

	public static Config load() {
		Config config = new Config();
		
		boolean fileExists = fileExists();
		File file = getFile();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			config.load(br);
		} catch (IOException e) {
			e.printStackTrace();
			Dialogs.showError(e);
		}
		
		if (!fileExists) {
			Optional<String> usrAgent = Dialogs.showInput("User Agent Config", "Enter User-Agent:");
			Optional<String> cookie = Dialogs.showInput("Cookie Config", "Enter Cookie:");
			usrAgent.ifPresent(s -> config.putIfAbsent(USER_AGENT, s));
			cookie.ifPresent  (s -> config.putIfAbsent(COOKIE, s));
		} 
		
		config.putIfAbsent(SEARCH_MINUTES, "10");
		config.putIfAbsent(AUTO_ENABLE, "true");
		
		return config;
	}
	
	private static boolean fileExists() {
		File file = new File("config.properties");
		return file.exists();
	}

	static final String SEARCH_FILE = "search.file";
	static final String SOUND_FILE = "sound.file";
	
	public Optional<String> get(String key) {
		return Optional.ofNullable(getProperty(key));
	}
	
	public void save() {
		try(OutputStream os = new FileOutputStream(getFile())) {
			store(os, "auto saving config");
		}  catch (IOException e) {
			e.printStackTrace();
			Dialogs.showError(e);
		}
	}
	
	private static File getFile() {
		File file = new File("config.properties");
		boolean fileExists = file.exists();
		if (!fileExists) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return file;
	}

}
