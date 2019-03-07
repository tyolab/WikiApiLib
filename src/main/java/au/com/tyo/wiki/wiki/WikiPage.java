package au.com.tyo.wiki.wiki;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.tyo.io.IO;
import au.com.tyo.lang.CJK;
import au.com.tyo.utils.StringUtils;
import au.com.tyo.utils.TextUtils;
import au.com.tyo.web.PageInterface;

public class WikiPage extends WikiPageBase implements PageInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1864682452543190800L;
	
	private static String[] STYLES_N_SCRIPTS = {};
//            {
//	        "jquery-1.3.2.min.js",
//			"ready.js",
//			"collapsible.js",
//			"common.js",
//			"wiki.css",
//			"wikipedia.css",
//			"wikipedia_align.css"
//			//, "pure.css"
//	};
	
	private static String[] THEME_DEPENDANT_CSS = {"css.min.css"};
	
	private static String[] LANDSCAPE_DEPENDANT_CSS = null; //{"wikipedia.css"};
	
	public static final int ERROR_PAGE_NOT_FOUND = 0;
	public static final int ERROR_PAGE_UNKNOWN = 1;
	
	public static final int FROM_SOURCE_SERVER = 0;
	public static final int FROM_SOURCE_LOCAL = 1;
	
	public static final int TEXT_FORMAT_JSON = 0;
	public static final int TEXT_FORMAT_TXT = 1;
	public static final int TEXT_FORMAT_HTML = 2;
	
	private String url;
	
	/* Text of page could be anything, html, json, or simply just text */
	private String text = "";
	
	private String lang; // language code

    /**
     * This is the base url for the API request
     */
	private String baseUrl;
	
	private byte[] bytes = null; // the text in bytes which may be modified by removing the search bar and title
	
	private byte[] imgBytes = null; // the image content associated with the page
	
	private List<PageLang> langs; // in available languages
	
	private List<WikiPageSection> sections;
	
	private int	sectionCount;
	
	private String fromAnchor;

	/**
	 * Should be the domain of the language
	 */
	private String langCode;

	private String domain;
	
	private String abs;
	
	private Error error;

	private Request request;
	
	private String html;
	
	private boolean didYouMean;
	
	private String thumbnailLink;
	
	private Map<String, String> imageUrls;

    /**
     * The list of names of the images
     */
    private List<String> images;
	
	private WikiPage xPage; // the page being cross linked

    /**
     * Wikipedia page last modified time
     */
	private String lastUpdateDate;
	
	private String lastUpdateBy;
	
	private String redirectFrom;
	
	private List<String> redirects;
	
	private int prefImageWidth;
	
	private String onImageClickLink;
	
	private boolean hasFullText;
	
	private static WikiPageProcessor pageProcessor;
	
	private int fromSource;
	
	private int textFormat;
	
	private String notes; // notes about the page, such as only abstract is availabe

	/**
	 * A unique identification number to id the page in the App
	 */
	private int id;

	/**
	 * The page / article id that is uniquely identified in Wikipedia
	 */
	private int pageId;

	private boolean loaded = false;

    private String cachePath;

    private long retrievedTimestamp;

    private long lastViewedTime;

	/**
	 * If the page is from a local source
	 */
	private String fromDb;
//	
//	private int namespace;
	static {
		pageProcessor = null;
	}

    public WikiPage(String title) {
		this.setTitle(title);
		
		init();
	}

	public WikiPage(File file) throws IOException {
		bytes = IO.readFileIntoBytes(file);
		text = new String(bytes);
		init();
	}

	public WikiPage() {
		init();
	}
	
	private void init() {
        setType(ItemType.PAGE);

//		abs = new StringBuffer("");
		abs = "";
		title = "";
		langs = null;
		baseUrl = null;
		sections = new ArrayList<WikiPageSection>();
		imageUrls = new HashMap<>();
		images = new ArrayList<>();
		setDidYouMean(false);
		
		this.stylesAndScripts = STYLES_N_SCRIPTS;
		this.themeCss = THEME_DEPENDANT_CSS;
		this.landscapeDependentCss = LANDSCAPE_DEPENDANT_CSS;
		prefImageWidth = -1;
		onImageClickLink = null;
		hasFullText = false;
		setFromSource(FROM_SOURCE_SERVER);
		langCode = "en";
		domain = null;
		redirectFrom = null;
		xPage = null;
		html = null;
		textFormat = TEXT_FORMAT_JSON;
		notes = null;

		lastViewedTime = System.currentTimeMillis();
		pageId = -1;
	}

	@Override
	public void serialise(ObjectOutputStream stream) throws IOException {
		super.serialise(stream);

		stream.writeObject(url);
        stream.writeObject(text);
        stream.writeObject(lang);
        stream.writeObject(baseUrl);
        stream.writeObject(bytes);
        stream.writeObject(imgBytes);
        stream.writeObject(langs);
        stream.writeObject(sections);
        stream.writeObject(fromAnchor);
        stream.writeObject(langCode);
        stream.writeObject(abs);
        stream.writeObject(error);
        stream.writeObject(request);
        stream.writeObject(html);
        stream.writeObject(didYouMean);
        stream.writeObject(thumbnailLink);
        stream.writeObject(images);
        stream.writeObject(xPage);
        stream.writeObject(lastUpdateDate);
        stream.writeObject(lastUpdateBy);
        stream.writeObject(redirectFrom);
        stream.writeObject(redirects);
        stream.writeObject(prefImageWidth);
        stream.writeObject(onImageClickLink);
        stream.writeObject(hasFullText);
        stream.writeObject(fromSource);
        stream.writeObject(textFormat);
        stream.writeObject(notes);
        stream.writeObject(id);
        stream.writeObject(loaded);
        stream.writeObject(cachePath);
        stream.writeObject(retrievedTimestamp);
        stream.writeObject(domain);
        stream.writeInt(pageId);
        stream.writeObject(imageUrls);
    }

	@Override
	public void deserialise(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.deserialise(stream);

		url = (String) stream.readObject();
        text = (String) stream.readObject();
        lang = (String) stream.readObject();
        baseUrl = (String) stream.readObject();
        bytes = (byte[]) stream.readObject();
        imgBytes = (byte[]) stream.readObject();
        langs = (List<PageLang>) stream.readObject();
        sections = (List<WikiPageSection>) stream.readObject();
        fromAnchor = (String) stream.readObject();
        langCode = (String) stream.readObject();
        abs = (String) stream.readObject();
        error = (Error) stream.readObject();
        request = (Request) stream.readObject();
        html = (String) stream.readObject();
        didYouMean = (boolean) stream.readObject();
        thumbnailLink = (String) stream.readObject();
        images = (List<String>) stream.readObject();
        xPage = (WikiPage) stream.readObject();
        lastUpdateDate = (String) stream.readObject();
        lastUpdateBy = (String) stream.readObject();
        redirectFrom = (String) stream.readObject();
        redirects = (List<String>) stream.readObject();
        prefImageWidth = (int) stream.readObject();
        onImageClickLink = (String) stream.readObject();
        hasFullText = (boolean) stream.readObject();
        fromSource = (int) stream.readObject();
        textFormat = (int) stream.readObject();
        notes = (String) stream.readObject();
        id = (int) stream.readObject();
        loaded = (boolean) stream.readObject();
        cachePath = (String) stream.readObject();
        retrievedTimestamp = (long) stream.readObject();
        domain = (String) stream.readObject();
        pageId = stream.readInt();
        imageUrls = (Map<String, String>) stream.readObject();
    }

    public long getLastViewedTime() {
        return lastViewedTime;
    }

    public void setLastViewedTime(long lastViewedTime) {
        this.lastViewedTime = lastViewedTime;
    }

    public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public static void setPageProcessor(WikiPageProcessor processor) {
		WikiPage.pageProcessor = processor;
	}
	
	public static WikiPageProcessor getPageProcessor() {
		return WikiPage.pageProcessor;
	}
	
	public void setText(String text) {
		this.text = text; 
	}
	
	private String process() {
		if (bytes == null)
			bytes = text.getBytes(); // "";
		return new String(WikiTransformer.transform(this));
	}
	
	public synchronized void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
	
	public String getAbstract() {
		return abs.toString();
	}
	
	public String getText() {
		return text;
	}
	
	public byte[] getBytes() {
		if (bytes == null && text != null)
			resetPageRawBytes();
		return bytes;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean shouldCheckOtherLanguages() {
		return langs == null;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public Request getRequest() {
		if (null == request)
			request = new Request();
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

//	public int getResponseCode() {
//		if (request != null)
//			return request.getResponseCode();
//		return 200;
//	}

	public void resetPageRawBytes() {
		bytes = text.getBytes();
	}

	public String getFromAnchor() {
		return fromAnchor;
	}

	public void setFromAnchor(String fromAnchor) {
		this.fromAnchor = fromAnchor;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getDomain() {
		if (null == domain)
			return getLangCode();
		return domain;
	}

	public void setDomain(String lang) {
		this.domain = lang;
	}

	public void transform() {
		if (request.getResponseCode() == 200)
			text = process();
	}

	public void setBytes(byte[] target) {
		bytes = target;
	}
	
	public void addRedirect(String redirect) {
		if (null == redirects)
			redirects = new ArrayList<String>();
		redirects.add(redirect);
	}

	public void retrieveTitle() {
		String title = WikiTransformer.processTitle(getBytes());
		if (title != null)
			setTitle(title);
	}
	
	public void setAbstract(String absText) {
		clearAbstract();
		abs = (absText);
	}
	
	public void clearAbstract() {
		abs = "";
	}
	
	public void retrieveAbstract(String section0, boolean appendAfter) {
		if (abs == null)
			abs = "";
		
		if (!failed()) {
			String absText;
			absText = (WikiTransformer.processAbstract(section0.getBytes()).trim());
			absText = format(absText);
			
			if (appendAfter && abs.length() > 0)
				abs = abs + "\n" + absText;
			else
				abs = absText;
		}
		else
			abs = (error.getInfo().trim());
	}
	
	public String format(String text) {
		String formatted = text.replaceAll("Script error", "");
		
		formatted = TextUtils.removeFootNoteMarks(formatted);
		formatted = TextUtils.removeNotes(formatted);
		formatted = TextUtils.removeExtraSpaces(formatted).trim();
		
		formatted = StringUtils.unescapeHtml(formatted);
		formatted = StringUtils.unescapeHtml(formatted);
		
		if (CJK.isCJKLangCode(langCode))
			formatted = CJK.removeSpacesBetweenChinese(formatted);
		return formatted;
	}

	public boolean hasLangLinks() {
        return this.langs != null && this.langs.size() > 0;
    }

    public List getLangLinks() {
        return this.langs;
    }

	public void setLangLinks(List<PageLang> languageLinks) {
		this.langs = languageLinks;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public boolean failed() {
		return error != null;
	}

	public void addSection(WikiPageSection pageSection) {
		sections.add(pageSection);
	}
	
	public int countParsedSections() {
		return sectionCount;
	}
	
	public int getSectionSize() {
		return sections.size();
	}
	
	public WikiPageSection getSection(int index) {
		if (index >= sections.size())
			return null;
		return sections.get(index);
	}
	
	public void setSectionCount(int number) {
		sectionCount = number;
	}
	
	public void clearSections() {
		sections.clear();
		setSectionCount(0);
	}

	public void setHtml(String html) {
		if (null == html)  {
			this.html = null;
			return;
		}

		if (pageProcessor != null)
			this.html = pageProcessor.process(fromDb, html);
		else
			this.html = html;
	}
	
	public String getHtml() {
		return html;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isDidYouMean() {
		return didYouMean;
	}

	public void setDidYouMean(boolean didYouMean) {
		this.didYouMean = didYouMean;
	}

	public WikiPage getXPage() {
		return xPage;
	}

	public void setXPage(WikiPage xPage) {
		this.xPage = xPage;
	}

	public int getTextFormat() {
		return textFormat;
	}

	public void setTextFormat(int textFormat) {
		this.textFormat = textFormat;
	}

	public String getLastUpdateBy() {
		return lastUpdateBy;
	}

	public void setLastUpdateBy(String lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public boolean isAbstractEmpty() {
		return abs == null || abs.toString().length() == 0;
	}

	public void cleanupForSerialization() {
		bytes = null;
		text = null;
		html = null;
		abs = null;
	}
	
	public void initializeForDeserialization() {
		abs = ""; //new StringBuffer("");
	}
	
	@Override
	public String createHtmlContent() {
		if (textFormat == TEXT_FORMAT_JSON)
			return WikiHtml.sectionsToHtml(this); // + WikiHtml.createFooter();
		else
			return text;
	}
	
	public String toString() {
		return getTitle();
	}

	public void setThumbnailLink(String link) {
		this.thumbnailLink = link;
	}
	
	public String getThumbnailLink() {
		return this.thumbnailLink;
	}
	
	public void loadAbstract() {
		if (sections.size() == 0)
			return;
		
		retrieveAbstract(getSection(0).getText(), false);
		int i = 1;
		while (!isAbstractLongEnough() && /*countParsedSections()*/getSectionSize() > i) 
			retrieveAbstract(getSection(i++).getText(), true);
	}

	public boolean isAbstractLongEnough() {
		return getAbstract().length() >= WikiTransformer.HOW_MANY_CHARACTERS_ARE_TOO_LITTLE;
	}

	public void addImageInfo(String name) {
		//if (!imageUrls.containsKey(name)) {
			//WikiImage wikiImage = new WikiImage(name);
			// wikiImage.setIndex(images.size());
			images.add(name);
			//this.imageUrls.put(name, wikiImage);
		//}//
	}
	
	public boolean hasImage() {
		return images.size() > 0;
	}

	public boolean hasImagesRetrieved() {
	    return imageUrls.size() == images.size();
    }

	public String getFirstImageName() {
		if (images.size() > 0)
			return images.get(0);
		return null;
	}

    /**
     *
     * @return
     */
	public String getFirstImageUrl() {
		String name = this.getFirstImageName();
        return getImageUrlWithName(name);
	}

	public String getImageUrlWithName(String name) {
        if (name != null)
            return imageUrls.get(name);
        return null;
    }

    public String getImageUrl(int index) {
        if (index < 0 || index >= images.size())
            return null;
        return images.get(index);
    }
	
	public void setImageUrl(String name, String url) {
        imageUrls.put(name, url);
	}

	public String getRedirectFrom() {
		return redirectFrom;
	}

	public void setRedirectFrom(String redirectFrom) {
		this.redirectFrom = redirectFrom;
	}

	public int getPrefImageWidth() {
		return prefImageWidth;
	}

	public void setPrefImageWidth(int prefImageWidth) {
		this.prefImageWidth = prefImageWidth;
	}

	public String getOnImageClickLink() {
		return onImageClickLink;
	}

	public void setOnImageClickLink(String onImageClickLink) {
		this.onImageClickLink = onImageClickLink;
	}

	public boolean hasFullText() {
		return hasFullText;
	}

	public void setHasFullText(boolean hasFullText) {
		this.hasFullText = hasFullText;
	}

	public int getFromSource() {
		return fromSource;
	}

	public void setFromSource(int fromSource) {
		this.fromSource = fromSource;
	}
	
	public static List<WikiPage> namesToList(List<String> names, String domain) {
		ArrayList<WikiPage> list = new ArrayList<WikiPage>();
		for (String name : names) {
			WikiPage page = new WikiPage();
			page.setTitle(name);
			page.setDomain(domain);
			list.add(page);
		}
		return list;
	}

	public byte[] getImgBytes() {
		return imgBytes;
	}

	public void setImgBytes(byte[] imgBytes) {
		this.imgBytes = imgBytes;
	}
	
	public int toId() {
		String url = toWikiUrl();
		int id = url.hashCode();
		return id;
	}
	
	public String toWikiUrl() {
		return WikiApi.getInstance().getApiConfig().buildWikipediaUrlWithTitle("http", getDomain(), getTitle());
	}

    public List<String> getImages() {
        return images;
    }

    public String getImage(int i) {
        return images.get(i);
    }

    public static WikiPage newMainPage() {
		WikiPage page = new WikiPage();
		page.setTitle("Main Page");
        return page;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

	public String getCachePath() {
		return cachePath;
	}

    public long getRetrievedTimestamp() {
        return retrievedTimestamp;
    }

    public void setRetrievedTimestamp(long retrievedTimestamp) {
        this.retrievedTimestamp = retrievedTimestamp;
    }

    public void reset() {
        setLoaded(false);
        setLangLinks(null);
    }

    public boolean hasThisImageUrl(String imageName) {
	    return imageUrls.containsKey(imageName);
    }

    public String getFromDb() {
        return fromDb;
    }

    public void setFromDb(String fromDb) {
        this.fromDb = fromDb;
    }
}
