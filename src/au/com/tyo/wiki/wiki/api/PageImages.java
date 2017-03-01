package au.com.tyo.wiki.wiki.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import au.com.tyo.wiki.wiki.WikiPage;

/*
 * prop=pageimages (pi) *
  Returns information about images on the page such as thumbnail and presence of photos.

This module requires read rights
Parameters:
  piprop              - What information to return
                         thumbnail - URL and dimensions of image associated with page, if any
                         name - image title
                        Values (separate with '|'): thumbnail, name
                        Default: thumbnail|name
  pithumbsize         - Maximum thumbnail dimension
                        Default: 50
  pilimit             - Properties of how many pages to return
                        No more than 50 (100 for bots) allowed
                        Default: 1
  picontinue          - When more results are available, use this to continue
 */

public class PageImages extends ApiQuery {
	
	public PageImages() {
		super();
		
		this.addAttribute("prop", "pageimages");
		this.setFormat("json");
		
		addPiProp("thumbnail");
	}

	private void addPiProp(String v) {
		addAttribute("piprop", v);
	}

	public void addLimit(int limit) {
		this.addAttribute("pilimit", String.valueOf(limit), true);
	}

	public String getPageImageUrl(List<String> titles) {
		this.addLimitVariable(titles.size());
		this.addTitlesVariable(titles);
		return getUrl();
	}

	private void addLimitVariable(int size) {
		this.addVariableAttribute("pilimit", String.valueOf(size), true);
	}

	public static List<WikiPage> parseResult(String result) {
		ArrayList<WikiPage> list = new ArrayList<WikiPage>();
		
        try {
			JSONObject jsonObj = new JSONObject(result);
			
			if (jsonObj != null) {
				JSONObject qObj = jsonObj.getJSONObject("query");
				
				JSONObject pagesObj = qObj.getJSONObject("pages");
				
				Iterator<?> keys = pagesObj.keys();
					
				while( keys.hasNext() ){
		            String key = (String) keys.next();

		            if(pagesObj.get(key) instanceof JSONObject ){
		            	JSONObject pageObj = (JSONObject) pagesObj.get(key);
		            	
		            	WikiPage page = new WikiPage();
		            	
		            	JSONObject tnObj = pageObj.getJSONObject("thumbnail");
		            	page.setThumbnailLink(tnObj.getString("source"));
		            	page.setTitle(pageObj.getString("title"));
		            	
		            	list.add(page);
		            }
		        }
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return list;
	}
}
