package au.com.tyo.wiki.wiki;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import au.com.tyo.parser.Sgml;
import au.com.tyo.parser.SgmlNode;
import au.com.tyo.parser.XML;
import au.com.tyo.utils.ByteArrayKMP;
import au.com.tyo.utils.XML2TXT;

public class WikiTransformer {
	private static final String SEARCHBOX_DIV_STRING = "<div id='searchbox'";
	private static final String SEARCHBOX_FORM_STRING = "<form"; // id=\"searchForm\"";
	private static final String DIV_STRING = "<div";
	private static final String LANGUAGE_SELECTION_STRING_OPEN = "<select id=\"languageselection\"";
	private static final String LANGUAGE_SELECTION_STRING_CLOSED = "</select>";
	private static final String BODY_STRING = "<body";
	
	private static final String DIV_CONTENT_STRING = "<div id=\"content\"";
	private static final String DIV_CONTENT_WRAPPER_STRING = "<div id=\"content_wrapper\"";
	private static final String CONTENT_STRING = "results";
	private static final String TITLE_TAG_STRING = "<title";
	private static final String STYLESHEET_STRING = null;
	
	public static final int	  HOW_MANY_CHARACTERS_ARE_TOO_LITTLE = 50;
	
	
	public static byte[] transform(WikiPage wikiPage) {
//		String cssFile = Application.getInstance().getResourceManager().getCssFile();
//		return transform(wikiPage, cssFile, Application.getInstance().getSettings().isNigthThemeEnabled());
		return transform(wikiPage, null);
	}
	
	public static byte[] transform(WikiPage wikiPage, String cssFile) {
		/*
		 * Get all the available languages
		 */
//		WikiTransformer.getLanguageSelection(wikiPage);
		
		// make space for night theme
		
		int lengthOfNightCssFile = 0; //cssFile.length();
		byte[] textBytes = wikiPage.getBytes();
		byte[] target = textBytes;
		
		if (cssFile != null)
			lengthOfNightCssFile = cssFile.length();
		
//		int index;
//		int indexEnd;
		
//		if (wikiPage.getNightCssStart() < 0) {
//			target = new byte[textBytes.length + lengthOfNightCssFile + 1];
//			
//			ByteArrayKMP kmpSearch = new ByteArrayKMP("</head".getBytes());
//			index = kmpSearch.search(textBytes, 0);
//			if (index > -1) {
//				System.arraycopy(textBytes, 0, target, 0, index + 1);
//				indexEnd = index + 1 + lengthOfNightCssFile;
//				Arrays.fill(target, index, indexEnd, (byte)' ');
//				System.arraycopy(textBytes, index, target, indexEnd, textBytes.length - index -1);
//				wikiPage.setNightCssStart(index);
//				wikiPage.setNightCssEnd(indexEnd);
//			}
//		}
//		else {
//			target = textBytes;
//		}
//		
//		if (wikiPage.getNightCssStart() > -1) {
//			index = wikiPage.getNightCssStart();
//			indexEnd = wikiPage.getNightCssEnd();
//			if (cssFile != null)
//				System.arraycopy(cssFile.getBytes(), 0, target, index, lengthOfNightCssFile);
//			else
//				Arrays.fill(target, index, indexEnd, (byte)' ');
//		}
		
		boolean removedHeader = removeHeader(target);
		
		if (!removedHeader) {
			removedHeader = removeSearchBox(target);
			
			/*
			 * remove language selection
			 */
			removeLanguageSelection(target);
			
			/*
			 * TODO get all the links from here
			 */
			//kmpSearch.setPattern(CONTENT_DIV_STRING.getBytes());
		}
		return target;
	}
	
	public static String processTitle(byte[] textBytes) {
		ByteArrayKMP kmpSearch = new ByteArrayKMP(TITLE_TAG_STRING.getBytes());
		int index = kmpSearch.search(textBytes, 0);
		String title = null;
		
		if (index > 0) {
			while (((char)textBytes[index]) != '>' && index < textBytes.length)
				++index;
			++index;
			
			int indexStart = index;
			while (((char)textBytes[index]) != '<' && index < textBytes.length)
				++index;
//			++index; 
			
			title = new String(textBytes, indexStart, index - indexStart);
			String[] tokens = title.split("-");
			if (tokens != null && tokens.length >= 2)
				title = XML.unXMLify(tokens[0].trim());
		}
		return title;
	}

	private static boolean replaceCss(WikiPage wikiPage) {
		ByteArrayKMP kmpSearch = new ByteArrayKMP(STYLESHEET_STRING.getBytes());
		byte[] textBytes = wikiPage.getBytes();
		int index = kmpSearch.search(textBytes, 0);
		int len = textBytes.length;
		boolean result = false;
		
		
		return result;
	}
	
	public static boolean removeHeader(byte[] textBytes) {
		int index = 0;
		int len = textBytes.length;
		boolean result = false;
		ByteArrayKMP kmpSearch = new ByteArrayKMP(BODY_STRING.getBytes());
		index = kmpSearch.search(textBytes, 0);
		
		while (textBytes[index] != '>' && index < textBytes.length)
			++index;
		++index;
		
		if (index > -1) {
//			while (index < len && textBytes[index] != '>')
//				++index;
//			
//			if (index < len) {
//				++index;  // '>'
			int fillStart = index;
			kmpSearch.setPattern(CONTENT_STRING.getBytes()); // checking the results
			int contentIndex = -1;
			int tagIndex = -1;
			SgmlNode contentDivNode;
			byte pre;
			do {
				contentIndex = kmpSearch.search(textBytes, index);
				if (contentIndex > -1) {
					index = contentIndex + CONTENT_STRING.length();
					
					tagIndex = contentIndex - 1;
					while (textBytes[tagIndex] == ' ')
						--tagIndex;
					
					pre = textBytes[tagIndex];
					if (pre == '"' || pre == '\'') {
						while (textBytes[tagIndex] != '<' && tagIndex > 0)
							--tagIndex;
						
						contentDivNode = new SgmlNode();
						contentDivNode.parseTag(textBytes, tagIndex);
						String tagName = contentDivNode.getName();
						if (tagName != null && tagName.equalsIgnoreCase("div"))
							break;
					}
					tagIndex = -1;
				}
			} while (contentIndex < 0 && index < textBytes.length);
			if (tagIndex > -1) {
				Arrays.fill(textBytes, fillStart, tagIndex, (byte)' ');
//				SgmlNode headerNode = new Sgml().parseFirstTag(textBytes, index);
//				String id = headerNode.getAttribute("id");
//				if (id == null) {
//					id = "";
//				}
//				id.toLowerCase();
//				
//				if (id.contains("header")) {
//					int end = headerNode.getEnd();
//					int start = headerNode.getStart();
//					
//					if (start > -1 && end > -1 && end > start && end < len) {
						result = true;
//						Arrays.fill(textBytes, start, end, (byte)' ');
//					}
//				}
			}
		}
		return result;
	}

	private static boolean processPage(WikiPage wikiPage) {
		byte[] textBytes = wikiPage.getBytes();
		int index = 0;
		boolean result = false;
		ByteArrayKMP kmpSearch = new ByteArrayKMP(DIV_CONTENT_STRING.getBytes());
		index = kmpSearch.search(textBytes, 0);
		SgmlNode contentDivNode;
		if (index > -1) {
			result = true;
			Sgml parser = new Sgml();
			contentDivNode = parser.parse(textBytes, index);
			int end = contentDivNode.getEnd();
			SgmlNode titleNode = null;
			SgmlNode firstChild = contentDivNode.getChild(0);
			String titleText = "";
			if (firstChild != null) {
				String text = firstChild.getContent().trim();
				if (text.equalsIgnoreCase("div")) {
					titleNode = firstChild.getChild(0);
				}
				else
					titleNode = firstChild;
				String titleTag = titleNode.getContent().trim();

				if (titleTag.length() > 0 && titleTag.substring(0, 1).equalsIgnoreCase("h")) {
					SgmlNode titleTextNode;
					titleTextNode = titleNode.getChild(0);
					if (titleTextNode != null && titleTextNode.getContent().length() > 0)
						titleText = titleTextNode.getContent().trim();
				}
			}
				
			if (titleText.length() > 0) {
				wikiPage.setTitle(titleText);
				Arrays.fill(textBytes, titleNode.getStart(), titleNode.getEnd(), (byte)' ');
			}
		}
		return result;
	}
	
	private static boolean removeSearchBox(byte[] textBytes) {
		ByteArrayKMP kmpSearch = new ByteArrayKMP(SEARCHBOX_DIV_STRING.getBytes());
		int index = 0;
		int end = 0;
		boolean result = false;
		
		index = kmpSearch.search(textBytes, index);
		if (index < 0) {
			removeForm(textBytes);
		}
		else {
//		if (index > -1) {
//			next = index;
//			byte[] searchBoxBlock = wikiPage.getTransformState().getSearchBoxBlock();

			Sgml parser = new Sgml();
			SgmlNode root = parser.parse(textBytes, index);
			end = root.getEnd();
			if (end > -1) {
				Arrays.fill(textBytes, index, end, (byte)' ');
				index = end;
				result = true;
			}
		}
		return result;
	}

	public static void getLanguageSelection(WikiPage wikiPage) {
		String html = wikiPage.getText();
		int pos1 = html.indexOf(LANGUAGE_SELECTION_STRING_OPEN);
		
		if (pos1 > -1) {
			int pos2 = html.indexOf(LANGUAGE_SELECTION_STRING_CLOSED, pos1);
			if (pos2 > -1) {
				SgmlNode languageSelectionNode = new SgmlNode(html.substring(pos1, pos2 + LANGUAGE_SELECTION_STRING_CLOSED.length()));
//			if (index > -1) {
//				Sgml parser = new Sgml();
//				languageSelectionNode = parser.parseJSON(textBytes, index);
//				wikiPage.setLanguageSelectionNode(languageSelectionNode);
			}
		}
		else {
			
		}
//				result = true;
//			}
//		}
//		return result;
	}
	
	public static boolean getLanguageSelection2(WikiPage wikiPage) {
		byte[] textBytes = wikiPage.getBytes();
		boolean result = false;
		if (textBytes != null && textBytes.length > 0) {
			ByteArrayKMP kmpSearch = new ByteArrayKMP(LANGUAGE_SELECTION_STRING_OPEN.getBytes());
			int index = kmpSearch.search(textBytes, 0);
	//		kmpSearch.setPattern(LANGUAGE_SELECTION_STRING.getBytes());
	
			SgmlNode languageSelectionNode = null;
			if (index > -1) {
				Sgml parser = new Sgml();
				languageSelectionNode = parser.parse(textBytes, index);
//				wikiPage.setLanguageSelectionNode(languageSelectionNode);
				result = true;
			}
		}
		return result;
	}
	
	public static boolean removeLanguageSelection(byte[] textBytes) {
		boolean result = false;
		SgmlNode languageSelectionNode = null;
		if (languageSelectionNode == null) {
			
			ByteArrayKMP kmpSearch = new ByteArrayKMP(LANGUAGE_SELECTION_STRING_OPEN.getBytes());
			int index = kmpSearch.search(textBytes, 0);

	//		kmpSearch.setPattern(LANGUAGE_SELECTION_STRING.getBytes());
			if (index > -1) {
				Sgml parser = new Sgml();
				languageSelectionNode = parser.parse(textBytes, index);

			}
		}
		if (languageSelectionNode != null) {
			int end = languageSelectionNode.getEnd();
			int start = languageSelectionNode.getStart();
			
			if (start > -1 && end > -1) {
				result = true;
	//				byte[] languageBlock = new byte[end - index];
	//				System.arraycopy(textBytes, index, languageBlock, 0, end - index);
				Arrays.fill(textBytes, start, end, (byte)' ');
	//				index = end;
			}
		}
		return result;
	}
	
	public static boolean removeForm(byte[] textBytes) {
		ByteArrayKMP kmpSearch = new ByteArrayKMP(SEARCHBOX_FORM_STRING.getBytes());
//		kmpSearch.setPattern(SEARCHBOX_FORM_STRING.getBytes());
		int index = kmpSearch.search(textBytes, 0);
		boolean result = false;
		while (index > 0) {
			Sgml parser = new Sgml();
			SgmlNode root = parser.parse(textBytes, index);
			int end = root.getEnd();
			if (end > -1) {
				result = true;
				Arrays.fill(textBytes, index, end, (byte)' ');
				index = end;
			}
			else
				index += SEARCHBOX_FORM_STRING.length();
			index = kmpSearch.search(textBytes, index);
		}
		return result;
	}
	
	private static String postProcess(boolean removeLinks, byte[] bytes) {
		return postProcess(removeLinks, bytes, 0, bytes.length);
	}
	
	private static String postProcess(boolean removeLinks, byte[] bytes, int start, int end) {
		String temp;
		try {
			temp = new String(removeLinks? XML2TXT.clean(bytes, start, end) : bytes, start, end - start, "UFT-8");
		} catch (UnsupportedEncodingException e) {
			temp = new String(removeLinks? XML2TXT.clean(bytes, start, end) : bytes, start, end - start);
		}
		
//		XML2TXT.replaceNonAlphabet(temp, " ");
		return temp;
	}

	public static String processAbstract(byte[] bytes) {
		return processAbstract(bytes, true);
	}
	
	private static String processAbstract(byte[] bytes, boolean removeLinks) {
		ByteArrayKMP kmpSearchStart = new ByteArrayKMP("<p".getBytes());
		ByteArrayKMP kmpSearchEnd = new ByteArrayKMP("</p".getBytes());
		int index = kmpSearchStart.search(bytes, 0);
		StringBuffer abs = new StringBuffer("");
		while (index > -1) {
			int end = kmpSearchEnd.search(bytes, index + 1);
			int startPara = index;
			index = kmpSearchStart.search(bytes, end + 1);
			
			if (end < 0 && index > -1)
				end = index;
			
			if (end > -1) {
				abs.append(postProcess(removeLinks, bytes, startPara, end));
				abs.append("\n");
			}
			else {
				index = -1;
				SgmlNode node = new Sgml().parse(bytes, index);
				int start = node.getStart();
				int nodeEnd = node.getEnd();
				if (start > -1 && nodeEnd > -1 && nodeEnd > start)
					abs.append(postProcess(removeLinks, bytes, start, nodeEnd));
			}
		}
		
		/*
		 * if we got too little information, then just give it all
		 */
//		if (abs.length() == 0)
		if (abs.length() <= HOW_MANY_CHARACTERS_ARE_TOO_LITTLE) {
			abs.setLength(0);
			abs.append(postProcess(removeLinks, bytes));
		}
		
		return abs.toString();
	}
	
	public String updateLinks(String html) {
		return html.replaceAll("href=\"//", "href=\"" + WikiApi.getInstance().getApiConfig().getProtocol() + "://")
				.replaceAll("src=\"//", "src=\"" + WikiApi.getInstance().getApiConfig().getProtocol() + "://");	
	}
	
	public static void main(String[] args) {
		
		File file = new File("files/wikipedia_mobile_hello.html");
		
		if (file.exists()) {
			WikiPage page = null;
			try {
				page = new WikiPage(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String result = page.getText();
//			WikiTransformer.removeTitleAndSearch(page);
//			byte[] bytes = page.getBytes();
//			WikiTransformer.removeForm(bytes);
//			WikiTransformer.removeLanguageSelection(bytes);
//			new String(page.getBytes());
			System.out.println(result);
		}

	}

}
