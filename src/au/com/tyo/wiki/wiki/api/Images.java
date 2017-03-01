package au.com.tyo.wiki.wiki.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import au.com.tyo.wiki.wiki.WikiPage;

public class Images extends ApiQuery {
	
	public Images() {
		
		this.setProp("images");
		this.setFormat("json");
		
		this.addImageLimit(1);
	}

	private void addImageLimit(int i) {
		this.setAttribute("imlimit", String.valueOf(i));
	}
	
	public String getImagesUrl(String title) {
		this.setTitle(title);
		
		return getUrl();
	}
	
	public static List<String> parse(String result) {
		List<String> list = new ArrayList<String>();
		
		try {
			JSONObject pagesObj = JSONParser.getQueryPagesJSONObject(result);
			
			if (pagesObj != null) {
				Iterator<?> keys = pagesObj.keys();
				
				while( keys.hasNext() ){
		            String key = (String) keys.next();

		            if(pagesObj.get(key) instanceof JSONObject ){
		            	JSONObject pageObj = (JSONObject) pagesObj.get(key);
		            	
		            	JSONArray tnObj = pageObj.getJSONArray("images");
		            	
		            	list = JSONParser.parseStringArray(tnObj, "title");
//		            	for (String str : list)
//		            		images.put(str, null);
		            }
		        }
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;
	}

}
