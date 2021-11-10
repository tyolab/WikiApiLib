package au.com.tyo.wiki.wiki;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.tyo.parser.Sgml;
import au.com.tyo.parser.SgmlNode;
import au.com.tyo.utils.UrlCode;
import au.com.tyo.wiki.Constants;

public class WikiParser {
	
	public static final String[] ELEMENT_FOR_SCRUB = {"<table>", "<tr>", "<td>", "<th>"};
	
	public static final Pattern[] ELEMENT_FOR_SCRUB_PATERN = {Pattern.compile("<table"), 
															Pattern.compile("<tr"), 
															Pattern.compile("<td"), 
															Pattern.compile("<th")};
	
	/**
	 * not implemented
	 * @param result
	 * @param page
	 */
	public static void parseXmlArticleText(String result, WikiPage page) {
		Sgml parser = new Sgml();
		SgmlNode rootNode = parser.parse(result.getBytes(), 0);
		if (rootNode != null) {
//			rootNode.parseJSON();
			for (int i= 0; i < rootNode.countChildren(); ++i) {
				SgmlNode child = rootNode.getChild(i);
				if (child != null) {
					if (child.getAttribute("selected") != null && child.getAttribute("selected").length() > 0) {
//						lang = child.getText();
					}
					else {
						PageLang pageLang = new PageLang();
						pageLang.url = child.getAttribute("value");
						

//						if (pageLang.url.startsWith("//"))
							pageLang.url = WikiApi.getInstance().getApiConfig().completeWikiLink(pageLang.url); //.getProtocol() + pageLang.url;
						
						pageLang.lang = child.getText();			
						pageLang.title = new String(UrlCode.decode(WikiApiConfig.linkToTitle(pageLang.url).getBytes())).trim();
						
						if (pageLang.title.length() == 0)
							continue;

//						langs.add(pageLang);
					}
				}
			}
		}
	}
	
	public static String scrubInlineStyles(String text) {
		int count = 0;
		String tmp = text;
		for (Pattern patern : ELEMENT_FOR_SCRUB_PATERN) {
			Matcher matcher;
			if ((matcher = patern.matcher(tmp)).find()) {
				matcher.start();
				tmp = matcher.replaceAll(ELEMENT_FOR_SCRUB[count]);
			}
			++count;
		}
		return tmp;
	}
	
	public static void parseJsonArticleText(String result, WikiPage page) throws Exception {
		parseJsonArticleText(result, page, -1);
	}
	
	public static void parseJsonArticleText(String result, WikiPage page, int howManySectionsToParse/*, boolean fastMode*/) throws Exception {
		if (result == null || result.length() == 0)
			return;
		
        JSONObject array = new JSONObject(result);
        Request request = page.getRequest();
        
        if (array != null) {
        	if (array.has("mobileview")) {
        		try {
        			parseMobileArticle(array, page, howManySectionsToParse);
        		}
        		catch (Exception ex) {
        			request.setResponseCode(Constants.STATUS_PAGE_ERROR_PARSED_JSON_MOBILEVIEW);
        			throw ex;
        		}
	        }
        	if (array.has("article")) {
        		try {
        			parseJsonArticle("article", array, page, howManySectionsToParse);
        		}
        		catch (Exception ex) {
        			request.setResponseCode(Constants.STATUS_PAGE_ERROR_PARSED_JSON_MOBILEVIEW);
        			throw ex;
        		}
	        }			
	        else if (array != null && array.has("parseJSON")) {
	        	try {
	        		parseArticle(array, page);
	        	}
	        	catch (Exception ex) {
	        		request.setResponseCode(Constants.STATUS_PAGE_ERROR_PARSED_JSON);
	        		throw ex;
	        	}
	        }
	        else {
	        	request.setResponseCode(Constants.STATUS_PAGE_ERROR);
	        }
        }
        else {
        	request.setResponseCode(Constants.STATUS_PAGE_NOT_FOUND);
        }
	}
	
	private static void parseArticle(JSONObject array, WikiPage page) throws Exception {
        JSONObject parsedObject = array.getJSONObject("parseJSON"); //getJSONArray();
		
        if (parsedObject != null) {
        	// it is parsed article page in html format
        	page.setTextFormat(WikiPage.TEXT_FORMAT_HTML);
        	
        	parseArticleCommon(parsedObject, page);
        	
        	JSONObject articleArray = parsedObject.getJSONObject("text");
        	
        	String article = articleArray.getString("*");
        	page.setText(article);
        }
	}
	
	private static void parseArticleCommon(JSONObject parsedObject, WikiPage page) throws Exception {
    	/*
    	 * 1. Title
    	 */
        String newTitle = "";
        if (parsedObject.has("normalizedtitle")) {
        	newTitle = parsedObject.getString("normalizedtitle");

			if (newTitle.length() > 0 && !page.getTitle().equals(newTitle)) {
				page.setRedirectFrom(page.getTitle());
			}

			page.setTitle(newTitle);
        }

        if (parsedObject.has("redirected")) {
        	String redirected = parsedObject.getString("redirected");

        	if (null != page.getRedirectFrom() && page.getRedirectFrom().length() > 0)
        		page.addRedirect(page.getRedirectFrom());

			page.setRedirectFrom(redirected);
        }	        

        /*
         * 2. Thumbnail info
         */
        if (parsedObject.has("thumb")) {
        	try {
	        	JSONObject thumbObj = parsedObject.getJSONObject("thumb");
	        	String url = thumbObj.getString("url");
	        	page.setThumbnailLink(WikiApi.getInstance().getApiConfig().completeWikiLink(url, page.getDomain()));
        	}
        	catch (Exception ex) {}
        }
        
        /*
         *  3. last modified
         */
        if (parsedObject.has("lastmodified")) {
        	try {
        		page.setLastUpdateDate(parsedObject.getString("lastmodified"));
        	}
        	catch (Exception ex) {}
        }
        
        /*
         *  4.  last modified
         */
        if (parsedObject.has("lastmodifiedby")) {
        	try {
        		page.setLastUpdateBy(parsedObject.getString("lastmodifiedby"));
        	}
        	catch (Exception ex) {}
        }
        /*
         *  5. Page Id 
         */
        if (parsedObject.has("pageid")) {
        	try {
        		page.setPageId(parsedObject.getInt("pageid"));
        	}
        	catch (Exception ex) {}
        }
        if (parsedObject.has("redirects")) {
        	try {
        		JSONArray redirects = parsedObject.getJSONArray("redirects");
        		if (redirects.length() > 0) 
   	        	 for (int i = 0; i < redirects.length(); i++) {
   	        		 String redirect = redirects.getString(i);
   	        		 page.addRedirect(redirect);
   	        	 }
//        		page.setId(Integer.parseInt(parsedObject.getString()));
        	}
        	catch (Exception ex) {}
        }
	}
	
	private static void parseMobileArticle(JSONObject array, WikiPage page, int howManySectionsToParse) throws Exception {
        parseJsonArticle("mobileview", array, page, howManySectionsToParse); //getJSONArray();
	}

	private static void parseJsonArticle(String jsonKey, JSONObject array, WikiPage page, int howManySectionsToParse) throws Exception {
        JSONObject parsedObject = array.getJSONObject(jsonKey); //getJSONArray();
		
		int offset = 0;
		int lowestLevel = 0;
		
        if (parsedObject != null) {
        	
        	parseArticleCommon(parsedObject, page);
	        
	        
	        /*
	         *  5. Sections Info
	         */
        	 JSONArray sections =  parsedObject.getJSONArray("sections");
        	 WikiPageSection preSection = null;
        	 WikiPageSection firstSection = null;
        	 int sectionCount = 0;
        	 
        	 if (page.countParsedSections() > 0) {
//        		 page.clearSections();
        		 preSection = page.getSection(page.countParsedSections() - 1);
        		 if (preSection != null)
        			 lowestLevel = preSection.getLevel();
        		 firstSection = page.getSection(0);	        		 
        		 sectionCount = page.countParsedSections();
        	 }
        	 
        	 if (sections.length() > 0) {
	        	 for (int i = sectionCount; i < sections.length(); i++) {
	        		 JSONObject section = sections.getJSONObject(i);
	        		 
	        		 WikiPageSection pageSection = new WikiPageSection();
		        	 boolean updateMe = true;
	        		 
	        		 int id = section.has("id") ? section.getInt("id") : -1;
					 String title = section.has("line") ? section.getString("line") : "";
					 int level = section.has("level") ? section.getInt("level") : -1;
					 if (level == -1 && section.has("toclevel"))
						 level = section.getInt("toclevel");
					 
					 String text = section.has("text") ? section.getString("text") : "";
//					 text= scrubInlineStyles(text);
					 
						/*
						 * DEBUG
						 */
//							if (id == 11)
//								System.out.println("Stop here");
					 
					if (id > -1) {
		        		 pageSection.setId(id);
		        		 pageSection.setTitle(title);
		        		 pageSection.setLevel(level);
		        		 pageSection.setText(text);
		        		 
		        		 if (level > 0) {
			        		 if (firstSection == null) {
			        			 firstSection = pageSection;
//									 firstSection.setIdName("" + (page.countParsedSections() + 1));
			        		 }
			        		 
//			        		 page.addSection(pageSection);
			        		 
			        		 if (id == 0 && (title == null || title.length() == 0)) {
//				        			 pageSection.setTitle("Abstract");
			        			 pageSection.setOffset(0);
			        		 }
		        		 		 
							if (level < lowestLevel || lowestLevel == 0)
								lowestLevel = level;
							
							offset = (level - lowestLevel);
							pageSection.setOffset(offset);
							
							if (preSection != null) {
								int preLevel = preSection.getLevel();
								if (level > preLevel) {
									WikiPageSection.buildUpRelation(preSection, pageSection);
								}
								else if (preLevel == level) {
//										preSection.setNext(pageSection);
									if (level == lowestLevel)
										page.addSection(pageSection);
									else 
										WikiPageSection.buildUpRelation(preSection.getParent(), pageSection);						
								}
								else {
									/*
									 * parent or grand-parent's sibling 
									 */
									WikiPageSection temp = preSection.getParent();
									while (temp != null && temp.getLevel() > level)
										 temp = temp.getParent();
									
									/*
									 * only here, we need to reset the preSection differently 
									 */
									if (temp == null || (temp != null && temp.getParent() == null)) {
										if (temp != null && temp.getLevel() != level)
											WikiPageSection.buildUpRelation(temp, pageSection);
										else
											page.addSection(pageSection);
//											preSection = page.getSection(page.countParsedSections() - 1);
									}
									else {
										WikiPageSection.buildUpRelation(temp.getParent(), pageSection);
										preSection = temp;
										updateMe = false;
									}
								}
							}
							else
								page.addSection(pageSection);
						}  // level > 0
						else {
							page.addSection(pageSection);	
							updateMe = false;
						}
							
						pageSection.setupIdName();
						if (updateMe)
							preSection = pageSection;
					} // id > -1

					/*
					 * because JSON parsing is just too slow, get the first two sections then we get out here
					 */
		        	 page.setSectionCount(i + 1);
		        	 
					if (howManySectionsToParse> 0 && page.countParsedSections() >= howManySectionsToParse)
						break;
				}
        	}
        	 
        	 /*
        	  * 7. Image
        	  */
		        if (parsedObject.has("image")) {
		        	try {
			        	JSONObject thumbObj = parsedObject.optJSONObject("image");
			        	if (null != thumbObj) {
				        	String name = thumbObj.getString("file");
				        	page.addImageInfo(name);
			        	}
			        	else {
			        		JSONArray imageArray = parsedObject.optJSONArray("image");
			        		if (null != imageArray) {
			        			thumbObj = imageArray.optJSONObject(0);
			        			if (null != thumbObj) {
				        			String url = thumbObj.getString("url");
						        	page.addImageInfo(url);
			        			}
			        		}
			        	}
		        	}
		        	catch (JSONException ex) {
		        		
		        	}
		        }
        }
	}

	public static void parseJsonQueryText(String result, WikiPage page) throws JSONException {
        JSONObject array = new JSONObject(result);
        if (array != null) {
        	try {
		        JSONObject parsedObject = array.getJSONObject("parseJSON"); //getJSONArray();
		        
		        if (parsedObject != null) {
		        	//JSONObject infoObject ;
		        	String title =  parsedObject.getString("title");
		        	page.setTitle(title);
		        	
		        	JSONObject textArray = parsedObject.getJSONObject("text");
		        	if (textArray != null) {
			        	String text = textArray.getString("*");
			        	page.setText(text);
		        	}
	
		        }
        	}
	        catch (JSONException ex) {
	        	Error error = new Error();
		        JSONObject errorObject = array.getJSONObject("error");; //getJSONArray();
		        
		        String info = "";
		        String code;
		        if (errorObject != null) {
		        	//JSONObject infoObject ;
		        	info =  errorObject.getString("info");
		        	code = errorObject.getString("code");
		        	
		        	error.setCode(code);
		        	error.setInfo(info);
		        }
		        page.setError(error);
		        page.setText(info);
	        }
        }
	}
	
	public WikiSearch getFirstSearchResult(String result, String domain) throws JSONException {
		List hits = parseJsonSearchResult(result, domain, 1);
		if (hits!= null && hits.size() > 0)
			return (WikiSearch) hits.get(0);
		return null;
	}
	
	public List parseJsonSearchResult(String result, String domain) throws JSONException {
		return parseJsonSearchResult(result, domain, -1);
	}
	
	public List parseJsonSearchResult(String result, String domain, int count) throws JSONException {
		List resultList = new ArrayList<WikiSearch>();
		int cnt = 0;
		if (result.length() > 0)
		    try {
		        // Create a JSON object hierarchy from the inputs
		        JSONObject array = new JSONObject(result);
		        if (array.length() > 0) {
		        	JSONObject search = array.getJSONObject("query"); //.getJSONObject(1);
			        JSONArray hitsJsonArray = search.getJSONArray("search"); // getJSONArray(0); //getJSONArray();
			        
			        // Extract the Place descriptions from the inputs
	//		        resultList = new ArrayList<WikiSearch>(hitsJsonArray.length());
			        for (int i = 0; i < hitsJsonArray.length(); i++) {
			        	JSONObject info = hitsJsonArray.getJSONObject(i);
			        	if (count > -1 && cnt >= count)
			        		break;
			        	
			        	String title = info.getString("title");
			             	
			        	if (title != null && title.length() > 0) {
			        		WikiSearch ws = newWikiSearchInstance(title);
			        		ws.setSnippet(info.getString("snippet"));
			        		ws.setDomain(domain);
			        		resultList.add(ws); //getJSONObject(i).toString());
			        		++cnt;
			        	}
			        }
		        }
		    } catch (JSONException e) {
		       throw e;
		    }
	    
	    return resultList;
	}

	protected WikiSearch newWikiSearchInstance(String title) {
		return new WikiSearch(title);
	}

	public ArrayList<PageLang> parseLanguageLinks(SgmlNode languageSelectionNode) {
//		WikiLang wikiLang = new WikiLang();
		ArrayList<PageLang> pageLangs = new ArrayList<PageLang>();
		
		if (languageSelectionNode != null) {
			languageSelectionNode.parse();
			for (int i= 0; i < languageSelectionNode.countChildren(); ++i) {
				SgmlNode child = languageSelectionNode.getChild(i);
				if (child != null) {
					if (child.getAttribute("selected") != null && child.getAttribute("selected").length() > 0) {
//						wikiLang.setCode(child.getText());
					}
					else {
						PageLang pageLang = new PageLang();
						pageLang.url = child.getAttribute("value");
						

						if (pageLang.url.startsWith("//"))
							pageLang.url = WikiApi.getInstance().getApiConfig().getProtocol() + pageLang.url;
						
						pageLang.lang = child.getText();			
						pageLang.title = new String(UrlCode.decode(WikiApiConfig.linkToTitle(pageLang.url).getBytes())).trim();
						
						if (pageLang.title.length() == 0)
							continue;

						pageLangs.add(pageLang);
					}
				}
			}
		}
		return pageLangs;
	}
	
	public static ArrayList<String> extractAnchors(String rootPath, String text) {
		ArrayList<String> list = new ArrayList<String>();
		String rootPathOnlyPattern = rootPath + ".*?\"";
		Pattern mathUrlPattern = Pattern.compile(rootPathOnlyPattern);
		Matcher matcher = mathUrlPattern.matcher(text);
		while (matcher.find()) {
			String url = text.substring(matcher.start(), matcher.end() - 2);
			String title = WikiApiConfig.linkToTitle(url);
			if (title != null && title.length() > 0 && title.indexOf(':') < 0)
				list.add(title);
		}
		return list;
	}
}
