package au.com.tyo.wiki.wiki;

import au.com.tyo.web.PageBuilder;

public class WikiHtml extends PageBuilder {
	
	public static final String HTML_SECTION_TEMPLATE_LEVEL = "<div class=\"wikisection\" id=\"%s\">\n";
	public static final String HTML_SECTION_TEMPLATE_CONTENT = "\t<div class=\"%ssection_content\" id=\"%s\" style=\"\">\n%s\n\t"; // id=\"%s\"
	public static final String HTML_SECTION_TEMPLATE_LEVEL2 = "<div class=\"wikisection\" id=\"%d\"><h3>%s</h3>\n <br>\n";
	public static final String HTML_SECTION_TEMPLATE_LEVEL3 = "\t<div class=\"wikisection\" id=\"%d\"><h4>%s</h4>\n <br>\n";
	public static final String HTML_SECTION_TEMPLATE_LEVEL4 = "\t\t<div class=\"wikisection\" id=\"%d\"><h5>%s</h5>\n <br>\n";
//	public static final String HTML_SECTION_TITLE = "\t<a href=\"#%s\" class=\"clickme\" onclick=\"toggleDiv('%s', this)\"><div class=\"html_section_title\"><h%d>%s</h%d></div></a>\n";
	//  title=\"%s\"
	public static final String HTML_SECTION_TITLE = "\t<h%d class=\"html_section_title\" tabindex=\"0\" role=\"button\" aria-pressed=\"false\" onclick=\"handleExpandCall('%s', this)\" id='%s'><span class=\"wt-headline\">%s</span></h%d>\n";
	public static final String HTML_SUBSECTION_TITLE = "\t\t<h%d class=\"html_subsection_title\"><span>%s</span></h%d>\n";
	public static final String HTML_SECTION_DIV_END = "</div>\n";
	public static final String HTML_ARTICLE_TITLE = "" +
			"<div class=\"article_title\" id=\"article_title\"><h2>%s</h2></div>\n";
	private static final String HTML_ABSTRACT_TEMPLATE_CONTENT = "\t<div class=\"abstract_content\" id=\"%s\">\n%s\n\t"; // id=\"%s\";

	public static final String HTML_STYLES_N_SCRIPTS = 
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"wiki.css\" />" +
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/wiki.css\" />" + 
			"<script type=\"text/javascript\" src=\"collapsible.js\" ></script>" +
			"<script type=\"text/javascript\" src=\"file:///android_asset/collapsible.js\" ></script>" +
			"<script type=\"text/javascript\" src=\"common.js\" ></script>" +
			"<script type=\"text/javascript\" src=\"file:///android_asset/common.js\" ></script>"
			;
	
	public static final String HTML_SECTION_HIDE_N_SHOW = "    <a href=\"#%s\" class=\"hide\" id=\"h-%s\">+</a>" + 
		    "<a href=\"#%s\" class=\"show\" id=\"s-%s\">-</a>";

	private static final int SECTION_HEADER_SIZE = 3;
	
	public static String html_section_div;
	public static String html_section_title;
	public static String html_section_content;
	public static String html_section_hide_n_show;
	
	public static String html_subsection_title;
	
	public static String html_styles_n_scripts;
	
	public static String html_section_content_no_hide_n_show;

	public static int section_header_size;
	
	static {
		html_section_content_no_hide_n_show  = HTML_ABSTRACT_TEMPLATE_CONTENT;
		
		html_section_div = HTML_SECTION_TEMPLATE_LEVEL;
		html_section_title = HTML_SECTION_TITLE;
		html_section_content = HTML_SECTION_TEMPLATE_CONTENT;
		html_section_hide_n_show = HTML_SECTION_HIDE_N_SHOW;
		
		html_subsection_title = HTML_SUBSECTION_TITLE;

		html_styles_n_scripts = HTML_STYLES_N_SCRIPTS;
		
		section_header_size = SECTION_HEADER_SIZE;
	}
	
	public WikiHtml() {
		html_title_div = HTML_ARTICLE_TITLE;
	}
	
	/**
	 * old implementation of sections to html 
	 * @param page
	 * @return
	 */
	public String loopSections(WikiPage page) {
		int headerSize = 3;
		int lowestLevel = 0;
		int offset = 0;
		int stack = 0;
		
		StringBuffer sb = new StringBuffer();
		StringBuffer subSection = new StringBuffer();
		WikiPageSection preSection = null;
		WikiPageSection section = null;
		
		for (int i = 0; i < page.getSectionSize(); ++i) {
			section = page.getSection(i);
			
			if (section.getLevel() > 0) {
				if (section.getLevel() < lowestLevel || lowestLevel == 0)
					lowestLevel = section.getLevel();
				
				offset = (section.getLevel() - lowestLevel);
				headerSize = section_header_size + offset;
				
				if (preSection != null && section.getLevel() <= preSection.getLevel()) {
//					closeDiv(subSection);
//					--stack;
//					sb.append(subSection);				
//					if (section.getLevel() <= preSection.getLevel()) {
						int step = preSection.getLevel() - section.getLevel() + 1;
						closeDiv(subSection, step);
//						sb.append(subSection);
//						subSection.setLength(0);
						stack -= step;
						
						if (stack < 0)
							stack = 0;
//					}
//					else if (section.getLevel() == preSection.getLevel()) {
//						closeDiv(subSection);
//						--stack;
//					}
//					else {
//						stack += section.getLevel() - preSection.getLevel();
//					}
				}
				
				subSection.append(sectionToHtml(section, headerSize, "section"));
				stack++;
			}
			else {
				appendToBuffer(sb, subSection, stack);
				
				sb.append(section.getText());
				sb.append("\n");
			}
			
			preSection = section;
		}
		
		appendToBuffer(sb, subSection, stack);
		return sb.toString();
	}
	
	public static String sectionsToHtml(WikiPage page) {
		StringBuffer sb = new StringBuffer();
		sb.append("<div id=\"content\" class=\"content\">\n");
		WikiPageSection section = null;
		for (int i = 0; i < page.getSectionSize(); ++i) {
			section = page.getSection(i);
			
			sb.append(section.toHtml(WikiHtml.section_header_size));
		}
		closeDiv(sb);
		return sb.toString();
	}
	
	public static String createFooter() {
		return "\n\n<br><br><div id=\"footer\"><ul>Content is licensed under <a rel=\"nofollow\" href=\"http://creativecommons.org/licenses/by-sa/3.0/\">CC BY-SA 3.0</a> </li></ul></div>";
	}

	public void appendToBuffer(StringBuffer strBuffer, StringBuffer subBuffer, int stack) {
		if (subBuffer.length() > 0) {
			closeDiv(subBuffer, stack);
			strBuffer.append(subBuffer);
			subBuffer.setLength(0);
		}
	}
	
	/**
	 * opening up div without closing it yet
	 * <div>
	 * where is </div>?
	 */
	public static String sectionToHtml(WikiPageSection section, int headerSize, String sectionId) {
		StringBuffer sectionHtml = new StringBuffer();
		sectionHtml.append(String.format(html_section_div, section.getLevel()));
		if (section.getTitle() != null && section.getTitle().length() > 0) {
			String titleHtml = String.format(html_section_title, headerSize, "secc" + section.getIdName(), section.getIdName(), section.getTitle(), headerSize);
			sectionHtml.append(titleHtml);
			sectionHtml.append(String.format(html_section_content, sectionId, section.getText()));
		}
		return sectionHtml.toString();
	}
	
	public void closeDiv(StringBuffer sb, int stack) {
		for (int j = 0; j < stack; ++j)
			closeDiv(sb);
	}

}
