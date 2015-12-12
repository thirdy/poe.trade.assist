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

import static java.lang.String.format;

import java.io.File;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import poe.trade.assist.scraper.BackendClient;

/**
 * @author thirdy
 *
 */
public class UniquesListSearchGenerator {
	
	public static String[] lists = new String[] {
			"List_of_unique_weapons",
			"List_of_unique_accessories",
			"List_of_unique_armour",
			"List_of_unique_flasks",
			"List_of_unique_jewels",
			"List_of_unique_maps"
	};

	static BackendClient backendClient = new BackendClient();
	
	/**imgurl, reqLvl, base, mod
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		List<String> outputLines = new LinkedList<>(); 
		outputLines.add("Name	Art	Req.Level	Base	Mods	TaslismanSC	TalismanHC	Standard	Hardcore	poewiki");
		for (String list : lists) {
			HttpResponse<String> response = Unirest.get("http://pathofexile.gamepedia.com/" + list)
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0")
				.asString();
			Document doc = Jsoup.parse(response.getBody());
			Elements elems = doc.select("table.wikitable.sortable");
			for (Element table : elems) {
				Elements rows = table.select("tr");
				int ctr = 0;
				boolean hasRequiredLevel = false;
				for (Element row : rows) {
					if (ctr == 0) { // first row is headers
						hasRequiredLevel = !row.select("abbr[title=\"Required Level\"]").isEmpty();
						ctr++;
						continue;
					} 
					String name = row.child(0).child(0).attr("title");
					System.out.println("Now processing: " + name);
					String imgurl = "=IMAGE(\"" + row.select("img").attr("src") + "\", 3)";
					String base = row.child(1).child(0).attr("title");
					String reqLvl = hasRequiredLevel ? row.child(2).text() : "0";
					reqLvl = reqLvl.equalsIgnoreCase("n/a") ? "0" : reqLvl;
					String mod = "=\"";
					Elements mods = row.select("span.itemboxstatsgroup.text-mod");
					if (!mods.isEmpty()) {
						if(mods.size() > 2) throw new Exception("mods.size() is > 2. " + name + " - " + mods.toString() );
						boolean hasImplicit = mods.size() > 1; 
						String imp = hasImplicit ? mods.get(0).text() : "";
						int expIdx = hasImplicit ? 1 : 0;
						String lineSeparator = "\"&CHAR(10)&\"";
						String exp = mods.get(expIdx).textNodes().stream().map(n -> n.text().trim()).filter(s -> !s.isEmpty()).collect(Collectors.joining(lineSeparator));
						String additionalExp = mods.get(expIdx).children().stream().filter(e -> e.hasText()).map(e -> e.text().trim()).collect(Collectors.joining(lineSeparator));
						if(additionalExp != null && !additionalExp.isEmpty()) exp += lineSeparator + additionalExp;
						mod += imp;
						if(hasImplicit) mod += (lineSeparator + "--------------" + lineSeparator);
						mod += exp;
					}
					mod += "\"";
					
					String standard = "Standard";
					String hardcore = "Hardcore";
			        String tempsc = "Talisman";
			        String temphc = "Talisman+Hardcore";
			        String nameenc = URLEncoder.encode(name, "UTF-8");
					String sc = hyperlink(getSearchURL(standard, nameenc));
					String hc = hyperlink(getSearchURL(hardcore, nameenc));
					String tsc = hyperlink(getSearchURL(tempsc, nameenc));
					String thc = hyperlink(getSearchURL(temphc, nameenc));
					String poewikiurl = hyperlink("http://pathofexile.gamepedia.com/" + (name.replace(' ', '_')));
					
					String s = format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", name, imgurl, reqLvl, base, mod, tsc, thc, sc, hc, poewikiurl);
					outputLines.add(s);
					Thread.sleep(1000);
				}
			}
		}
		FileUtils.writeLines(new File("uniqueslist.txt"), outputLines);
	}
	
	
	private static String hyperlink(String url) {
		return format("=HYPERLINK(\"%s\", \"link\")", url);
	}


	private static String getSearchURL(String league, String name)  {
		String payload = format("league=%s&type=&base=&name=%s&dmg_min=&dmg_max=&aps_min=&aps_max=&crit_min=&crit_max=&dps_min=&dps_max=&edps_min=&edps_max=&pdps_min=&pdps_max=&armour_min=&armour_max=&evasion_min=&evasion_max=&shield_min=&shield_max=&block_min=&block_max=&sockets_min=&sockets_max=&link_min=&link_max=&sockets_r=&sockets_g=&sockets_b=&sockets_w=&linked_r=&linked_g=&linked_b=&linked_w=&rlevel_min=&rlevel_max=&rstr_min=&rstr_max=&rdex_min=&rdex_max=&rint_min=&rint_max=&impl=&impl_min=&impl_max=&mod_name=&mod_min=&mod_max=&group_type=And&group_min=&group_max=&group_count=1&q_min=&q_max=&level_min=&level_max=&mapq_min=&mapq_max=&rarity=unique&seller=&thread=&identified=&corrupted=&online=x&buyout=x&altart=&capquality=x&buyout_min=&buyout_max=&buyout_currency=&crafted=",
				league, name );
		int tries = 50;
		String searchPage = null;
		while(searchPage == null && tries > 0) {
			try {
				searchPage = backendClient.post( payload );
			} catch (Exception e) {
				tries--;
			}
		}
		
		return searchPage;
	}

}
