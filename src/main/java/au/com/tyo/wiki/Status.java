package au.com.tyo.wiki;

import au.com.tyo.wiki.wiki.WikiArticle;

public class Status {

	public static boolean isStatusAcceptable(int status) {
		return status == WikiArticle.STATUS_OK || 
				(status >= WikiArticle.STATUS_IN_OTHER_PART_FULL && status < WikiArticle.STATUS_IN_OTHER_PART);
	}
	
}
