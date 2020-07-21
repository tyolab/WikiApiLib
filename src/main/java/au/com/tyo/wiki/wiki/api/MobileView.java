package au.com.tyo.wiki.wiki.api;

/*
 * The MobileView API for retrieving Wikipedia article in a format suitable for viewing on mobile device
 */

/*////
 * action=mobileview *
  Returns data needed for mobile views
  https://www.mediawiki.org/wiki/Extension:MobileFrontend#action.3Dmobileview

This module requires read rights
Parameters:
  page                - Title of page to process
                        This parameter is required
  redirect            - Whether redirects should be followed
                        One value: yes, no
                        Default: yes
  sections            - Pipe-separated list of section numbers for which to return text. `all' can be used to return for all. Ranges in format '1-4' mean get sections 1,2,3,4. Ranges without second number, e.g. '1-' means get all until the end. `references' can be used to specify that all sections containing references should be returned.
  prop                - Which information to get
                         text            - HTML of selected section(s)
                         sections        - information about all sections on page
                         normalizedtitle - normalized page title
                         lastmodified    - MW timestamp for when the page was last modified, e.g. "20130730174438"
                         lastmodifiedby  - information about the user who modified the page last
                         protection      - information about protection level
                         languagecount   - number of languages that the page is available in
                         hasvariants     - whether or not the page is available in other language variants
                         image           - information about an image associated with this page
                         thumb           - thumbnail of an image associated with this page
                        Values (separate with '|'): id, text, sections, normalizedtitle, lastmodified, lastmodifiedby, protection, languagecount,
                            hasvariants, image, thumb
                        Default: text|sections|normalizedtitle
  sectionprop         - What information about sections to get
                        Values (separate with '|'): toclevel, level, line, number, index, fromtitle, anchor
                        Default: toclevel|line
  variant             - Convert content into this language variant
  noimages            - Return HTML without images
  noheadings          - Don't include headings in output
  notransform         - Don't transform HTML into mobile-specific version
  onlyrequestedsections - Return only requested sections even with prop=sections
  offset              - Pretend all text result is one string, and return the substring starting at this point
                        The value must be no less than 0
                        Default: 0
  maxlen              - Pretend all text result is one string, and limit result to this length
                        The value must be no less than 0
                        Default: 0
  thumbsize           - Maximum thumbnail dimensions
                        The value must be no less than 0
                        Default: 50
Examples:
  api.php?action=mobileview&page=Doom_metal&sections=0
  api.php?action=mobileview&page=Candlemass&sections=0|references
  api.php?action=mobileview&page=Candlemass&sections=1-|references
 
//// */


import au.com.tyo.wiki.wiki.WikiPage;
import au.com.tyo.wiki.wiki.api.response.MobileViewJson;

public class MobileView extends ApiAction<MobileViewJson> {
	
	public static final String SECTION_ALL = "all";  // never use all with other sections
	public static final String SECTION_FIRST_TWO = "0|1";

	public MobileView() {
		super("mobileview");
		
		addRedirect("yes");
		
		this.addProp("text");
		this.addProp("sections");
		this.addProp("normalizedtitle");
		this.addProp("languagecount");
		this.addProp("hasvariants");
		this.addProp("image");
		this.addProp("thumb");
		this.addProp("lastmodified");
		this.addProp("lastmodifiedby");
		
		this.setFormat("json");
		
		this.addNoheadings("yes");
		
		this.addSectionprop("level");
		this.addSectionprop("line");
	}
	
	public void addPageVariable (String v) { this.addVariableAttribute("page", v, true); }
	public void addSectionsVariable (String v) { this.addVariableAttribute("sections", v, true); }
	public void addVariantVariable (String v) { this.addVariableAttribute("variant", v, true); }
	
	public void addPage (String v) { this.addAttribute("page", v); }
	public void addSections (String v) { this.addAttribute("sections", v); }
	public void addRedirect (String v) { this.addAttribute("redirect", v); }
	public void addProp (String v) { this.addAttribute("prop", v); }
	public void addSectionprop (String v) { this.addAttribute("sectionprop", v); }
	public void addVariant (String v) { this.addAttribute("variant", v); }
	public void addNoimages (String v) { this.addAttribute("noimages", v); }
	public void addNoheadings (String v) { this.addAttribute("noheadings", v); }
	public void addNotransform (String v) { this.addAttribute("notransform", v); }
	public void addOnlyrequestedsections (String v) { this.addAttribute("onlyrequestedsections", v); }
	public void addOffset (String v) { this.addAttribute("offset", v); }
	public void addMaxlen (String v) { this.addAttribute("maxlen", v); }
	public void addThumbsize (String v) { this.addAttribute("thumbsize", v); }


	public String getMobileViewUrl(String title, String sections) {
		this.addPageVariable(title);
		this.addSectionsVariable(sections);
		
		return getUrl();
	}
	
	public String getMobileViewUrl(String title, String sections, String domain, String areaCode) {
		if (domain.equalsIgnoreCase("zh") && areaCode.length() > 0) {
			String variant = null;
			
			if (areaCode.equalsIgnoreCase("tw") || areaCode.equalsIgnoreCase("cn") || 
					areaCode.equalsIgnoreCase("hk") || areaCode.equalsIgnoreCase("mo") ||
					areaCode.equalsIgnoreCase("sg") || areaCode.equalsIgnoreCase("my")) {
				if (areaCode.equalsIgnoreCase("my"))
					variant = "zh-sg";
				else
					variant =  "zh-" + areaCode.toLowerCase();
			}
			
			if (variant != null)
				this.addVariantVariable(variant);
		}
		return this.getMobileViewUrl(title, sections);
	}

	// public MobileViewJson getAs(WikiPage page) throws Exception {
    //     return getAs(page, MobileViewJson.class);
    // }
}
