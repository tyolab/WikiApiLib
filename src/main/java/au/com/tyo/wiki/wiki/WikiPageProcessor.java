/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.wiki.wiki;

public interface WikiPageProcessor {
	
	public String process(String fromDb, String html);

	public void processUrl(String url);
	
}
