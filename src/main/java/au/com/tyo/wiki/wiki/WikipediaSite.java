package au.com.tyo.wiki.wiki;

import java.util.ArrayList;
import java.util.HashMap;

import au.com.tyo.parser.SgmlNode;

public class WikipediaSite {
	
	private HashMap<String, WikiLang> languages = new HashMap<String, WikiLang>();
	
	private int count = 0;
	
	public class Site {
		int index;
		
		String url;
		String code;
		
		public Site(int index) {
			
		}
	}
	
	public WikipediaSite() {
		
	}
	
	public void addSite(SgmlNode langNode) {
		
		WikiLang lang = new WikiLang();
		lang.code = langNode.getAttribute("code");
		lang.name = langNode.getAttribute("name");
		lang.english = langNode.getAttribute("localname");
		
		lang.sites = new ArrayList<Site>();
		if (langNode.countChildren() > 0) {
			SgmlNode site = langNode.getChild(0);
			for (int i = 0; i < site.countChildren(); ++i) {
				SgmlNode siteNode = site.getChild(i);
				Site wikiSite = new Site(count);
				wikiSite.url = siteNode.getAttribute("url");
				wikiSite.code = siteNode.getAttribute("code");
				
				lang.sites.add(wikiSite);
			}
		}
		
		languages.put(lang.code, lang);
	}

	public String[] createLanguageList() {
		String[] langs = new String[languages.size()];
		int i = 0;
		for (WikiLang wikiLang : languages.values()) {
			if (wikiLang.index == -1)
				wikiLang.index = i++;
			langs[wikiLang.index] = wikiLang.english;
		}
		return langs;
	}
	
	public String[] createLanguageCodeList() {
		String[] codes = new String[languages.size()];
		int i = 0;
		for (WikiLang wikiLang : languages.values()) {
			codes[wikiLang.index] = wikiLang.code;
//			codes[wikiLang.index] = String.valueOf(wikiLang.index);
//			wikiLang.index = i++;
		}
		return codes;
	}
	
	public void addLang(String code, String name, String english, int index) {
		WikiLang lang = new WikiLang();
		lang.code = code;
		lang.name = name;
		lang.english = english;
		lang.index = index;
		
		languages.put(lang.code, lang);
	}
	
	public void addLang(String code, String name, String english) {
		this.addLang(code, name, english, -1);
	}

	public WikiLang getEnglishWikiLang() {
		WikiLang wikiLang = null;

		wikiLang = languages.get("en");
		if (wikiLang == null) {
			WikiLang lang = new WikiLang();
			lang.code = "en";
			lang.name = "English";
			lang.english = "English";
			lang.index = languages.size();
			languages.put("en", lang);
			wikiLang = lang;
		}
		return wikiLang;
	}
	
	public WikiLang getWikiLang(String langCode) {
		WikiLang wikiLang = languages.get(langCode);
		return wikiLang;
	}

	public String[] createNativeLanguageList() {
		String[] langs = new String[languages.size()];
		int i = 0;
		for (WikiLang wikiLang : languages.values()) {
			if (wikiLang.index == -1)
				wikiLang.index = i++;
			langs[wikiLang.index] = wikiLang.name;
		}
		return langs;
	}

	public ArrayList<PageLang> createlangLinks(String favCode, String primaryCode) {
		ArrayList<PageLang> langLinks = new ArrayList<PageLang>();
		for (WikiLang wikiLang : languages.values()) {
			PageLang pageLang = new PageLang();
			
			pageLang.langCode = wikiLang.code;
			pageLang.lang = wikiLang.english;
			pageLang.title = wikiLang.name;
			
			if (pageLang.langCode.equalsIgnoreCase(favCode)
					|| pageLang.langCode.equalsIgnoreCase(primaryCode)) 
				langLinks.add(0, pageLang);
			else 
				langLinks.add(pageLang);
		}
		return langLinks;
	}
}
