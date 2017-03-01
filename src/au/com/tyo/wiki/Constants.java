package au.com.tyo.wiki;

public interface Constants {
	
	/*
	 * page request response code
	 * ==========================
	 * try to make it as same as possible with the http status code
	 * 
	 */
	public static final int STATUS_OK = 200;
	
	public static final int STATUS_NOT_FOUND = -1;
	public static final int STATUS_IN_OTHER_PART = 700; // > 0, mean the part number it belongs to
	public static final int STATUS_IN_OTHER_PART_FULL = 600; // has abstract, but full article is in other part, > 0, mean the part number it belongs to
	
	public static final int STATUS_PAGE_NOT_FOUND = 404;
	
	public static final int STATUS_PAGE_ERROR = 1000; 
	public static final int STATUS_PAGE_ERROR_PARSED_JSON = 1001;
	public static final int STATUS_PAGE_ERROR_PARSED_JSON_MOBILEVIEW = 1002;
	
	public static final int POSITION_CENTER = 0;
	public static final int POSITION_LEFT = 1;
	public static final int POSITION_RIGHT = 2;
	
	public static final String POSITION_CENTER_STRING = "center";
	public static final String POSITION_LEFT_STRING = "left";
	public static final String POSITION_RIGHT_STRING = "right";
	
	public static final String POSITION_STRINGS[] = {POSITION_CENTER_STRING, POSITION_LEFT_STRING, POSITION_RIGHT_STRING };
}
