package au.com.tyo.wiki.wiki.api;

public interface ApiConstants {
	
	/*
	 * Api -> Query -> List -> Search
	 */
	public int SEARCH_DEFAULT = -1;	
	public int SEARCH_TITLE = 0;	
	public int SEARCH_TEXT = 1;	
	public int SEARCH_NEARMATCH = 2;
	
	public int SEARCH_ENGINE_LUCENE = 0;	
	public int SEARCH_ENGINE_CIRRUS = 1;
}
