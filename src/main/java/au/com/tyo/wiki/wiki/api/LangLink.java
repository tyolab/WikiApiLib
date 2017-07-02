package au.com.tyo.wiki.wiki.api;

import java.util.ArrayList;
import java.util.List;

import au.com.tyo.parser.Sgml;
import au.com.tyo.parser.SgmlNode;
import au.com.tyo.utils.StringUtils;
import au.com.tyo.wiki.wiki.PageLang;
import au.com.tyo.wiki.wiki.WikiApi;
import au.com.tyo.wiki.wiki.WikiLang;
import au.com.tyo.wiki.wiki.WikipediaSite;

/*
 * * prop=langlinks (ll) *
  Returns all interlanguage links from the given page(s)
  https://www.mediawiki.org/wiki/API:Properties#langlinks_.2F_ll

This module requires read rights
Parameters:
  lllimit             - How many langlinks to return
                        No more than 500 (5000 for bots) allowed
                        Default: 10
  llcontinue          - When more results are available, use this to continue
  llurl               - DEPRECATED! Whether to get the full URL (Cannot be used with llprop)
  llprop              - Which additional properties to get for each interlanguage link
                         url      - Adds the full URL
                         langname - Adds the localised language name (best effort, use CLDR extension)
                                    Use llinlanguagecode to control the language
                         autonym  - Adds the native language name
                        Values (separate with '|'): url, langname, autonym
  lllang              - Language code
  lltitle             - Link to search for. Must be used with lllang
  lldir               - The direction in which to list
                        One value: ascending, descending
                        Default: ascending
  llinlanguagecode    - Language code for localised language names
                        Default: en
Example:
  Get interlanguage links from the [[Main Page]]:
    api.php?action=query&prop=langlinks&titles=Main%20Page&redirects=
    
 * For example,
 * 
 * http://zh.wikipedia.org/w/api.php?action=query&prop=langlinks&titles=%E6%9F%A0%E6%AA%AC&redirects=&lllang=zh-yue
 * 
 */
public class LangLink extends ApiQuery {
	
	public LangLink() {
		super();
		
		this.addProp("langlinks");
		this.setFormat("xml");
	}

	private void addTargetLangVariable(String targetLangCode) {
		this.addVariableAttribute("lllang", targetLangCode, true);
	}

	public String getCrossLangLinkUrl(String title, String targetLangCode) {
		this.addTitlesVariable(title);
		this.addTargetLangVariable(targetLangCode);
		
		return getUrl();
	}

	public static List<PageLang> parseLangLinks(String result, String favCode, String primaryCode, WikipediaSite wikipedias) {
		return parseLangLinks(result, favCode, primaryCode, true, wikipedias);
	}

	public static List<PageLang> parseLangLinks(String result, String favCode, String primaryCode, boolean ignoreEmptyTitle, WikipediaSite wikipedias) {
		ArrayList<PageLang> langs = new ArrayList<PageLang>();

		if (null != result) {
			SgmlNode languageSelectionNode = new Sgml().parse(result);

			if (languageSelectionNode != null && languageSelectionNode.getName().equals("api") && languageSelectionNode.countChildren() > 0) {
//			languageSelectionNode.parseJSON();
				SgmlNode decendant = languageSelectionNode.path("query/pages/page/langlinks"); // languageSelectionNode.getDecendant(3, 0);
				if (decendant != null) {
					int count = decendant.countChildren();
					for (int i = 0; i < count; ++i) {
						SgmlNode child = decendant.getChild(i);
						//				if (child != null) {
						//					if (child.getAttribute("selected") != null && child.getAttribute("selected").length() > 0) {
						//						lang = child.getText();
						//					}
						//					else {

						PageLang pageLang = new PageLang();
						//						pageLang.url = child.getAttribute("value");
						//
						//
						//						if (pageLang.url.startsWith("//"))
						//							pageLang.url = WikiApi.getInstance().getApiConfig().getProtocol() + pageLang.url;
						String lang = child.getAttribute("lang");
						pageLang.setLangCode(lang);
						WikiLang wikiLang = wikipedias.getWikiLang(lang);
						if (wikiLang != null)
							lang = wikiLang.getName();
						pageLang.setLang(lang);

						pageLang.setTitle(StringUtils.unescapeHtml(child.getText()));    //new String(UrlCode.decode(WikiApiConfig.linkToTitle(pageLang.url).getBytes())).trim();

						if (pageLang.getTitle().length() == 0 && ignoreEmptyTitle)
							continue;
						else
							pageLang.setUrl(WikiApi.getInstance().getApiConfig().buildBaseUrl(pageLang.getLangCode()));

						if (pageLang.getLangCode() == null)
							System.err.println("Something really wrong here");

						if (pageLang.getLangCode().equalsIgnoreCase(favCode)
								|| pageLang.getLangCode().equalsIgnoreCase(primaryCode))
							langs.add(0, pageLang);
						else
							langs.add(pageLang);
						//					}
						//				}
					}
				}
			}
		}
		return langs;
	}
}
