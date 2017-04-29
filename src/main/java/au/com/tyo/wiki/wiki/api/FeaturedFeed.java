package au.com.tyo.wiki.wiki.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import au.com.tyo.utils.StringUtils;
import au.com.tyo.wiki.wiki.WikiApi;
import au.com.tyo.wiki.wiki.WikiPage;
import au.com.tyo.parser.SgmlAttribute;
import au.com.tyo.parser.SgmlNode;
import au.com.tyo.parser.XML;

/*

* action=featuredfeed *
  Returns a featured content feed

This module requires read rights
Parameters:
  feedformat          - The format of the feed
                        One value: rss, atom
                        Default: rss
  feed                - Feed name
                        This parameter is required
                        One value: potd, featured, onthisday
  language            - Feed language code. Ignored by some feeds.
Example:
  Retrieve feed ``potd':
    api.php?action=featuredfeed&feed=potd
    
    
 */

public class FeaturedFeed extends ApiAction {
	
	public static final String FEATURED_FEED_PICTURE_OF_TODAY = "potd";
	
	public static final String FEATURED_FEED_FEATURED = "featured";
	
	public static final String FEATURED_FEED_ON_THIS_DAY = "onthisday";
	
	public 	static final String TAG_DESCRIPTION_START = "<description>";
	
	public static final String TAG_DESCRIPTION_END = "</description>";

	public FeaturedFeed() {
		
		super("featuredfeed");
		
	}
	
	public void addFeedVariable(String type) {
		this.addVariableAttribute("feed", type, true);
	}
	
	public void addFeed(String type) {
		this.addAttribute("feed", type, true);
	}

	/**
	 * make it default 
	 */
	public String getFeaturedFeedUrl() {
		return this.getFeaturedFeedUrl("featured");
	}

	/**
	 * 
	 * @param feedType
	 * @return
	 */
	public String getFeaturedFeedUrl(String feedType) {
		this.addFeedVariable(feedType);
		
		return getUrl();
	}
	
	public static ArrayList<WikiPage> parse(String result, String langCode) {
		ArrayList<WikiPage> list = new ArrayList<WikiPage>();
		return parse(result, list, langCode);
	}
	
	private static ArrayList<WikiPage> parse(String result, ArrayList<WikiPage> list, String landCode) {
		RSS rss = RSS.newFeed(result);
		
		Iterator<au.com.tyo.feed.Channel> it = rss.iterator();
		if (it != null)
			while (it.hasNext()) {
				au.com.tyo.feed.Channel channel = it.next();
				Iterator<Item> itemIt = channel.iterator();
				
				while (itemIt.hasNext()) {
					Item item = itemIt.next();
					parseItemDescription(item.getDescription(), list, landCode);
				}
			}
		return list;
	}
	
	private static void parseItemDescription(String description,
			ArrayList<WikiPage> list, String langCode) {
		
//		String thumbnailLink = null;
		WikiPage page = new WikiPage();
		page.setLangCode(langCode);
		
		String str = XML.unXMLify(description); 
		int start = str.indexOf("<img");
		int byteLength = start; 
		
		if (start > -1) {
			String substr = str.substring(0, start);
			byteLength = substr.getBytes().length;
			
			SgmlNode imgNode = new SgmlNode();
			imgNode.parse(str.getBytes(), byteLength);
			String url = imgNode.getAttribute("src");
			if (url != null)				
				page.setThumbnailLink(WikiApi.getInstance().getApiConfig().completeWikiLink(url));
		}
		
		start = 0;
		while ((start = str.indexOf("title=", start)) > -1) {
			int titleStart = start;
			int aPos = str.lastIndexOf("<a", start);
			
			start += 6;

			if (aPos > -1) {
				String substr = str.substring(0, aPos);
				byteLength = substr.getBytes().length;
				
				SgmlNode aNode = new SgmlNode();
				aNode.parse(str.getBytes(), byteLength);
				String url = aNode.getAttribute("href");
				if (aNode.hasChild("img"))
					continue;
				
				if (url != null) {
					try {
						page.setUrl(URLDecoder.decode(url, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						page.setUrl(URLDecoder.decode(url));
					}
				}
			}

			SgmlAttribute attr = new SgmlAttribute();
			byteLength = str.substring(0, titleStart).getBytes().length;
			attr.parse(str.getBytes(), byteLength);
			if (attr.getValue() != null && attr.getValue().length() > 0) {
				page.setTitle(StringUtils.unescapeHtml(StringUtils.unescapeHtml(attr.getValue())));
				list.add(page);
				break;
			}
		}
		
	}

	public static ArrayList<WikiPage> fastParse(String result, String langCode) {

		ArrayList<WikiPage> list = new ArrayList<WikiPage>();
		
		int end = result.lastIndexOf(TAG_DESCRIPTION_END);
		
		if (end > -1) {
			while (end > -1) {
				int start = result.lastIndexOf(TAG_DESCRIPTION_START, end);
				if (start == -1) {
					parseItemDescription(result.substring(0, end), list, langCode);
					break;
				}
				else
					parseItemDescription(result.substring(start, end), list, langCode);
				
				end = result.lastIndexOf(TAG_DESCRIPTION_END, start);
			}
		}
		else
			parse(result, list, langCode);
		
		return list;
	}
}
