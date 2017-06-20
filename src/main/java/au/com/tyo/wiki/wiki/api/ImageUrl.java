package au.com.tyo.wiki.wiki.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImageUrl extends ApiQuery {

	public ImageUrl() {
		this.setProp("imageinfo");
		this.setFormat("json");
		
		this.setIiLimit(1);
		this.addIiProp("url");
	}

	private void addIiProp(String v) {
		this.addAttribute("iiprop", v);
	}

	private void setIiLimit(int i) {
		this.setAttribute("iilimit", String.valueOf(i));
	}
	
	public static List<String> parse(String result) {
		List<String> list = new ArrayList<String>();
		try {
			JSONObject pagesObj = JSONParser.getQueryPagesJSONObject(result);
			
			Iterator<?> keys = pagesObj.keys();
			
			while( keys.hasNext() ){
	            String key = (String) keys.next();

	            if(pagesObj.get(key) instanceof JSONObject ){
	            	JSONObject pageObj = (JSONObject) pagesObj.get(key);
	            	
	            	JSONArray tnObj = pageObj.getJSONArray("imageinfo");
	            	
	            	list = JSONParser.parseStringArray(tnObj, "url");
	            }
	        }
		}
		 catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/*
	 * Only get one image a time
	 */
	public static String parseOne(String result) {
		List<String> list = parse(result);
		if (list.size() > 0)
			return list.get(0);
		return "";
	}

	public String getImageUrl(String title) {
		if (!title.startsWith("File:"))
			this.setTitle("File:" + title);
		else
			setTitle(title);
		return getUrl();
	}
}
