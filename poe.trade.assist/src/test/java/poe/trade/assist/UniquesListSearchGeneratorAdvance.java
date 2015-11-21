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

import static java.util.Arrays.asList;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

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
public class UniquesListSearchGeneratorAdvance {
	
	// TODO, load this list dynamically via scraping wiki pages
	public static String[] uniques = new String[] {
			// List_of_unique_weapons
			"The Screaming Eagle", "Dreadarc", "Relentless Fury", "Moonbender's Wing", "Soul Taker", "Jack, the Axe", "Dyadus", "Limbsplit", "Wideswing", "Reaper's Pursuit", "The Harvest", "The Blood Reaper", "Kaom's Primacy", "Wings of Entropy", "Kingmaker", "Atziri's Disfavour", "Quill Rain", "Storm Cloud", "Doomfletch", "Death's Harp", "Infractem", "Null's Inclination", "Chin Sol", "Darkscorn", "Voltaxic Rift", "Lioneye's Glare", "Windripper", "Silverbranch", "Wildslash", "Al Dhih", "Mortem Morsu", "Cybil's Paw", "Ornament of the East", "Bloodseeker", "Essentia Sanguis", "Izaro's Dilemma", "Last Resort", "Goredrill", "Bloodplay", "Ungil's Gauche", "Mightflay", "Heartbreaker", "The Consuming Dark", "Mark of the Doubting Knight", "Bino's Kitchen Knife", "Divinarius", "Song of the Sirens", "Gorebreaker", "Brightbeak", "Lavianga's Wisdom", "Flesh-Eater", "Cameria's Maul", "Mjölner", "Callinellus Malleus", "Spine of the First Claimant", "Brutus' Lead Sprinkler", "The Dark Seer", "Nycta's Lantern", "The Supreme Truth", "Death's Hand", "Doon Cuebiyari", "Mon'tregul's Grasp", "Doryani's Catalyst", "Hrimnor's Hymn", "Quecholli", "Geofri's Baptism", "Chober Chaber", "Jorrhast's Blacksteel", "Voidhome", "Marohi Erqi", "Kongor's Undying Rage", "The Princess", "The Goddess Scorned", "Lakishu's Blade", "Ephemeral Edge", "Prismatic Eclipse", "Ichimonji", "Hyaon's Fury", "Rebuke of the Vaal", "Dreamfeather", "Redbeak", "The Goddess Bound", "Aurumvorax", "Fidelitas' Spike", "Shiversting", "Edge of Madness", "Queen's Decree", "Rigvald's Charge", "Terminus Est", "Doomsower", "Oro's Sacrifice", "Pillar of the Caged God", "Realmshaper", "Dying Breath", "The Stormheart", "The Whispering Ice", "Tremor Rod", "Sire of Shards", "The Searing Touch", "Taryn's Shiver", "Agnerod East", "Agnerod North", "Agnerod South", "Hegemony's Era", "Pledge of Hands", "The Blood Thorn", "Fencoil", "Abberath's Horn", "Reverberation Rod", "Twyzel", "Midnight Bargain", "Moonsorrow", "Apep's Rage", "Piscator's Vigil", "Void Battery", "Lifesprig"
			// List_of_unique_accessories
			,"Karui Ward", "Stone of Lazhwar", "Demigod's Presence", "The Ignomon", "Talisman of the Victor", "Atziri's Foible", "Daresso's Salute", "Shaper's Seed", "Victario's Acuity", "Astramentis", "Blood of Corruption", "Carnage Heart", "Eye of Chayula", "Tear of Purity", "Ungil's Harmony", "Voll's Devotion", "Marylene's Fallacy", "The Anvil", "Warped Timepiece", "Bloodgrip", "Rashkaldor's Patience", "Araku Tiki", "Sidhebreath", "Meginord's Girdle", "Wurm's Molt", "The Magnate", "Perandus Blazon", "Belt of the Deceiver", "Bated Breath", "Prismweave", "Sunblast", "Headhunter", "Maligaro's Restraint", "Soulthirst", "Immortal Flesh", "Dyadian Dawn", "Doryani's Invitation", "Auxium", "Demigod's Bounty", "Malachai's Artifice", "Andvarius", "Dream Fragments", "Emberwake", "Mokou's Embrace", "Pyre", "Demigod's Eye", "Berek's Grip", "Berek's Pass", "Berek's Respite", "Call of the Brotherhood", "Heartbound Loop", "Kikazaru", "Tasalio's Sign", "Timeclasp", "Bloodboil", "Le Heup of All", "Gifts from Above", "Ngamahu's Sign", "Lori's Lantern", "Shavronne's Revelation", "The Taming", "Thief's Torment", "Valako's Sign", "Brinerot Mark", "Mutewind Seal", "Redblade Band", "Sibyl's Lament", "Voideye", "Death Rush", "The Pariah", "Romira's Banquet", "Ventor's Gamble", "Ming's Heart", "Blackheart", "Doedre's Damning", "Kaom's Sign", "Perandus Signet", "Asphyxia's Wrath", "Hyrri's Bite", "Blackgleam", "Rearguard", "Drillneck", "Maloney's Nightfall", "Soul Strike", "Craghead", "Broadstroke"
			// List_of_unique_armour
			, "Solaris Lorica", "Greed's Embrace", "Lioneye's Vision", "Death's Oath", "Kaom's Heart", "Bramblejack", "Briskwrap", "Ashrend", "Foxshade", "Bronn's Lithe", "The Rat Cage", "Queen of the Forest", "Hyrri's Ire", "Cloak of Flame", "Zahndethus' Cassock", "The Covenant", "Soul Mantle", "Vis Mortis", "Shavronne's Wrappings", "Infernal Mantle", "Tabula Rasa", "Belly of the Beast", "Lightning Coil", "Daresso's Defiance", "Cherrubim's Maleficence", "Voll's Protector", "Icetomb", "Ambu's Charge", "Lightbane Raiment", "Kingsguard", "Incandescent Heart", "Cloak of Defiance", "Victario's Influence", "Carcass Jack", "The Restless Ward", "Thousand Ribbons", "Atziri's Splendour", "Windscream", "Redblade Tramplers", "Kaom's Roots", "Victario's Flight", "Deerstalker", "Goldwyrm", "The Blood Dance", "Atziri's Step", "Wondertrap", "Bones of Ullr", "Shavronne's Pace", "Rainbowstride", "Steppan Eard", "Wanderlust", "Dusktoe", "Lioneye's Paws", "Mutewind Whispersteps", "Darkray Vectors", "Wake of Destruction", "Alberon's Warpath", "Gang's Momentum", "Nomic's Storm", "Sundance", "Brinerot Whalers", "Sin Trek", "Demigod's Stride", "Meginord's Vise", "Winds of Change", "Empire's Grasp", "Doryani's Fist", "Atziri's Acuity", "Lochtonial Caress", "Hrimsorrow", "Maligaro's Virtuosity", "Sadima's Touch", "Doedre's Tenure", "Asenath's Gentle Touch", "Voidbringer", "Flesh and Spirit", "Slitherpinch", "Vaal Caress", "Aurseize", "Surgebinders", "Shackles of the Wretched", "Southbound", "Null and Void", "Repentance", "Ondar's Clasp", "Facebreaker", "Shadows and Dust", "Snakebite", "Thunderfist", "Demigod's Touch", "Hrimnor's Resolve", "Abyssus", "Ezomyte Peak", "Fairgraves' Tricorne", "Heatshiver", "Rat's Nest", "Starkonja's Head", "Alpha's Howl", "Goldrim", "Asenath's Mark", "Doedre's Scorn", "Chitus' Apex", "Rime Gaze", "Scold's Bridle", "Crown of Eyes", "Ylfeban's Trickery", "Crown of Thorns", "The Peregrine", "Deidbell", "Skullhead", "Black Sun Crest", "The Bringer of Rain", "Devoto's Devotion", "Honourhome", "Veil of the Night", "Mindspiral", "Geofri's Crest", "The Broken Crown", "Malachai's Simula", "Leer Cast", "The Three Dragons", "The Gull", "Crown of the Pale King", "The Vertex", "Demigod's Triumph", "Wheel of the Stormsail", "Springleaf", "Crest of Perandus", "Demigod's Beacon", "Kaltenhalt", "Trolltimber Spire", "The Deep One's Hide", "Brinerot Flag", "Matua Tupuna", "Chalice of Horrors", "Titucius' Span", "Sentari's Answer", "Redblade Banner", "Mutewind Pennant", "Maligaro's Lens", "Great Old One's Ward", "Atziri's Mirror", "Daresso's Courage", "Saffell's Frame", "Chernobog's Pillar", "Aegis Aurora", "Thousand Teeth Temu", "Rise of the Phoenix", "Broken Faith", "Prism Guardian", "Rathpith Globe", "Jaws of Agony", "Lioneye's Remorse"
			// List_of_unique_flasks
			, "Blood of the Karui", "Doedre's Elixir", "Lavianga's Spirit", "Divination Distillate", "Taste of Hate", "Forbidden Taste", "Lion's Roar", "Atziri's Promise", "Rumi's Concoction"
			// List_of_unique_jewels
			, "Clear Mind", "Fortified Legion", "Hidden Potential", "Hotfooted", "Mantra of Flames", "Martial Artistry", "Rain of Splinters", "Spire of Stone", "Anatomical Knowledge", "Apparitions", "Army of Bones", "Brawn", "Brute Force Solution", "Careful Planning", "Cold Steel", "Efficient Training", "Eldritch Knowledge", "Energised Armour", "Energy From Within", "Fertile Mind", "Fireborn", "Fluid Motion", "Fragile Bloom", "Healthy Mind", "Inertia", "Inspired Learning", "Intuitive Leap", "Izaro's Turmoil", "Lioneye's Fall", "Malicious Intent", "Might in All Forms", "Pugilist", "Static Electricity", "Ancient Waystones", "Chill of Corruption", "Hungry Abyss", "Mutated Growth", "Self-Flagellation", "Atziri's Reign", "Blood Sacrifice", "Brittle Barrier", "Combustibles", "Corrupted Energy", "Fevered Mind", "Fragility", "Pacifism", "Powerlessness", "Sacrificial Harvest", "Vaal Sentencing", "Weight of Sin", "Assassin's Haste", "Conqueror's Efficiency", "Conqueror's Longevity", "Conqueror's Potency", "Poacher's Aim", "Survival Instincts", "Survival Secrets", "Survival Skills", "Warlord's Reach"
			// List_of_unique_maps
			, "The Coward's Trial", "Maelström of Chaos", "Mao Kun", "Vaults of Atziri", "Acton's Nightmare", "Hall of Grandmasters", "Olmec's Sanctum", "Poorjoy's Asylum", "Whakawairua Tuahu", "Oba's Cursed Trove", "Death and Taxes"
	};

	static BackendClient backendClient = new BackendClient();
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		List<String> lists = asList(
				"http://pathofexile.gamepedia.com/The_Anvil"
				);

		for (String list : lists) {
			HttpResponse<String> response = Unirest.get(list)
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0")
				.asString();
			
			Document doc = Jsoup.parse(response.getBody());
			Elements elems = doc.select("div.itembox-full");
			if(elems.size() != 1) System.err.println("A page has zero/multiple Itembox full!");
			Element element = elems.get(0);
			
			String name = element.select("strong.selflink").text();
			String base = element.select("span.itemboxheadertext a").text();
			
			System.out.println(name);
			System.out.println(base);
		}
		
		
//			String standard = "Standard";
//			String hardcore = "Hardcore";
//	        String tempsc = "Darkshrine+%28IC003%29";
//	        String temphc = "Darkshrine+HC+%28IC004%29";
//	        System.out.println(uniques.length);
//	        System.out.println("Name	Standard	Hardcore	DarkshrineSC	DarkshrineHC	poewiki");
//			for (String unique : uniques) {
//				String name = URLEncoder.encode(unique);
//				String sc = getSearchURL(standard, name);
//				String hc = getSearchURL(hardcore, name);
//				String tsc = getSearchURL(tempsc, name);
//				String thc = getSearchURL(temphc, name);
//				String poewikiurl = "http://thirdy.github.io/poewiki/index.html?page=" + (unique.replace(' ', '_'));
//				System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s", 
//						unique, sc, hc, tsc, thc, poewikiurl
//						));
//				Thread.sleep(500);
//			}
	}
	
	private static class Item {
		String name;
		String base;
		int reqlvl;
		
	}
	
	private static String getSearchURL(String league, String name) throws Exception {
		String searchPage = backendClient.post(
//	        		"league=Standard&type=&base=&name=&dmg_min=&dmg_max=&aps_min=&aps_max=&crit_min=&crit_max=&dps_min=&dps_max=&edps_min=&edps_max=&pdps_min=&pdps_max=&armour_min=&armour_max=&evasion_min=&evasion_max=&shield_min=&shield_max=&block_min=&block_max=&sockets_min=&sockets_max=&link_min=&link_max=&sockets_r=&sockets_g=&sockets_b=&sockets_w=&linked_r=&linked_g=&linked_b=&linked_w=&rlevel_min=&rlevel_max=&rstr_min=&rstr_max=&rdex_min=&rdex_max=&rint_min=&rint_max=&impl=&impl_min=&impl_max=&mods=&modexclude=&modmin=&modmax=&mods=&modexclude=&modmin=&modmax=&q_min=&q_max=&level_min=&level_max=&mapq_min=&mapq_max=&rarity=&seller=&thread=&time=2015-08-29&corrupted=&online=&buyout=&altart=&capquality=x&buyout_min=&buyout_max=&buyout_currency=&crafted=&identified="
				String.format("league=%s&type=&base=&name=%s&dmg_min=&dmg_max=&aps_min=&aps_max=&crit_min=&crit_max=&dps_min=&dps_max=&edps_min=&edps_max=&pdps_min=&pdps_max=&armour_min=&armour_max=&evasion_min=&evasion_max=&shield_min=&shield_max=&block_min=&block_max=&sockets_min=&sockets_max=&link_min=&link_max=&sockets_r=&sockets_g=&sockets_b=&sockets_w=&linked_r=&linked_g=&linked_b=&linked_w=&rlevel_min=&rlevel_max=&rstr_min=&rstr_max=&rdex_min=&rdex_max=&rint_min=&rint_max=&impl=&impl_min=&impl_max=&mod_name=&mod_min=&mod_max=&group_type=And&group_min=&group_max=&group_count=1&q_min=&q_max=&level_min=&level_max=&mapq_min=&mapq_max=&rarity=unique&seller=&thread=&identified=&corrupted=&online=x&buyout=x&altart=&capquality=x&buyout_min=&buyout_max=&buyout_currency=&crafted=",
						league, name )
				//"league=Darkshrine+%28IC003%29&type=&base=&name=tabula+rasa"
//	        		"league=Darkshrine+%28IC003%29&type=&base=&name=Lakishu%27s+Blade&dmg_min=&dmg_max=&aps_min=&aps_max=&crit_min=&crit_max=&dps_min=&dps_max=&edps_min=&edps_max=&pdps_min=&pdps_max=&armour_min=&armour_max=&evasion_min=&evasion_max=&shield_min=&shield_max=&block_min=&block_max=&sockets_min=&sockets_max=&link_min=3&link_max=&sockets_r=&sockets_g=&sockets_b=&sockets_w=&linked_r=&linked_g=&linked_b=&linked_w=&rlevel_min=&rlevel_max=&rstr_min=&rstr_max=&rdex_min=&rdex_max=&rint_min=&rint_max=&impl=&impl_min=&impl_max=&mod_name=&mod_min=&mod_max=&group_type=And&group_min=&group_max=&group_count=&mod_name=&mod_min=&mod_max=&mod_name=&mod_min=&mod_max=&group_type=And&group_min=&group_max=&group_count=&q_min=&q_max=&level_min=&level_max=&mapq_min=&mapq_max=&rarity=&seller=&thread=&identified=&corrupted=&online=x&buyout=x&altart=&capquality=x&buyout_min=&buyout_max=&buyout_currency=&crafted="
				);
		return searchPage;
	}

}
