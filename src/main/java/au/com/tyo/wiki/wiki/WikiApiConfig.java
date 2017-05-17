package au.com.tyo.wiki.wiki;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import au.com.tyo.web.UrlUtils;
import au.com.tyo.wiki.WikiSettings;
import au.com.tyo.wiki.wiki.api.ApiConstants;
import au.com.tyo.wiki.wiki.api.FeaturedFeed;
import au.com.tyo.wiki.wiki.api.WikiCommon;

public class WikiApiConfig implements ApiConstants {
	
	public static final String WIKIPEDIA_DONATION_SITE = "https://donate.wikipedia.org";
	
	public static final String WIKIPEDIA_HOST_ORG = "wikipedia.org";
	public static final String WIKIPEDIA_HOST_COM = "wikipedia.com";
	
	public static final String WIKI_URL_TEMPLATE = "/wiki/";
	
	public static final String WIKIPEDIA_MOBILE_DOMAIN = "m.";
	
	private static final int WIKIPEDIA_HOST_STRING_LENGTH = WIKIPEDIA_HOST_ORG.length();

	/**
	 * all bots should use https by default since 2016-07-12
	 */
	private static final String DEFAULT_PROTOCOL = "https";
	private static final String DEFAULT_PROTOCOL_SSL = "https";
	
	private static final String ROOT_PATH = "//";

	private static final String DEFAULT_WIKI_MAINPAGE = "index.php?";
	private static final String DEFAULT_WIKI_QUERY = "title=";
	private static final String DEFAULT_LANGUAGE_DOMAIN = "en";
//	private static final String DEFAULT_SITE = DEFAULT_LANGUAGE_DOMAIN + "." + WIKIPEDIA_HOST_ORG;
	
	public static final String WIKIPEDIA_SCRIPT_PATH = "/w/";
	
	public static final String WIKIPEDIA_API_URL = "api.php";
	
	public static final String WIKIPEDIA_API_MAIN_PARAM_ACTION = "action";
	
	public static final String WIKIPEDIA_API_MAIN_PARAM_ACTION_VALUE_MOBILEVIEW = "mobileview";	
	public static final String WIKIPEDIA_API_MAIN_PARAM_ACTION_VALUE_OPENSEARCH= "opensearch";
	
	public static final int WIKIPEDIA_API_MAIN_PARAM_ACTION_ID_MOBILEVIEW = 1;
	public static final int WIKIPEDIA_API_MAIN_PARAM_ACTION_ID_OPENSEARCH = 2;
	public static final int WIKIPEDIA_API_MAIN_PARAM_ACTION_ID_QUERY = 3;
	
	private String subdomain = DEFAULT_LANGUAGE_DOMAIN; // the sub domain, etc. www, en, zh, ...
	
	private String protocol = DEFAULT_PROTOCOL;
	
	private String siteIp = "";

	private String site = "";
	
	private boolean needEncoded = true;
	
	private String baseUrl = "";
	
	private Format format;
	
	private WikiSettings settings;
	
	private WikiCommon wikiCommon;
	
	private String host;
	
	private String hostAlias; // like .org to .com
	
	private String hostMobile;  // if there is mobile host
	
	private String mainScript;
	
	private String apiScript;
	
	private String scriptPath;
	
	private int port;

	public class Format {
		boolean rawText;
		boolean mobile;
	}

	public WikiApiConfig() {
		super();
//		site = DEFAULT_SITE; 
		
		init();
	}
	
	public WikiApiConfig(WikiSettings settings) {
		super();
		this.settings = settings;
		
		init();
	}

	private void init() {
		subdomain = null;
		
		format = new Format();
		
		format.mobile = true; //settings.isMobileDevice();
		format.rawText = false;
		
		wikiCommon = new WikiCommon();
		
		host = WIKIPEDIA_HOST_ORG;
		hostMobile = WIKIPEDIA_HOST_ORG;
		hostAlias = WIKIPEDIA_HOST_COM;
		
		setPort(80);
		
		scriptPath = WIKIPEDIA_SCRIPT_PATH;
		
		mainScript = DEFAULT_WIKI_MAINPAGE;
		apiScript = WIKIPEDIA_API_URL;
		
		refresh();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public WikiSettings getSettings() {
		return settings;
	}

	public void setSettings(WikiSettings settings) {
		this.settings = settings;
	}

	public String getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(String domain) {
		this.subdomain = domain;
		
		refresh();
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
		
		refresh();
	}

	public String getSiteIp() {
		return siteIp;
	}

	public void setSiteIp(String siteIp) {
		this.siteIp = siteIp;
	}

	public boolean isNeedEncoded() {
		return needEncoded;
	}

	public void setNeedEncoded(boolean needEncoded) {
		this.needEncoded = needEncoded;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}
	
	public String getMainScript() {
		return mainScript;
	}

	public void setMainScript(String mainScript) {
		this.mainScript = mainScript;
	}

	public String getApiScript() {
		return apiScript;
	}

	public void setApiScript(String apiScript) {
		this.apiScript = apiScript;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
	
	public String getApiQueryString() {
		return scriptPath + apiScript;
	}

	public String getWikipediaMobileHost() {
		return buildSite("m." + subdomain);
	}

    /**
     * OpenSearch doesn't need to have SSL connection as it will be slower
     *
     * @param input
     * @param domain
     * @return
     */
	public String buildOpenSearchUrl(String input, String domain) {
        // choose the protocol we need here
		StringBuffer url = new StringBuffer(buildBaseUrl("http", domain) + getApiQueryString() + "?");
		String encodedInput = encode(input);
		url.append(WIKIPEDIA_API_MAIN_PARAM_ACTION + "=opensearch&limit=22&search=" + encodedInput);
		return url.toString();
	}

	public String buildMainPageUrl() {
		return baseUrl;
	}
	
	public String buildMainPageUrl(String domain) {
		return buildMainPageUrl(domain, 80);
	}
	
	public String buildMainPageUrl(String domain, int port) {
		return buildBaseUrl(domain);
	}
	
	public String buildWikipediaUrlWithTitle(String domain, String title) {
		return buildBaseUrl(domain) + WIKI_URL_TEMPLATE + encode(title);
	}
	
	public String buildWikipediaUrlWithTitle(String protocol, String domain, String title) {
		return buildBaseUrl(protocol, domain) + WIKI_URL_TEMPLATE + encode(title);
	}
	
	public String buildMobileMainPageUrl() {
		return buildBaseUrl(this.protocol, this.subdomain, port);
	}
	
	public String buildMainIndexUrlFromBaseUrl(String aBaseUrl) {
		return aBaseUrl + scriptPath + mainScript;
	}
	
	public String completeWikiLink(String url) {
		return this.completeWikiLink(url, subdomain);
	}
	
	public String completeWikiLink(String url, String domain) {
		if (url.indexOf("://") > 0)
			return url;
		
		if (url.startsWith("//"))
			return this.protocol + ":" + url;
		
		if (url.startsWith("/"))
			return this.buildBaseUrl(domain) + url;
		
		return this.buildBaseUrl(domain) + "/" + url;
	}
	
	public String buildMainIndexUrl() {
		return buildMainIndexUrlFromBaseUrl(baseUrl);
	}
	
	public String buildMainIndexUrl(String domain) {
		return buildMainIndexUrlFromBaseUrl(buildBaseUrl(domain));
	}
	
	
//	
//	public String buildMainPageUrl(String title) {
//		StringBuffer sb = new StringBuffer();
//		return buildMainPageUrl();
//	}
	
//	public String buildUrlWithTitle(String title) {
//		return buildQueryUrl(site, title);
//	}
	
		
//		if (site == null || site.length() == 0)
//			buffer = new StringBuffer(buildUrl(protocol, DEFAULT_SITE));
//		else
//			buffer = new StringBuffer(buildUrl(protocol, site));
//		
//		baseUrl = buffer.toString();
	
	public String buildQueryUrlWithBaseUrl(String baseUrl, String title) {
		StringBuffer buffer = new StringBuffer(buildMainIndexUrlFromBaseUrl(baseUrl));
		
		String encodedTitle = encode(title);
		buffer.append(DEFAULT_WIKI_QUERY);	
		buffer.append(encodedTitle);
		
		return buffer.toString();
	}

	public String buildQueryUrl(String domain, String title) {
		return buildQueryUrlWithBaseUrl(buildBaseUrl(domain), title);
	}
	
	public String buildQueryUrl(String title) {
		return buildQueryUrl(subdomain, title);
	}
	
	public String buildSearchUrl(String query, String langCode) {
		return this.buildSearchUrl(query, langCode, 10);
	}
	
	public String buildSearchUrl(String query, String langCode, int number) {
		String encodedQuery = encode(query);
		
		StringBuffer buffer = new StringBuffer(this.buildBaseUrl(langCode) + getApiQueryString() + "?");
		
		buffer.append(WIKIPEDIA_API_MAIN_PARAM_ACTION + "=query&list=search&srlimit=" + String.valueOf(number) + "&format=json&srsearch=");
		buffer.append(encodedQuery);
		
		return buffer.toString();
	}
	
	public String buildSearchApiUrl(String query, String landCode, int searchType, int offset, int limit) {
		return createApiUrl(landCode) + (wikiCommon.getWikiSearchApi().getSearchUrl(query, searchType, offset, limit));
	}
	
	public String buildUrl(String protocol, String domain) {	
		return protocol + ":" + ROOT_PATH + domain;		
	}
	
	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	static public String encode(String what) {
		String encodedText = null;
		try {
			encodedText = URLEncoder.encode(what, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			try {
				encodedText = URLEncoder.encode(what);
			}
			catch (Exception ex) {
				encodedText = what;
			}
		}
		return encodedText;
	}
	
	private static String appendFormatRequests(StringBuffer formatedUrl, Format format) {
		StringBuffer parameters = new StringBuffer();
		if (format != null ) {
			if (format.rawText)
				parameters.append("action=raw&");
				
			if (format.mobile)
				parameters.append("useformat=mobile&");
		}
		if (parameters.length() > 0) {
			if (formatedUrl.indexOf("?") == -1) 
				formatedUrl.append("?");
			else if (formatedUrl.charAt(formatedUrl.length() - 1) != '?')
				formatedUrl.append('&');
			formatedUrl.append(parameters);
		}
		return formatedUrl.toString();
	}
	
	public static String appendFormatRequests(String url, Format format) {
		StringBuffer formatedUrl = new StringBuffer(url);
		return appendFormatRequests(formatedUrl, format);
	}
	
	public String appendFormatRequests(String url) {
		return appendFormatRequests(url, format);
	}

	public void setFormatRaw(boolean b) {
		format.rawText = b;
	}
	
	public boolean isWikipediaDomain(String domain) {
		if (domain.length() < WIKIPEDIA_HOST_STRING_LENGTH)
			return false;
		int len = domain.length();
		return domain.substring(len - WIKIPEDIA_HOST_STRING_LENGTH).equalsIgnoreCase(host) ||
				domain.substring(len - WIKIPEDIA_HOST_STRING_LENGTH).equalsIgnoreCase(hostAlias);
	}
	
	public static String linkToTitle(String url) {
		String tempTitle = "";
		int pos = url.lastIndexOf('/');
		if (pos > -1) {
			tempTitle = url.substring(pos + 1).replace('_', ' ');
			
			if ((pos = tempTitle.indexOf("title=")) > -1) {
				String temp = tempTitle.substring(pos + 6);
				String[] tokens = temp.split("&");
				if (tokens != null) {
					if (tokens.length > 0)
						tempTitle = tokens[0];
					else
						tempTitle = temp;
				}
				else
					tempTitle = temp;
			}
		}
		String articleTitle = null;
		try {
			articleTitle = URLDecoder.decode(tempTitle, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			articleTitle = URLDecoder.decode(tempTitle);
		}
		return articleTitle;
	}
	
	public static String linkToDomain(String url) {
		String domain = UrlUtils.extractDomain(url);
		if (domain.length() == 0)
			return DEFAULT_LANGUAGE_DOMAIN;
		return domain;
	}

	public String buildBaseUrl() {
		return buildBaseUrl(protocol, subdomain, port);
	}
	
	public String buildSite(String domain) {
		StringBuffer buffer = new StringBuffer();
		
		if (null != domain && domain.length() > 0)
			buffer.append(domain + ".");
		
		buffer.append(host);

		return buffer.toString();
	}
	
	public String createApiUrl() {
		return createApiUrl(this.protocol, this.subdomain, port);
	}
	
	public String createApiUrl(String domain) {
		return createApiUrl(domain, 80);
	}
	
	public String createApiUrl(String domain, int port) {
		return createApiUrl(this.protocol, domain, port);
	}
	
	public String createApiUrl(String whatProtocol, String langCode) {
		return createApiUrl(whatProtocol, langCode, port);
	}
	
	/**
	 * 
	 * @param whatProtocol
	 * @param langCode
	 * @return api url with the question mask at the end
	 */
	public String createApiUrl(String whatProtocol, String langCode, int port) {
		String aBaseUrl = buildBaseUrl(whatProtocol, langCode, port);
		StringBuffer url = new StringBuffer(aBaseUrl + getApiQueryString() + "?");
		return url.toString();
	}
	
	public String buildBaseUrl(String domain) {
		return buildBaseUrl(this.protocol, domain, port);
	}
	
	public String buildBaseUrl(String protocol, String domain) {
		return buildBaseUrl(protocol, domain, port);
	}
	
	public String buildBaseUrl(String protocol, String domain, int port) {
		return protocol + ":" + ROOT_PATH + buildSite(domain) +
                (((protocol.equals("http") && port == 80) || protocol.equals("https")) ? "" : (":" + port));
	}
	
	public void refresh() {
		site = (null != subdomain && subdomain.length() > 0) ? (subdomain + ".") : "" + host;
		baseUrl = protocol + ":" + ROOT_PATH + site + ":" + String.valueOf(port);
	}

	public void useSecureConnection() {
		this.setProtocol(DEFAULT_PROTOCOL_SSL);
	}

	public void useTradtionalConnection() {
		this.setProtocol(DEFAULT_PROTOCOL);
	}

	public String buildLangLinkUrl(String langCode, String title) {
		StringBuffer url = new StringBuffer(createApiUrl(protocol, langCode));
		String encodedInput = title; //encode(title);
		url.append(WIKIPEDIA_API_MAIN_PARAM_ACTION + "=query&prop=langlinks&lllimit=500&format=xml&redirects&titles=" + encodedInput);
		return url.toString();
	}
	
	public String buildSectionRetrievalUrl(String query) {
		return buildSectionRetrievalUrl(query, subdomain, -1);
	}

	public String buildSectionRetrievalUrl(String query, int section) {
		return buildSectionRetrievalUrl(query, subdomain, section);
	}
	
	public String buildSectionRetrievalUrl(String query, String langCode, int section) {
		StringBuffer url = new StringBuffer(createApiUrl(protocol, langCode));
		String encodedInput = encode(query);
		url.append(WIKIPEDIA_API_MAIN_PARAM_ACTION + "=parse&format=json&prop=text&redirects&page=" + encodedInput);
		if (section > -1)
			url.append("&section=" + section);
		return url.toString();
	}

	/**
	 * 
	 * @param query
	 * @param sections
	 * 
	 * @return
	 */
	public String buildArticleRetrieval4MobileViewUrl(String query,
			String sections, String areaCode) {
		return buildArticleRetrieval4MobileViewUrl(subdomain, query, sections, areaCode);
	}
	
//	public String buildArticleRetrieval4MobileViewUrl(String langCode, String query,
//			String sections) {
//		return this.buildArticleRetrieval4MobileViewUrl(langCode, query, sections, "");
//	}
	
	public String buildArticleRetrieval4MobileViewUrl(String langCode, String query,
			String sections, String areaCode) {
		StringBuffer url = new StringBuffer(createApiUrl(protocol, langCode));
//		String encodedInput = encode(query);
		//url.append(WIKIPEDIA_API_MAIN_PARAM_ACTION + "=mobileview&redirect=yes&prop=" + URLEncoder.encode("sections|text|normalizedtitle") + "&sectionprop=" + URLEncoder.encode("level|line") + "&noheadings=yes&format=json&page=" + encodedInput + "&sections=" + sections);
		url.append(wikiCommon.getMobileView().getMobileViewUrl(query, sections, langCode, areaCode));
		return url.toString();
	}
	
	public String buildRandomPageRetrievalUrl() {
		return buildRandomPageRetrievalUrl(1);
	}
	
	public String buildRandomPageRetrievalUrl(int limit) {
		StringBuffer url = new StringBuffer(createApiUrl());
		
		url.append(wikiCommon.getListRandom().getListRandomPageUrl(limit));
		
		return url.toString();
	}
	
	public String buildThumbnailLinkRetrievalUrl(List<String> titles) {
		return createApiUrl() + (wikiCommon.getPageImages().getPageImageUrl(titles));
	}
	
	public String buildFeaturedFeedUrl(String domain) {
		return buildFeaturedFeedUrl(domain, FeaturedFeed.FEATURED_FEED_FEATURED);
	}
	
	public String buildFeaturedFeedUrl(String domain, String type) {
		return createApiUrl(domain) + (wikiCommon.getFeaturedFeed().getFeaturedFeedUrl(type));
	}
	
	public String buildFeaturedFeedUrl() {
		return buildFeaturedFeedUrl(subdomain, FeaturedFeed.FEATURED_FEED_FEATURED);
	}

	public String buildCrossLinkUrl(String domain, String title, String wikiLangCode) {
		return createApiUrl(domain) + (wikiCommon.getLangLink().getCrossLangLinkUrl(title, wikiLangCode));
	}

	public String buildImageRetrievelUrl(String domain, String title) {
		return createApiUrl(domain) + (wikiCommon.getImageUrl().getImageUrl(title));
	}
	
	/*
	 * API url for retrieval image name of a page
	 */
	public String buildImagesRetrievalUrl(String domain, String title) {
		return createApiUrl(domain) + (wikiCommon.getImages().getImagesUrl(title));
	}

	public String getMobileHost() {
		return hostMobile;
	}

	public void setMobileHost(String hostMobile) {
		this.hostMobile = hostMobile;
	}

	public String getHostAlias() {
		return hostAlias;
	}

	public void setHostAlias(String hostAlias) {
		this.hostAlias = hostAlias;
	}
}
