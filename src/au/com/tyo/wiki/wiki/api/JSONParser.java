package au.com.tyo.wiki.wiki.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
	
	public static JSONObject getQueryPagesJSONObject(String result) throws JSONException {
		JSONObject jsonObj = null; 
		JSONObject pagesObj = null;

		jsonObj = new JSONObject(result);
		
		if (jsonObj != null) {
			JSONObject qObj = jsonObj.getJSONObject("query");
			
			pagesObj = qObj.getJSONObject("pages");
		}
        return pagesObj;
	}
	
	public static List<?> parseArray(JSONArray arrayObj, String key) throws JSONException {
		List list = new ArrayList();
		for (int i = 0 ; i < arrayObj.length(); ++i) {
			JSONObject obj = arrayObj.getJSONObject(i);
			JSONObject what = obj.getJSONObject(key);
			list.add(what);
		}
		return list;
	}
	
	public static List<String> parseStringArray(JSONArray arrayObj, String key) throws JSONException {
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i = 0 ; i < arrayObj.length(); ++i) {
			JSONObject obj = arrayObj.getJSONObject(i);
			String what = obj.getString(key);
			list.add(what);
		}
		
		return list;
	}
}
