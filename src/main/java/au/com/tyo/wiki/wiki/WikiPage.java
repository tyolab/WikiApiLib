package au.com.tyo.wiki.wiki;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.tyo.io.IO;
import au.com.tyo.lang.CJK;
import au.com.tyo.utils.StringUtils;
import au.com.tyo.utils.TextUtils;
import au.com.tyo.web.PageInterface;

public class WikiPage extends WikiPageBase implements Serializable, PageInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1864682452543190800L;
	
	private static String[] STYLES_N_SCRIPTS = {"jquery-1.3.2.min.js", "ready.js", "collapsible.js", "common.js", "wiki.css", "wikipedia.css", "wikipedia_align.css"};
	
	private static String[] THEME_DEPENDANT_CSS = {"page.css"};
	
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
	
	private String baseUrl;
	
	private byte[] bytes = null; // the text in bytes which may be modified by removing the search bar and title
	
	private byte[] imgBytes = null; // the image content associated with the page
	
	private List<PageLang> langs; // in available languages
	
	private List<WikiPageSection> sections;
	
	private int	sectionCount;
	
	private String fromAnchor;
	
	private String langCode;
	
	private String abs;
	
	private Error error;

	private Request request;
	
	private String html;
	
	private boolean didYouMean;
	
	private String thumbnailLink;
	
	private Map<String, WikiImage> imageUrls;

    private List<WikiImage> images;
	
	private WikiPage xPage; // the page being cross linked
	
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
	
	private int id;
//	
//	private int namespace;
	static {
		pageProcessor = null;
	}
	
	public WikiPage(String text) {
		this.setText(text);
		
		init();
	}

	public WikiPage(File file) {
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
		langs = new ArrayList<PageLang>();
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
		langCode = "";
		redirectFrom = null;
		xPage = null;
		html = null;
		textFormat = TEXT_FORMAT_JSON;
		notes = null;
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
			resetPage();
		return bytes;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean hasOtherLanguages() {
		return langs.size() > 0;
	}
	
	public List<PageLang> getLangs() {
		return langs;
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

	public void resetPage() {
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

	public void setLangCode(String lang) {
		this.langCode = lang;
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
	}

	public void setHtml(String html) {
		if (pageProcessor != null)
			this.html = pageProcessor.process(html);
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
		if (!imageUrls.containsKey(name)) {
			WikiImage wikiImage = new WikiImage(name);
			wikiImage.setIndex(images.size());
			images.add(wikiImage);
			this.imageUrls.put(name, wikiImage);
		}
	}
	
	public boolean hasImage() {
		return imageUrls.size() > 0;
	}

	public String getFirstImageName() {
		if (images.size() > 0)
			return images.get(0).getTitle();
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
            return imageUrls.get(name).getImageUrl();
        return null;
    }

    public String getImageUrl(int index) {
        if (index < 0 || index >= images.size())
            return null;
        return images.get(index).getImageUrl();
    }
	
	public void setImageUrl(String name, String url) {
        WikiImage wikiImage = imageUrls.get(name);
        if (null != wikiImage)
            wikiImage.setImageUrl(url);
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
			page.setLangCode(domain);
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
		return WikiApi.getInstance().getApiConfig().buildWikipediaUrlWithTitle("http", getLangCode(), getTitle());
	}

    public List<WikiImage> getImages() {
        return images;
    }

    public WikiImage getImage(int i) {
        return images.get(i);
    }

    public static WikiPage newMainPage() {
		WikiPage page = new WikiPage();
		page.setTitle("Main Page");
        return page;
    }
}
