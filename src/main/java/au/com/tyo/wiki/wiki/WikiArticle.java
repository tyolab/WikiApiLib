package au.com.tyo.wiki.wiki;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import au.com.tyo.io.GZIP;
import au.com.tyo.parser.XML;
import au.com.tyo.utils.StringUtils;
import au.com.tyo.wiki.Constants;
import au.com.tyo.wiki.Status;

public class WikiArticle implements Constants {
	
	public static final String TABLE_NAME = "articles";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_ARTICLE_ID = "article_id";
//	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_ABSTRACT = "abstract";
	public static final String COLUMN_ARTICLE = "article";
	public static final String COLUMN_LAST_UPDATE = "last_update";
	public static final String COLUMN_LAST_VISIT = "last_visit";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_REDIRECT = "redirect";
	
	/*
	 * we keep abstract and article in different places
	 */
	public static final int ARTICLE_VERSION_1 = 1;
	
	/*
	 * article and abstract all store in article filed
	 */
	public static final int ARTICLE_VERSION_2 = 2;
	
	public static final int TYPE_UNKNOWN = -1;
	
	public static final int TYPE_ABSTRACT = 1;
	
	public static final int TYPE_ABSTRACT_V2 = 3;
	
	public static final int TYPE_ARTICLE = 2;
	
	public static final String SQL_INSERT = "INSERT INTO " +
				TABLE_NAME  + " (" + /*COLUMN_ID + ", " 
											+ */COLUMN_ARTICLE_ID + ", " 
											+ COLUMN_TITLE  + ", " 
											+ COLUMN_ABSTRACT  + ", " 
											+ COLUMN_ARTICLE + ") VALUES (?, ?, ?, ?)";
	
	public static final String SQL_INSERT_V2 = "INSERT INTO " +
			TABLE_NAME  + " (" + /*COLUMN_ID + ", " 
										+ */COLUMN_ID + ", " 
										+ COLUMN_ARTICLE_ID  + ", " 
										+ COLUMN_ARTICLE  + ", " 
										+ COLUMN_REDIRECT + ") VALUES (?, ?, ?, ?)";
	
	public static final String SQL_INSERT_ARTICLE_TITLE = "INSERT INTO " +
			TABLE_NAME  + " (" + /*COLUMN_ID + ", " 
										+ */COLUMN_ARTICLE_ID + ", " 
										+ COLUMN_TITLE + ") VALUES (?, ?)";
	
	public static final String SQL_UPDATE_ARTICLE = "UPDATE " +
			TABLE_NAME  + " set " + /*COLUMN_ID + ", " 
										+ */COLUMN_ABSTRACT + "=?" 
										+ " and "
										+ COLUMN_ARTICLE + "=?"
										+ " where " + COLUMN_ARTICLE_ID + "=?";
	
	public static final String SQL_UPDATE_ARTICLE_ABSTRACT = "UPDATE " +
			TABLE_NAME  + " set " + COLUMN_ABSTRACT + "=?" 
										+ " where " + COLUMN_ARTICLE_ID + "=?";
	
	public static final String SQL_UPDATE_ARTICLE_ARTICLE = "UPDATE " +
			TABLE_NAME  + " set " + COLUMN_ARTICLE + "=?"
										+ " where " + COLUMN_ARTICLE_ID + "=?";

	
	public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS articles"
			+ " (id integer primary key autoincrement, article_id integer, title text, "
			+ "abstract blob, article blob, last_update date, last_visit date, category text)";
	
	public static final String SCHEMA_V3 = "CREATE TABLE IF NOT EXISTS articles"
			+ " (id integer primary key autoincrement, article_id integer,"
			+ "article blob, redirect ingeter)";
	
	public static final String SQL_GET_MIN_ID = "SELECT MIN(id) FROM ";
	
	public static final String SQL_GET_MAX_ID = "SELECT MAX(id) FROM ";
	
	public static final String SQL_GET_ARTICLE_TITLE = "SELECT title FROM ";

	public static final int MODE_PLAIN_TEXT = 0;
	public static final int MODE_COMPRESSED_GZIP = 1;
	
	public static int version = ARTICLE_VERSION_2;
	
	public static int defaultMode = MODE_COMPRESSED_GZIP;
	
	private long id;
	
//	private int type; // 0 for abstract, 1 for full article
	private int articleId;
	
	private String title;
	
	private byte[] article; // article blob, may in gzip compressed mode
	
	private int mode;
	
	private byte[] abs;
	
	private boolean redirect;
	
	private boolean fromRedirect;
	
	private String redirectFrom;
	
	private int status;
	
	private int type;
	private String articleUrl;

	public WikiArticle() {
		this(MODE_COMPRESSED_GZIP);
	}
	
	public WikiArticle(int mode) {
		this.mode = mode;
		this.articleId = 0;
		this.redirect = false;
		this.fromRedirect = false;
		this.setStatus(STATUS_OK);
		
		init();
	}
	
	public WikiArticle(WikiArticle article) {
		this.mode = article.getMode();
		this.id = article.getId();
		this.articleId = article.getArticleId();
		this.redirect = article.isRedirect();
		this.fromRedirect = article.isFromRedirect();
		this.title = article.getTitle();
		
		init();
	}
	
	private void init() {
		setType(TYPE_UNKNOWN); 
	}

	public String articleToString() {
		String temp = "";
		if (null != article) {
            if ((version != ARTICLE_VERSION_1 || (version == ARTICLE_VERSION_1 && !redirect)) && mode == MODE_COMPRESSED_GZIP) {
                try {
                    temp = GZIP.decompress(article); //.getBytes(), "UTF-8");
                } catch (IOException e) {
                    mode = MODE_PLAIN_TEXT;
                    redirect = true;
                }

                if (article.length > 0 && temp.length() == 0) {
                    redirect = true;
                    mode = MODE_PLAIN_TEXT;
                }
            }

            if ((redirect && version == ARTICLE_VERSION_1) || mode == MODE_PLAIN_TEXT)
                try {
                    temp = new String(article, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    temp = new String(article);
                }
        }
		return temp;
	}
	
	public String abstractToString() {
		if (abs != null)
			return bytesToString(abs);
		return articleToString();
	}
	
	public String toXml() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<doc>\n");
		buffer.append(String.format("<docid>%d<docid>\n", id));
		buffer.append(String.format("<title>%s</title>\n", title));
		buffer.append(String.format("<abstract>%s</abstract>\n", abstractToString()));
		buffer.append("</doc>\n");
		
		return buffer.toString();
	}
	
	private String bytesToString(byte[] bytes) {
		String temp = "";
		if (mode == MODE_COMPRESSED_GZIP)
			try {
				temp = GZIP.decompress(bytes);
			} catch (IOException e) { }		
		
		if ((mode == MODE_COMPRESSED_GZIP && bytes.length > 0 && temp.length() == 0)
				|| mode == MODE_PLAIN_TEXT)
			try {
				temp =  new String(bytes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				temp =  new String(bytes);
			}
		return temp;
	}
	
//	public String toString() {
//		return bytesToString(article);
//	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

//	public int getType() {
//		return type;
//	}
//
//	public void setType(int type) {
//		this.type = type;
//	}

	public byte[] getArticle() {
		return article;
	}

	public void setArticle(byte[] article) {
		this.article = article;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public byte[] getAbstract() {
		return abs;
	}

	public void setAbstract(byte[] abs) {
		this.abs = abs;
	}
	
	public static WikiArticle parseAbstract(String doc, int mode) {
		WikiArticle article = new WikiArticle(mode);
		
		String text = XML.getElementText(doc, "title");
		String[] tokens = text.split("[:|：]");
		if (tokens != null && tokens.length > 1)
			article.setTitle(tokens[1].trim());
		else
			article.setTitle(text.trim());
		
		text = XML.getElementText(doc, "docid");
		if (text.length() > 0)
			article.setArticleId(Integer.valueOf(text));
			
		text = XML.getElementText(doc, "abstract");
		if (text.length() > 0) {
			byte[] blob;
			if (mode == MODE_COMPRESSED_GZIP)
				try {
					blob = GZIP.compress(text);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			else
				blob = text.getBytes();
			article.setAbstract(blob);
			return article;
		}
		return null;
	}
	
	public boolean isRedirect() {
		return redirect;
	}

	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isFromRedirect() {
		return fromRedirect;
	}

	public void setFromRedirect(boolean fromRedirect) {
		this.fromRedirect = fromRedirect;
	}

	public String getRedirectFrom() {
		return redirectFrom;
	}

	public void setRedirectFrom(String redirectFrom) {
		this.redirectFrom = redirectFrom;
	}
	
	public void toPage(WikiPage page) throws Exception {
		articleToPage(this, page, articleToString(), -1, WikiArticle.TYPE_ARTICLE, true);
	}
	
	public static void articleToPage(WikiArticle article, WikiPage page, String text, int parsingNeeded, 
			int articleType, boolean hasFullText) throws Exception {
		/*
		 * don't set the article status/request response code here
		 */
//		if (null != page.getRequest())
//			page.getRequest().setResponseCode(200);
        page.setUrl(article.getArticleUrl());
		page.setTitle(article.getTitle());
		page.setFromSource(WikiPage.FROM_SOURCE_LOCAL);
		// if page is from local source, the page id is the id for the record, not the article id which is same with the one in Wikipedia
		//page.setId(article.getArticleId());
		if (article.isFromRedirect())
			page.setRedirectFrom(article.getRedirectFrom());
		if (articleType == WikiArticle.TYPE_ARTICLE/*WikiDataSource.dataType != WikiArticle.TYPE_ABSTRACT*/) {
			page.setText(text);
			page.setHasFullText(hasFullText/*WikiDataSource.dataType == WikiArticle.TYPE_ARTICLE*/);
			if (parsingNeeded != 0) {
				try {
					WikiParser.parseJsonArticleText(text, page, parsingNeeded);
				}
				catch (Exception ex) {
					throw ex;
				}
			}
		}
		else {
			String artText = text;
			if (articleType == WikiArticle.TYPE_ABSTRACT)
				artText = article.abstractToString();
			else if (articleType == WikiArticle.TYPE_ARTICLE)
				article.articleToString();
			page.setAbstract(StringUtils.unescapeHtml(artText));
		}
	}
	
	public boolean usable() {
		return Status.isStatusAcceptable(getStatus());
	}

	public void setArticleUrl(String url) {
		articleUrl = url;
	}

	public String getArticleUrl() {
		return articleUrl;
	}

    @Override
    public String toString() {
        return "WikiArticle{" +
                "title='" + title + '\'' +
                ", status=" + status +
                '}';
    }
}
