package au.com.tyo.wiki.wiki;

import java.io.Serializable;

public class PageLang implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1832626980041250805L;
	
	String url;
	String lang;
	String langCode;
	String title;
	
	public String getUrl() {
		return url;
	}
	
	public String getLang() {
		return lang;
	}
	
	public String getTitle() {
		return title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
	
}