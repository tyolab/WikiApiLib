package au.com.tyo.wiki.wiki;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import au.com.tyo.io.ItemSerializable;

public class PageLang extends ItemSerializable {

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

	@Override
	public void serialise(ObjectOutputStream stream) throws IOException {
		stream.writeObject(url);
		stream.writeObject(lang);
		stream.writeObject(langCode);
		stream.writeObject(title);
	}

	@Override
	public void deserialise(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		url = (String) stream.readObject();
		lang = (String) stream.readObject();
		langCode = (String) stream.readObject();
		title = (String) stream.readObject();
	}
}