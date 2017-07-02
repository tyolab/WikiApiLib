package au.com.tyo.wiki.wiki.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class ApiQuery<T> extends ApiAction<T> {

	public ApiQuery() {
		super("query");
		
		/* make this by default */
		this.addAttribute("redirects", "");
	}
	
	public void addTitlesVariable(List<String> titles) {
		this.addVariableAttribute("titles", titles, true);
	}
	
	public void addProp(List<String> props) {
		this.addAttribute("prop", props);
	}
	
	public void setProp(String prop) {
		this.setAttribute("prop", prop);
	}
	
	public void addTitlesVariable(String title) {
		addTitlesVariable(Arrays.asList(new String[] {title}));
	}
	
	public void addProp(String prop) {
		addProp(Arrays.asList(new String[] {prop}));
	}
	
	public void setTitles(List<String> titles) {
		this.addTitlesVariable(titles);
	}
	
	public void setTitle(String title) {
		this.setTitles(Arrays.asList(new String[] {title}));
	}

	public String getAcquireTokenUrl() {
		this.setFormat("json");
		this.setAttribute("meta", "tokens");
		
		return getUrl();
	}
	
	/**
	 * there could be many type of tokens
	 * 
	 */
	public static String parseAcquireTokenResultJson(String result) {
		String token = null;
		
		try {
			JSONObject obj = new JSONObject(result);
			
			JSONObject queryObj = obj.getJSONObject("query");
			
			JSONObject tokensObj = queryObj.getJSONObject("tokens");
			
			token = tokensObj.getString("csrftoken");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return token;
	}
}
