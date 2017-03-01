package au.com.tyo.wiki.wiki.api;

public class ApiList extends ApiQuery {
	
	public ApiList(String value) {
		super();
		
		this.addAttribute("list", value);
	}

}
