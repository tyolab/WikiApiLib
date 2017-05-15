package au.com.tyo.wiki;

import java.util.Observable;

import au.com.tyo.wiki.wiki.WikiApi;
import au.com.tyo.wiki.wiki.WikiLang;

public class WikiSettings extends Observable {
    public static final int PROGRESS_MAX = 10000;
	
	protected boolean predictionEnabled = false;
	protected boolean nigthThemeEnabled = false;

	/**
	 * It should be always on by default as 2016-07-12
	 *
	 * https://lists.wikimedia.org/pipermail/mediawiki-api-announce/2016-May/000110.html
	 */
	protected boolean sslEnabled = true;
	
	public WikiSettings() {
		WikiApi.initialize(this);
	}

	public boolean isPredictionEnabled() {
		return predictionEnabled;
	}

	public void setPredictionEnabled(boolean predictionEnabled) {
		this.predictionEnabled = predictionEnabled;
		
		if (predictionEnabled)
			WikiApi.getInstance().getApiConfig().useSecureConnection();
	}
	
	public boolean isNigthThemeEnabled() {
		return nigthThemeEnabled;
	}

	public void setNigthThemeEnabled(boolean nigthThemeEnabled) {
		this.nigthThemeEnabled = nigthThemeEnabled;
	}

	public boolean isSecureConnectionEnabled() {
		return sslEnabled;
	}
	
	public void enableSecureConnection(boolean b) {
		sslEnabled = b;
		WikiApi.getInstance().enableSecureConnection(sslEnabled);
	}
	
	public String getWikiName(WikiLang lang) {
		return getWikiName(lang, "%s Wikipedia");
	}
	
	public static String getWikiName(WikiLang lang, String format) {
		return String.format(format, lang.getName());
	}
}
