package au.com.tyo.wiki.wiki;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import au.com.tyo.common.feed.Feed;
import au.com.tyo.io.IO;
import au.com.tyo.services.HttpConnection;
import au.com.tyo.services.HttpConnection.HttpRequest;
import au.com.tyo.services.HttpConnection.Parameter;
import au.com.tyo.services.HttpJavaNet;
import au.com.tyo.services.HttpPool;
import au.com.tyo.services.HttpRequestListener;
import au.com.tyo.wiki.WikiSettings;
import au.com.tyo.wiki.wiki.WikiApiConfig.Format;
import au.com.tyo.wiki.wiki.api.ApiQuery;
import au.com.tyo.wiki.wiki.api.Edit;
import au.com.tyo.wiki.wiki.api.FeaturedFeed;
import au.com.tyo.wiki.wiki.api.ImageUrl;
import au.com.tyo.wiki.wiki.api.Images;
import au.com.tyo.wiki.wiki.api.Import;
import au.com.tyo.wiki.wiki.api.LangLink;
import au.com.tyo.wiki.wiki.api.ListRandom;
import au.com.tyo.wiki.wiki.api.Login;
import au.com.tyo.wiki.wiki.api.MobileView;

public class WikiApi {
	
	private static final String VERSION = "2.0.0";
	
	public static final String USER_AGENT = "TyokiWikiApi/" + VERSION + " (http://tyo.com.au/)";
	
	private static WikiApi instance;
	
//	private static Http connection; // keep-alive
	private WikiApiConfig apiConfig;
	
//	private String userAgent;
	
	private boolean isMobileDevice;
	private String version;
	
	private WikipediaFamily wikipedias;
	
	private Login login;
	
	private static String cachePath = ".";

	private WikiParser parser;
	
	public static void setCachePath(String path) {
		cachePath = path;
	}
	
	public WikiApi() {
		init(new WikiSettings());
	}
	
	public WikiApi(WikiSettings settings) {
		init(settings);
	}
	
	public WikiApi(String site, WikiSettings settings) {
		init(settings);
		apiConfig.setSite(site);
	}

	private void init(WikiSettings settings) {
		
		apiConfig = new WikiApiConfig(settings);
		version = VERSION;

		// set the default parser
		parser = new WikiParser();
	}

	public static WikiApi getInstance() {
		if (instance == null)
			instance = new WikiApi();
		return instance;
	}

	public void setParser(WikiParser parser) {
		this.parser = parser;
	}

	public static void initialize(WikiSettings settings) {
		instance = new WikiApi(settings);
	}

	public void setWikipedias(WikipediaFamily wikipedias) {
		this.wikipedias = wikipedias;
	}

	public WikiApiConfig getApiConfig() {
		return apiConfig;
	}

	public void setApiConfig(WikiApiConfig apiConfig) {
		this.apiConfig = apiConfig;
	}
	
	/**
	 * 
	 * @param agent
	 */
	public static void setUserAgent(String agent) {
		HttpJavaNet.setUserAgent(agent);
	}
	
	public static String getUserAgent() {
//		if (userAgent == null) {
//			userAgent = String.format(USER_AGENT, version);
//			if (isMobileDevice)
//				userAgent = "Mobile " + userAgent;
//		}
		return HttpJavaNet.getUserAgent();
	}
	
	public WikiPage getWikiPageWithTitle(String baseUrl, WikiPage page,
			HttpRequestListener caller) throws Exception {
		String url = apiConfig.buildQueryUrlWithBaseUrl(baseUrl, page.getTitle());
		return getUrl(url, page, caller, baseUrl);
	}
	
	public WikiPage getWikiPage(String query, WikiPage page, HttpRequestListener caller) throws Exception {
		return getWikiPage(apiConfig.getSubdomain(), query, page, caller, apiConfig.buildBaseUrl());
	}

	public WikiPage getWikiPage(String domain, String query, WikiPage page, HttpRequestListener caller, String baseUrl) throws Exception {
		String url = apiConfig.buildQueryUrl(domain, query);
		return getUrl(url, page, caller, baseUrl);
	}
	
	public WikiPage getUrl(String url, WikiPage page, HttpRequestListener caller) throws Exception {
		return getUrl(url, page, caller, apiConfig.buildBaseUrl(page.getDomain()));
	}
			
	public WikiPage getUrl(String url, WikiPage page, HttpRequestListener caller, String baseUrl) throws Exception {
//		String url = apiConfig.buildBaseUrl(query);
//		String lang = WikiApiConfig.linkToDomain(url);
		HttpConnection connection = HttpPool.getInstance().getConnection();
		connection.setCaller(caller);
		
//		apiConfig.setDomain(lang);
		String html = getUrlText(url, connection);
		int responseCode = connection.getResponseCode();
		page.getRequest().setResponseCode(responseCode);
		page.setText(html);
//		getPage(html, url, page, responseCode, apiConfig.getBaseUrl());
		page.setBaseUrl(baseUrl);
//		page.setDomain(apiConfig.getDomain());
		return page;
	}
	
	public WikiPage getMainPage() throws Exception {
		return getMainPage(apiConfig.getSubdomain(), null);
	}
	
	public WikiPage getMainPage(String langCode, HttpRequestListener caller) throws Exception {
		String url = apiConfig.buildMainPageUrl(langCode);
		String baseUrl = apiConfig.buildBaseUrl(langCode);
		HttpConnection connection = HttpPool.getInstance().getConnection();
		connection.setCaller(caller);
		
		WikiPage page = new WikiPage("Main Page");
		page.setText(getUrlText(url, connection));
		page.getRequest().setResponseCode(connection.getResponseCode());
		page.setBaseUrl(baseUrl);
		page.setUrl(connection.getUrl());
		return page;
	}
	
	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public String getSearchJson(String query) throws Exception {
		return getSearchJson(query, apiConfig.getSubdomain(), WikiApiConfig.SEARCH_TITLE, 0, 1);
	}
	
	public String getSearchJson(String query, String langCode, int searchType, int offset, int limit) throws Exception {
		String url;
		if (searchType == WikiApiConfig.SEARCH_TITLE)
			url = apiConfig.buildSearchUrl(query, langCode);
		else
			url = apiConfig.buildSearchApiUrl(query, langCode, searchType, offset, limit);
		HttpConnection connection = HttpPool.getInstance().getConnection();
		
		return getUrlText(url, connection, null, 0);
	}
	
	public List search(String query, int searchType) throws Exception {
		return search(query, apiConfig.getSubdomain(), searchType);
	}
	
	public List search(String query, String langCode, int searchType) throws Exception {
		String result = getSearchJson(query, langCode, searchType, 0, 1);
		return parser.parseJsonSearchResult(result, langCode);
	}
	
	
	public String getFirstRandomPageName(String targetDomain) throws Exception {
		List<String> list = getRandomPages(targetDomain, 1);
		return list.size() == 0 ? null : list.get(0);
	}
	
	public List<String> getRandomPages(String targetDomain, int number) throws Exception {
		String url = apiConfig.buildRandomPageRetrievalUrl(targetDomain, number);
        HttpConnection connection = HttpPool.getInstance().getConnection();
		String result = getUrlText(url, connection);
		return ListRandom.parseResult(result);
	}
	
	public WikiSearch getFirstSearchResult(String query) throws Exception {
		return getFirstSearchResult(query, apiConfig.getSubdomain());
	}
	
	public WikiSearch getFirstSearchResult(String query, String langCode) throws Exception {
		String url = apiConfig.buildSearchUrl(query, langCode);
		HttpConnection connection = HttpPool.getInstance().getConnection();
		
		String result = getUrlText(url, connection);
		return parser.getFirstSearchResult(result, langCode);
	}

	public WikiPage lookup(String query) throws Exception {
		WikiPage page = new WikiPage();
		lookup(query, apiConfig.getSubdomain(), null, page);
		return page;
	}
	
	public String lookup(String query, String langCode, HttpRequestListener caller, WikiPage page) throws Exception {
		String url = apiConfig.buildSearchUrl(query, langCode);
		String baseUrl = apiConfig.buildBaseUrl(langCode);
		HttpConnection connection = HttpPool.getInstance().getConnection();
		connection.setCaller(caller);
		
		String result = getUrlText(url, connection);

		if (page != null) { 
			page.getRequest().setResponseCode(connection.getResponseCode());
			page.setBaseUrl(baseUrl);
			page.setUrl(connection.getUrl());
		}
		return result;
	}
	
	public String getUrlText(String url, long lastModifiedDate) throws Exception {
		return getUrlText(url, HttpPool.getInstance().getConnection(), null, lastModifiedDate);
	}

	public String getUrlText(String url, HttpConnection connection) throws Exception {
		return  getUrlText(url, connection, apiConfig.getFormat(), 0);
	}
	
	public String getUrlText(String url, HttpConnection connection, long lastModifiedDate) throws Exception {
		return  getUrlText(url, connection, apiConfig.getFormat(), lastModifiedDate);
	}
			
	public String getUrlText(String url, HttpConnection connection, Format format, long lastModifiedDate) throws Exception {
		String formattedUrl;
		String text;
		
		if (format != null) {
			formattedUrl = WikiApiConfig.appendFormatRequests(url, format);
			text = connection.get(formattedUrl, lastModifiedDate, true);
		}
		else {
			text = connection.get(url, lastModifiedDate, true);
		}
		return text;
	}
	
	public void getAbstract(String query, WikiPage page) throws Exception {
		String result = getArticle(query, 0);
		parser.parseJsonQueryText(result, page);
	}
	
	public String getArticle(String query) throws Exception {
		return getArticle(query, -1);
	}	
	
	public String getArticle(String query, int section) throws Exception {
		// {"servedby":"srv214","error":{"code":"missingtitle","info":"The page you specified doesn't exist"}}
		// {"parseJSON":{"title":"Baseball","text":{"*":"
		String firstSectionUrl = apiConfig.buildSectionRetrievalUrl(query, section);
		HttpConnection connection = HttpPool.getInstance().getConnection();
		String result = connection.get(firstSectionUrl);
		return result;
	}
	
	public void getAbstractWithMobileViewForPage(String query, WikiPage page, String domain, String areaCode) throws Exception {
		getParsedArticleWithMobileViewForPage(query, "0|1", page, domain, areaCode, 2);
	}
	
	public void getParsedArticleWithMobileViewForPage(String query, WikiPage page, String domain, String areaCode) throws Exception {
		getParsedArticleWithMobileViewForPage(query, MobileView.SECTION_ALL, page, domain, areaCode, -1);
	}
	
	public void getParsedArticleWithMobileViewForPage(String query, String sections, WikiPage page, String domain, String areaCode, int secNumberToParse) throws Exception {
		String article = getArticleWithMobileView(query, sections, page, domain, areaCode);
		page.setText(article);
		parser.parseJsonArticleText(article, page, secNumberToParse);
	}
	
	public String getArticleWithMobileView(String query, String domain, String areaCode) throws Exception {
		return getArticleWithMobileView(query, MobileView.SECTION_ALL, domain, areaCode);
	}
	
	public String getArticleWithMobileView(String query, String sections, String domain, String areaCode) throws Exception {
		return getArticleWithMobileView(query, sections, null, domain, areaCode);
	}
	
	public String getArticleWithMobileView(String query, WikiPage page, String domain, String areaCode) throws Exception {
		return getArticleWithMobileView(query, MobileView.SECTION_ALL, page, domain, areaCode); // // too slow to load and parseJSON for now
	}
	
	/**
	 * 
	 * @param query
	 * @param sections separated by "|" , for example 1|2|3|4|5|6|7|8|9|10|11|12 
	 * @param page 
	 * @return
	 * @throws Exception 
	 */
	public String getArticleWithMobileView(String query, String sections, WikiPage page, String domain, String areaCode) throws Exception {
		String firstSectionUrl = apiConfig.buildArticleRetrieval4MobileViewUrl(domain, query, sections, areaCode);
		HttpConnection connection = HttpPool.getInstance().getConnection();
		String result = connection.get(firstSectionUrl);
		if (page != null) {
			page.getRequest().setResponseCode(connection.getResponseCode());
			page.setBaseUrl(apiConfig.buildBaseUrl(domain));
			page.setUrl(connection.getUrl());
		}
		return result;
	}
	
	public List hints1(String input, String domain) throws Exception {
	    String openSearchUrl = apiConfig.buildOpenSearchUrl(input, domain);
		HttpConnection connection = HttpPool.getInstance().getConnection();
	    String result = getUrlText(openSearchUrl, connection, null, 0);
	    
		List resultList = null;
	    try {
	        // Create a JSON object hierarchy from the inputs
	        JSONArray array = new JSONArray(result);
	        if (array.length() > 1) {
		        JSONArray predsJsonArray = array.getJSONArray(1); //getJSONArray();
		        
		        // Extract the Place descriptions from the inputs
		        resultList = new ArrayList<WikiSearch>(predsJsonArray.length());
		        for (int i = 0; i < predsJsonArray.length(); i++) {
		        	WikiSearch ws = parser.newWikiSearchInstance(predsJsonArray.getString(i));
		        	ws.setDomain(domain);
		            resultList.add(ws); //getJSONObject(i).toString());
		        }
	        }
	    } catch (JSONException e) {
	       throw e;
	    }
	    
	    return resultList;
	}
	
	public List hints(String input, String domain) throws Exception {
		List resultList = new ArrayList<WikiSearch>();
	    String openSearchUrl = apiConfig.buildOpenSearchUrl(input, domain);
		HttpConnection connection = HttpPool.getInstance().getConnection();
	    String result = getUrlText(openSearchUrl, connection, null, 0).trim();
	    
	    try {
	        // Create a JSON object hierarchy from the inputs
	    	if (result.length() > 0) {
		    	JSONArray predsJsonArray = null;
		    	if (result.charAt(0) == '[') {
		    		JSONArray array = new JSONArray(result);
			        if (array.length() > 1)
				        predsJsonArray = array.getJSONArray(1); 
		    	}
		    	else {
			        JSONObject json = new JSONObject(result); // JSONSerializer.toJSON( jsonTxt );  
			        predsJsonArray = json.getJSONArray("1");
		    	}
			        
		        // Extract the Place descriptions from the inputs
//		        resultList = new ArrayList<String>(predsJsonArray.length());
		        for (int i = 0; i < predsJsonArray.length(); i++) {
		        	WikiSearch ws = parser.newWikiSearchInstance(predsJsonArray.getString(i));
		        	ws.setDomain(domain);
		            resultList.add(ws); //getJSONObject(i).toString());
		        }
	    	}
	    } catch (JSONException e) {
	       throw e;
	    }
	    
	    return resultList;
	}

	public void enableSecureConnection(boolean b) {
		if (b)
			apiConfig.useSecureConnection();
		else
			apiConfig.useTraditionalConnection();
		
	}
	
	public String getLanguageLinks(String title, String langCode, String favCode, String primaryCode, boolean ignoreEmptyTitle) throws Exception {
		if (title == null || title.length() == 0)
			return "";
		
		String enchodeTitle = URLEncoder.encode(URLDecoder.decode(title));
		String langLinkUrl = apiConfig.buildLangLinkUrl(langCode, enchodeTitle, favCode, false);
		HttpConnection connection = HttpPool.getInstance().getConnection();
//		connection.setMethod(HttpJavaNet.METHOD_POST);
//		connection.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
	    return connection.get(langLinkUrl); //getUrlText(langLinkUrl, connection);
	}
	
	public List<PageLang> getLanguageLinksArray(String title, String langCode, String favCode, String primaryCode, boolean ignoreEmptyTitle, WikipediaFamily site) throws Exception {
		if (title == null || title.length() == 0)
			return new ArrayList<PageLang>();
		
		String result = this.getLanguageLinks(title, langCode, favCode, primaryCode, ignoreEmptyTitle);		
	    
	    return LangLink.parseLangLinks(result, favCode, primaryCode, ignoreEmptyTitle, site);
	}
	
	public Feed getFeaturedFeed(String type, boolean lastOneOnly) throws Exception {
		return this.getFeaturedFeed(apiConfig.getSubdomain(), type, 0, lastOneOnly);
	}
	
	public Feed getFeaturedFeed(String domain, String type, long lastModifiedDate, boolean lastOneOnly) throws Exception {
		Feed feed = new Feed();

		String url = apiConfig.buildFeaturedFeedUrl(domain, type);

		HttpConnection connection = HttpPool.getInstance().getConnection();

		String result = getUrlText(url, connection, lastModifiedDate);

		long lastModifiedDateFromServer = connection.getLastModifiedDate(url);
		feed.setLastModifiedDate(lastModifiedDateFromServer);

		if (feed.getLastModifiedDate() == 0)
			feed.setLastModifiedDate(System.currentTimeMillis());

		if (connection.getResponseCode() == 200 && result.length() > 0) {
			feed.setList(FeaturedFeed.fastParse(result, domain, lastOneOnly));
		} else
			feed.setList(new ArrayList<WikiPage>());

		return feed;
	}
	
	public String getCrosslink(String title, String domain, String wikiLangCode) throws Exception {
		String url = apiConfig.buildCrossLinkUrl(domain, title, wikiLangCode);
        HttpConnection connection = HttpPool.getInstance().getConnection();
		String result = getUrlText(url, connection);
		
		List<PageLang> list = LangLink.parseLangLinks(result, wikiLangCode, wikiLangCode, false, wikipedias);
		
		if (list.size() > 0)
			return list.get(0).getTitle();
		return "";
	}
	
	public String getImageUrl(String domain, String title) throws Exception {
		String url = apiConfig.buildImageRetrievelUrl(domain, title);
        HttpConnection connection = HttpPool.getInstance().getConnection();
		String result = getUrlText(url, connection);
		
		String imageUrl = ImageUrl.parseOne(result);
		return imageUrl;
	}

	/**
	 * return token if need confirm token, otherwise, "success"
	 *
	 * @param domain
	 * @param name
	 * @param password
	 * @return
	 */
	public Login login(String domain, String name, String password) {
		
		int status = Login.ERROR;
		
		login = new Login();
		
		File tokenFile = new File(cachePath + File.separator + apiConfig.getHost() + ".token");
		String token;
//		if (tokenFile.exists()) {
//			token = new String(IO.readFileIntoBytes(tokenFile)).trim();
//			login.setVariableAttribute("lgtoken", token);
//		}
		
		String apiUrl = apiConfig.createApiUrl(domain);
		
		String url = apiUrl + login.getLoginUrl();
		
		HttpConnection.HttpRequest settings = new HttpConnection.HttpRequest(url);
		settings.setKeepAlive(false);
		
		List<HttpConnection.Parameter> params = login.buildParams(name, password);
		settings.setParams(params);
		settings.setAutomaticLoadCookie(true);
		settings.setKeepAlive(false);
		
		String result = null;
		
		HttpJavaNet conn = new HttpJavaNet();
		try {
			result = conn.postForResult(settings);
			
			conn.saveCookieToFile();
		} catch (Exception e) {
			status = Login.ERROR_SERVER;
		}
		
		if (null != result) {
			try {
				status = Login.parseLoginResultJson(login, result);
				
				if (status == login.ERROR_NEEDTOKEN) {
					token = login.getToken();
					login.setVariableAttribute("lgtoken", token);
					params.add(new Parameter("lgtoken", token));
					
					url = apiUrl + login.getUrl();
					
//					conn = new Http();
					
					result = conn.postForResult(settings);
				
					conn.saveCookieToFile();
					
					IO.writeFile(tokenFile, token);
					
					status = Login.parseLoginResultJson(login, result);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
				status = Login.ERROR_PARSE;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		login.setCookies(conn.getClientCookies());
		login.setStatus(status);
		
		return login;
	}
	
	public String acquireToken(String domain) throws Exception {
		return acquireToken(domain, login);
	}
	
	public String acquireToken(String domain, Login login) throws Exception {

		HttpConnection conn = HttpPool.getInstance().getConnection();
		conn.setClientCookies(login.getCookies());
		
		String apiUrl = apiConfig.createApiUrl(domain);
		ApiQuery query = new ApiQuery();
		String url = apiUrl + query.getAcquireTokenUrl();
		
		String result = conn.get(url);
		
		return ApiQuery.parseAcquireTokenResultJson(result);
	}
	
	public String importXmlFile(String domain, String xml, String token) throws Exception {
		String apiUrl = apiConfig.createApiUrl(domain);
		Import importApi = new Import();
		importApi.setXml(xml);
		importApi.setToken(token);
		
		HttpRequest settings = new HttpRequest(apiUrl);
		settings.setParams(importApi.getParamsPost());
		settings.setAutomaticLoadCookie(true);
		
		String url = apiUrl + importApi.getUrl();
		
		HttpConnection conn = HttpPool.getInstance().getConnection();
		
		String result = conn.uploadWithResult(url, settings);
		return result;
	}

    public String editPage(String domain, String title, String text, String token) throws Exception {
        String apiUrl = apiConfig.createApiUrl(domain);

        Edit edit = Edit.getInstance(token);
        edit.editPage(title, text);

        String url = apiUrl + edit.getUrl();

        HttpRequest settings = new HttpRequest(url);
        settings.setParams(edit.getParamsPost());
        settings.setAutomaticLoadCookie(true);

        HttpConnection conn = HttpPool.getInstance().getConnection();

        String result = conn.postForResult(settings);
        return result;
    }

    public WikiParser getParser() {
        return parser;
    }


    public List getPageImages(WikiPage page) throws Exception {
        Images images = new Images();
        images.setApiUrl(apiConfig.createApiUrl(page.getDomain()));
        return images.getList(page);
	}
}
