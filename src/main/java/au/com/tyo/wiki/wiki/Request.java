package au.com.tyo.wiki.wiki;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import au.com.tyo.io.ItemSerializable;
import au.com.tyo.wiki.Status;
import au.com.tyo.wiki.wiki.api.MobileView;

public class Request extends ItemSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1003226985810710514L;

    public static final String COMMAND_LOOK_UP_ID = "id:";
	
	public static final int QUERY_TYPE_SEARCH = 0;
	public static final int QUERY_TYPE_URL = 1;
	public static final int QUERY_TYPE_MAIN_PAGE = 2; // still url but the url for the main page
	public static final int QUERY_TYPE_TITLE = 3;  // get article with title
	public static final int QUERY_TYPE_ABSTRACT = 4;
	public static final int QUERY_TYPE_LOCAL = 5;
	public static final int QUERY_TYPE_HISTORY = 6;
    public static final int QUERY_TYPE_FEED = 7;
    public static final int QUERY_TYPE_LANGLINKS = 8;

    public static final long QUERY_BASE = 1000;

//    public static final int QUERY_TYPE_FEED_FEATURED_ARTICLE = 101;
//    public static final int QUERY_TYPE_FEED_FEATURED_POTD = 100;
//    public static final int QUERY_TYPE_FEED_FEATURED_ONTHISDAY = 102;

	public static final int SCOPE_REMOTE = 0;
	public static final int SCOPE_LOCAL = 1;

	/**
	 * Public Request Types which don't have a subtype
	 */
	public static final long FROM_NOTHING = 0;
	public static final long FROM_OPENSEARCH = 1;
	public static final long FROM_LANG_LINK = 2;
	public static final long FROM_RELATED = 4;
	public static final long FROM_HISTORY = 8;
	public static final long FROM_SEARCH_RESULT = 16;
	public static final long FROM_VOICE_SEARCH = 32;
	public static final long FROM_SEARCH_BUTTON = 64;
	public static final long FROM_SEARCH_REQUEST = 128;


	public static final long FROM_RANDOM_LOCAL = 256;
	public static final long FROM_RANDOM_WWW = 512;
	public static final long FROM_RANDOM_SEARCH_BUTTON = 1024;
	public static final long FROM_RANDOM_MENU = 2048;

	/**
	 * Subtype base
	 *
	 */

	public static final long FROM_BASE = 10;

	public static final long FROM_FEATURED = 4096;

	public static final long FROM_OTHER_APP = 4096 << 2;
	public static final long FROM_VIEWED = 4096 << 4;

	/**
	 * Subtype Featured Feed, PotD, Featured, OnThisDay
	 */
	public static final long QUERY_FEED_FEATURED_POTD = QUERY_TYPE_FEED * QUERY_BASE + 1;
	public static final long QUERY_FEED_FEATURED_ARTICLE = QUERY_TYPE_FEED * QUERY_BASE + 2;
	public static final long QUERY_FEED_FEATURED_ONTHISDAY = QUERY_TYPE_FEED * QUERY_BASE + 3;

	/**
	 * Request Status
	 */
	public static final int STATUS_NEW = 0;
	public static final int STATUS_SENT = 1;
	public static final int STATUS_DONE = 2;


    /**
	 *
	 */
	private String url;

	/**
	 *
	 */
	private String query;

	/**
	 *
	 */
	private String rawQuery;

	/**
	 * Query type
	 */
	private int type;  // what kind of request, search? title?

    private long fromSubtype;
	
	private String anchor;
	
	private String[] wikiDomains;
	
	private String sections;  // which sections needed, 1|2|3, .. or all
	
	private int responseCode;

	/**
	 * Don't serialize / deserialize page as it would create a infinite loop
	 */
	private WikiPage page;

	/**
	 * From search results, from featured list, from others
	 */
	private long fromType;

	/**
	 *
	 */
	private boolean toCrosslink;

    /**
     * A list of search results
     */
	private List results;

	private boolean fullTextSearch;

	private int scope;

	private boolean bestMatch;

	/**
	 * Request Status
	 */
	private int status;

    /**
     * Index
     */
    private int index;

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
		fullTextSearch = false;
		scope = SCOPE_REMOTE;
		status = STATUS_NEW;
	}

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
    public void serialise(ObjectOutputStream stream) throws IOException {
        stream.writeObject(url);
        stream.writeObject(query);
        stream.writeObject(rawQuery);
        stream.writeObject(type);
        stream.writeObject(anchor);
        stream.writeObject(wikiDomains);
        stream.writeObject(sections);
        stream.writeObject(responseCode);
        stream.writeObject(fromType);
        stream.writeObject(toCrosslink);
        stream.writeObject(fullTextSearch);
        stream.writeLong(fromSubtype);
        stream.writeInt(scope);
        stream.writeBoolean(bestMatch);
    }

    @Override
    public void deserialise(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        url = (String) stream.readObject();
        query = (String) stream.readObject();
        rawQuery = (String) stream.readObject();
        type = (int) stream.readObject();
        anchor = (String) stream.readObject();
        wikiDomains = (String[]) stream.readObject();
        sections = (String) stream.readObject();
        responseCode = (int) stream.readObject();
        fromType = (long) stream.readObject();
        toCrosslink = (boolean) stream.readObject();
        fullTextSearch = (boolean) stream.readObject();
        fromSubtype = stream.readLong();
        scope = stream.readInt();
        bestMatch = stream.readBoolean();
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public boolean isBestMatch() {
        return bestMatch;
    }

    public void setBestMatch(boolean bestMatch) {
        this.bestMatch = bestMatch;
    }

    public long getFromSubtype() {
        return fromSubtype;
    }

    public void setFromSubtype(int fromSubtype) {
        this.fromSubtype = fromSubtype;
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

    /**
     * Get the original query which could be a page title, user input query
     *
     * @return String
     */
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
	
	public void setWikiDomains(String[] domains) {
		this.wikiDomains = domains;
	}

	public void addWikiDomain(String domain) {
		if (null == wikiDomains) {
			wikiDomains = new String[2]; // one for main, one for alternative
			wikiDomains[0] = domain;
		}
		// simplify it, so we have to make sure we don't add two same domains
		else if (wikiDomains.length == 2 && wikiDomains[1] == null) {
			wikiDomains[1] = domain;
		}
		else {
			int newLen = wikiDomains.length + 1;
			String[] domains = new String[newLen];
			int i;
			for (i = 0; i < wikiDomains.length; ++i)
				domains[i] = wikiDomains[i];
			domains[i] = domain;
		}
	}
	
	public String[] getWikiDomains() {
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

	public long getFromType() {
		return fromType;
	}

	public void setFromType(long fromType) {
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

	public List getResults() {
		return results;
	}

	public void setResults(List results) {
		this.results = results;
	}
	
	public boolean succeeded() {
		return Status.isStatusAcceptable(responseCode);
	}

	public boolean isIdLookup() {
		return getRawQuery() != null && getRawQuery().startsWith(COMMAND_LOOK_UP_ID);
	}

	public boolean isFullTextSearch() {
		return fullTextSearch;
	}

	public void setFullTextSearch(boolean b) {
		fullTextSearch = b;
	}

    public boolean isFromTypeFeed() {
        return isFromType(FROM_FEATURED);
    }

    private boolean isFromType(long base) {
        long value = fromType - (FROM_BASE * base);
        return value > 0 && value < FROM_BASE;
    }

    public boolean isFromHistory() {
        return fromType == Request.FROM_HISTORY;
    }
}
