package au.com.tyo.wiki.wiki.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * list=random (rn) *
  Get a set of random pages
  NOTE: Pages are listed in a fixed sequence, only the starting point is random.
        This means that if, for example, "Main Page" is the first random page on
        your list, "List of fictional monkeys" will *always* be second, "List of
        people on stamps of Vanuatu" third, etc
  NOTE: If the number of pages in the namespace is lower than rnlimit, you will
        get fewer pages. You will not get the same page twice
  https://www.mediawiki.org/wiki/API:Random

This module requires read rights
Parameters:
  rnnamespace         - Return pages in these namespaces only
                        Values (separate with '|'): 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 100, 101, 108, 109, 118, 119, 710, 711, 446,
                            447, 828, 829
                        Maximum number of values 50 (500 for bots)
  rnlimit             - Limit how many random pages will be returned
                        No more than 10 (20 for bots) allowed
                        Default: 1
  rnredirect          - Load a random redirect instead of a random page
Example:
  api.php?action=query&list=random&rnnamespace=0&rnlimit=2
Generator:
  This module may be used as a generator
 */
public class ListRandom extends ApiList {

	public ListRandom() {
		super("random");
		
		addNamespace(0);
		this.setFormat("json");
	}

	public void addLimitVariable(int limit) {
		this.addVariableAttribute("rnlimit", String.valueOf(limit), true);
	}
	
	public void 	addNamespace(int namespace) {
		this.addAttribute("rnnamespace", String.valueOf(namespace));
	}
	
	public void addLimit(int limit) {
		this.addAttribute("rnlimit", String.valueOf(limit), true);
	}
	
	public void setRedirectPageOnly() {
		this.addAttribute("rnredirect", (String) null, true);
	}
	
	/**
	 *  {"query":{"random":[{"id":16415810,"ns":0,"title":"Eugen Hoenig"}]}}
	 *  
	 * @param result
	 * @return
	 */
	public static List<String> parseResult(String result) {
		ArrayList<String> list = new ArrayList<String>();
		
        try {
			JSONObject jsonObj = new JSONObject(result);
			
			if (jsonObj != null) {
				JSONObject parsedObject =jsonObj.getJSONObject("query");
				
				JSONArray array = parsedObject.getJSONArray("random");
				
	        	 if (array.length() > 0) 
		        	 for (int i = 0; i < array.length(); i++) {
		        		 JSONObject object = array.getJSONObject(i);
		        		 
		        		 list.add(object.getString("title"));
		        	 }
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
//        if (list.size() == 0)
//			list.add(ApiBase.WIKIPEDIA_MAIN_PAGE);
		return list;
	}


	public String getListRandomPageUrl(int limit) {
		this.addLimitVariable(limit);
		
		return getUrl();
	}
	
}
