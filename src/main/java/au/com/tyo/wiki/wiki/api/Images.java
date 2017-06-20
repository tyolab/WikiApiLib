package au.com.tyo.wiki.wiki.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Images extends ApiQuery {
	
	public Images() {
		
		this.setProp("images");
		this.setFormat("json");
		
		this.addImageLimit(2);
	}

	private void addImageLimit(int i) {
		this.setAttribute("imlimit", String.valueOf(i));
	}

    /**
     *
     * @param text
     * @return
     */
    @Override
    protected List parseInternal(String text) {
        return parse(text);
    }

    /**
     *
     * @param title
     * @return
     */
	public String getImagesUrl(String title) {
		this.setTitle(title);
		
		return getUrl();
	}


    /**
     *
     * @param result
     * @return
     */
	public static List<String> parse(String result) {
		List<String> list = null;
		
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
                        break;
		            }
		        }
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (null == list)
			list = new ArrayList<String>();

		return list;
	}

}
