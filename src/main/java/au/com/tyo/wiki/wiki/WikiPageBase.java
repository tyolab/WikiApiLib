package au.com.tyo.wiki.wiki;

import au.com.tyo.CommonSettings;
import au.com.tyo.web.HtmlStyleAndScript;
import au.com.tyo.web.PageBuilder;
import au.com.tyo.web.PageInterface;

public class WikiPageBase implements PageInterface {
	
	
	
	protected static String themeName;
	
	protected static String inlineCss;
	
	protected String[] stylesAndScripts;
	
	protected String[] themeCss;
	
	protected String[] landscapeDependentCss;
	
	protected String title;
	
	public WikiPageBase() {
	}
	
	public String getTitle() {
		return title;
	}

	public String getThemeName() {
		return themeName;
	}

	public static void setThemeName(String themeName) {
		WikiPageBase.themeName = themeName;
	}
	
	public static void setInlineCss(String inlineCss) {
		WikiPageBase.inlineCss = inlineCss;
	}

	@Override
	public String createHtmlContent() {
		return "";
	}

	@Override
	public String createStyleAndScript() {
		String path = "";
		if (CommonSettings.isAndroid())
			path = PageBuilder.getAndroidAssetPath();
		
		HtmlStyleAndScript styleBuilder = new HtmlStyleAndScript(path, themeName, 
				CommonSettings.isLandscapeMode(), CommonSettings.isTablet(), 
				CommonSettings.getDevice());
		return styleBuilder.build(stylesAndScripts, this.themeCss, landscapeDependentCss, inlineCss, HtmlStyleAndScript.DEVICE_DEPENDANT_JS) ;
	}

	@Override
	public String createTitle() {
		if (title != null) 
			return String.format(WikiHtml.HTML_ARTICLE_TITLE, title);
//		if (html_title_div != null)
//			sb.append(String.format(html_title_div, title));
//		else
//			sb.append(String.format("<div id=\"title\">%s</div><br>", title));
		return "";
	}
}
