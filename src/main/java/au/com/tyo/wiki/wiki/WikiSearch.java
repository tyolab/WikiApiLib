package au.com.tyo.wiki.wiki;

import java.io.Serializable;
import java.util.Comparator;

public class WikiSearch implements Serializable, Comparator<WikiSearch>, Comparable<WikiSearch> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5689363818107372144L;

	public static final String TEMPLATE = "<!doctype html>	<html>  <head>   <meta charset=\"utf-8\"> <style type=\"text/css\">"
			+ " .searchmatch {font-weight: bold; font-style: normal;}	"
			+ "</style></head> <body><div >%s</div></body></html>"; 
	
	public static final String HIGHLIGHT_COLOR = "#888888";
	
	public static String highlightColor = HIGHLIGHT_COLOR;
	
	private String title;
	
	private String snippet;
	
	private String snippetHtml;
	
	private long id;
	
	private int rank;
	
	private double rsv;
	
	private String domain;

	public WikiSearch() {
		snippetHtml = null;
	}

	public WikiSearch(String text) {
		this.title = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public double getRsv() {
		return rsv;
	}

	public void setRsv(double rsv) {
		this.rsv = rsv;
	}

	public String toString() {
		return title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setSnippetHtml(String snippetHtml) {
		this.snippetHtml = snippetHtml;
	}
	
	public String getSnippetHtml() {
		return snippetHtml;
	}

	public void buildSnippetHtml(String query) {
		this.snippetHtml = this.snippet.replaceAll(query, String.format("<em><font color='%s'>" + query + "</font></em>", highlightColor)); //String.format(TEMPLATE, snippet);
	}

	@Override
	public int compareTo(WikiSearch another) {
		return this.getRsv() > another.getRsv() ? -1 : (this.getRsv() < another.getRsv() ? 1 : 0);
	}

	@Override
	public int compare(WikiSearch lhs, WikiSearch rhs) {
		return lhs.compareTo(rhs);
	}
}
