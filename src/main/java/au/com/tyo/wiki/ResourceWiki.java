package au.com.tyo.wiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import au.com.tyo.io.IO;
import au.com.tyo.parser.Sgml;
import au.com.tyo.parser.SgmlNode;
import au.com.tyo.wiki.wiki.PageLang;
import au.com.tyo.wiki.wiki.WikiLang;
import au.com.tyo.wiki.wiki.WikipediaSite;
import au.com.tyo.wiki.wiki.api.ApiBase;

public class ResourceWiki  {
	public static final String PAGE_NOT_FOUND_ERROR_HTML = "<html><title></title><body><h1>The page \"%s\" does not exist</h1></body></html>";
	public static final String PAGE_GENERAL_ERROR_HTML = "<html><title></title><body><h1>Unknow Error \"%s\", please report bug to dev@tyo.com.au </h1></body></html>";
	
	public static final String QUERY_FORMAT = "<em><i><font color=\"#FF1515\">%s</font></i></em>";
	
	protected WikipediaSite wikipedias;
	protected String[] langs;
	protected String[] langNatives;
	protected String[] langCodes;
	
	ArrayList<PageLang> langLinks;
	
	protected HashMap<String, String> wikpediaNames;
	
	public ResourceWiki() {

	}

	public void initialize() throws Exception {
		loadWikipediaNames();
		loadWikipediaSites();
	}

	private void loadWikipediaNames() throws Exception {
        InputStream is;
        
        is = ResourceWiki.class.getResourceAsStream("/au/com/tyo/wiki/res/wikipedia-names.txt");
		
        if (is != null) {
			wikpediaNames = new HashMap<String, String>();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader reader = new BufferedReader(isr);
	        String line;
	        while ((line = reader.readLine()) != null && line.trim().length() > 0) {
	        	if (line.charAt(0) != '#') {
		        	String[] tokens = line.split(":");
		        	if (tokens.length == 2)
		        		wikpediaNames.put(tokens[0], tokens[1]);
	        	}
	        }
	        line = null;
        }
	}

	public synchronized WikipediaSite getWikipedias() {
		return wikipedias;
	}

	public synchronized void setWikipedias(WikipediaSite wikipedias) {
		this.wikipedias = wikipedias;
	}
	
	private void loadWikipediaSites() throws Exception{
        InputStream is;

//			is = context.getAssets().open("wikipedia/sites.txt");
		String resourcePath = "/au/com/tyo/wiki/res/sites.txt";
        is = ResourceWiki.class.getResourceAsStream(resourcePath);

		if (is == null)
			is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
			
        if (is != null) {
        	loadWikipediaSites(is);
        }
	}
	
	public void loadWikipediaSites(InputStream is) throws Exception {
		try {
			wikipedias = new WikipediaSite();
//	        String text = new String(IO.readFileIntoBytes(is));
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader reader = new BufferedReader(isr);
	        String line;
	        int count = -1;
	        while ((line = reader.readLine()) != null && line.trim().length() > 0) {
	        	if (line.charAt(0) != '#') {
		        	String[] tokens = line.split(",");
		        	if (tokens.length == 3)  {
		        		String nativeName = String.valueOf(Character.toUpperCase(tokens[1].charAt(0))) + tokens[1].substring(1);
		        		wikipedias.addLang(tokens[0], nativeName, tokens[2], ++count);
		        	}
	        	}
	        }
	        line = null;
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	protected void loadWikipediaSiteMatrix(InputStream is) throws IOException, Exception {
		try {
			wikipedias = new WikipediaSite();
	        String xml = new String(IO.readFileIntoBytes(is));
	        Sgml parser = new Sgml();
	        
	        SgmlNode siteMatrixNode = parser.parse(xml.getBytes(), "api").getChild(0);
	        int i = 0;
	        SgmlNode langNode = null;
	        for (; i< siteMatrixNode.countChildren(); ++i) {
	        	langNode = siteMatrixNode.getChild(i);
	        	if (langNode.getContent().equals("language"))
	        		break;
	        }
//	        if (siteMatrixNode.countChildren() > 1) {
	        	while (langNode != null) {
	        		this.getWikipedias().addSite(langNode);
	        		++i;
	        		langNode = siteMatrixNode.getChild(i);
	        	}
//	        }
	        siteMatrixNode = null;  
		}
		catch (IOException e) {
			throw e;
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	public String[] getWikipediaLanguages() {
		if (langs == null) {
			langs = this.getWikipedias().createLanguageList();
		}
		return langs;
	}
	
	public String[] getWikipediaLanguageNatives() {
		if (langNatives == null) {
			langNatives = this.getWikipedias().createNativeLanguageList();
		}
		return langNatives;
	}
	
	public String[] getWikipediaLanguageCodes() {
		if (langCodes == null) {
			langCodes = this.getWikipedias().createLanguageCodeList();
		}
		return langCodes;
	}
	
	public ArrayList<PageLang> getWikipediaLangLinks(String favCode, String primaryCode) {
		if (langLinks == null) {
			langLinks = this.getWikipedias().createlangLinks(favCode, primaryCode);
		}
		return langLinks;
	}

	public String[] getWikipediaLanguageIndexs() {
		return null;
	}

	public String getPageNotFoundTemplate(String query) {
		return "";
	}

	public String getPageErrorTemplate(String query) {
		return "";
	}

	public String getPageBlankTemplate(String query) {
		return "";
	}
	
	public String getPageTemplate(InputStream is, String content) throws IOException {
        String text = String.format(new String(IO.readFileIntoBytes(is)), content);
        return text;
	}

	public WikiLang getWikiLang(String langCode) {
		return wikipedias.getWikiLang(langCode);
	}

	public String getWikiLangLocalName(String langCode) {
		return getWikiLang(langCode) != null ? getWikiLang(langCode).getName() : null;
	}

	public String getWikiLangName(String langCode) {
		return getWikiLang(langCode) != null ? getWikiLang(langCode).getEnglish() : null;
	}

	public String getMainPageString() {
		return ApiBase.WIKIPEDIA_MAIN_PAGE;
	}

	public String getString(int resourceId) {
		return "";
	}
	
	public String getWikipediaName(String code) {
		return this.wikpediaNames.get(code);
	}
}
