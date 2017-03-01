package au.com.tyo.wiki.wiki.api;

public class ApiQueryListSearch extends ApiList {
	
	public ApiQueryListSearch() {
		super("search");
		
		this.setFormat("json");
//		this.setLimit(50);
		
		this.addAttribute("srnamespace", "0");
		this.addAttribute("continue", "");
	}

	private void setLimit(int i) {
		this.setVariableAttribute("srlimit", String.valueOf(i));
	}
	
	private void setQuery(String what) {
		this.setVariableAttribute("srsearch", what);
	}
	
	private void setOffset(int offset) {
		this.setVariableAttribute("sroffset", String.valueOf(offset));
	}
	
	public String getSearchUrl(String what, int offset, int limit) {
		return getSearchUrl(what, ApiConstants.SEARCH_TITLE, offset, limit);
	}

	public String getSearchUrl(String what, int searchType, int offset, int limit) {
		setQuery(what);
		setOffset(offset);
		setLimit(limit);
		setSearchType(searchType);
		
		return getUrl();
	}

	private void setSearchType(int searchType) {
		String v;
		if (searchType == ApiConstants.SEARCH_TITLE) 
			v = "title";
		else if (searchType == ApiConstants.SEARCH_TEXT)
			v = "text";
		else
			v =  "nearmatch";
		
		this.setVariableAttribute("srwhat", v);
	}
}
