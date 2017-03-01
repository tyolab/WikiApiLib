package au.com.tyo.wiki.wiki;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import au.com.tyo.wiki.Status;
import au.com.tyo.wiki.wiki.api.MobileView;

public class Request implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1003226985810710514L;
	
	public static final int QUERY_TYPE_SEARCH = 0;
	public static final int QUERY_TYPE_URL = 2;
	public static final int QUERY_TYPE_MAIN_PAGE = 4; // still url but the url for the main page
	public static final int QUERY_TYPE_TITLE = 8;  // get artile with title
	public static final int QUERY_TYPE_ABSTRACT = 16;
	public static final int QUERY_TYPE_LOCAL = 32;
	public static final int QUERY_TYPE_HISTORY = 64;
	
	public static final int FROM_NOTHING = 0;
	public static final int FROM_OPENSEARCH = 1;
	public static final int FROM_LANG_LINK = 3;
	public static final int FROM_RELATED = 4;
	public static final int FROM_HISTORY = 5;
	public static final int FROM_SEARCH_RESULT = 6;
	public static final int FROM_VOICE_SEARCH = 7;
	public static final int FROM_SEARCH_BUTTON = 8;
	public static final int FROM_SEARCH_REQUEST = 2;
	
	public static final int FROM_FEATURED = 9;
	public static final int FROM_RANDOM_LOCAL = 10;
	public static final int FROM_RANDOM_WWW = 13;
	public static final int FROM_RANDOM_SEARCH_BUTTON = 11;
	public static final int FROM_RANDOM_MENU = 12;
	
	private String url;
	
	private String query;
	
	private String rawQuery;
	
	private int type;  // what kind of request, search? title? 
	
	private String anchor;
	
//	private String langCode;
	
	private Vector<String> wikiDomains;
	
	private String sections;  // which sections needed, 1|2|3, .. or all
	
	private int responseCode;
	
	private WikiPage page;
	
	private int fromType;
	
	private boolean toCrosslink;
	
	private List<WikiSearch> results;
	
	public Request() {
		url = null;
		query = "";
		rawQuery = "";
		type = QUERY_TYPE_SEARCH;
		setToCrosslink(false);

		init();
	}
	
	public Request(String url, String query, int type) {
		this.url = url;
		this.query = query;
		this.type = type;
		
		init();
	}

	public Request(int queryType) {
		this.type = queryType;
		query = "";
		url = null;
		wikiDomains = null;
		
		init();
	}

	private void init() {
		fromType = FROM_NOTHING;
		wikiDomains = null;
		setSections(MobileView.SECTION_ALL);
		page = null;
		responseCode = 404;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getRawQuery() {
		return rawQuery;
	}

	public void setRawQuery(String rawQuery) {
		this.rawQuery = rawQuery;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAnchor() {
		return anchor;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}

//	public String getLangCode() {
//		return langCode;
//	}
//
//	public void setLangCode(String langCode) {
//		this.langCode = langCode;
//	}
	
	public void setWikiDomains(Vector<String> domains) {
		this.wikiDomains = domains;
	}
	
	public Vector<String> getWikiDomains() {
		return wikiDomains;
	}

	public String getSections() {
		return sections;
	}

	public void setSections(String sections) {
		this.sections = sections;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public int getFromType() {
		return fromType;
	}

	public void setFromType(int fromType) {
		this.fromType = fromType;
	}

	public WikiPage getPage() {
		return page;
	}

	public void setPage(WikiPage page) {
		this.page = page;
	}

	public boolean isToCrosslink() {
		return toCrosslink;
	}

	public void setToCrosslink(boolean toCrosslink) {
		this.toCrosslink = toCrosslink;
	}

	public List<WikiSearch> getResults() {
		return results;
	}

	public void setResults(List<WikiSearch> results) {
		this.results = results;
	}
	
	public boolean succeeded() {
		return Status.isStatusAcceptable(responseCode);
	}
}
