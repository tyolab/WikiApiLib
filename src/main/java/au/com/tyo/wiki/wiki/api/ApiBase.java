package au.com.tyo.wiki.wiki.api;

import com.google.api.client.util.Charsets;
import com.google.api.client.util.ObjectParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import au.com.tyo.services.HttpConnection;
import au.com.tyo.services.HttpPool;
import au.com.tyo.wiki.wiki.WikiPage;

/*
 
cat /tmp/tmp.txt | grep - | cut -f 1 -d "-" | while read line; do fL=`echo ${line:0:1} | tr '[:lower:]' '[:upper:]'`${line:1}; echo "public void add${fL} (String v) { this.addAttribute(\"$line\", v); }"; done
 */

/**
 *
 * @param <T>
 */
public abstract class ApiBase<T> implements ApiRequest {
	
	public static final String WIKIPEDIA_MAIN_PAGE = "Main Page";

	public static class ActionAttribute {
		
		private String attributeName;
		
		private ArrayList<String> values;
		
		public ActionAttribute() {
			initializeValues();
		}
		
		public ActionAttribute(String name, String v) {
			this();
			
			attributeName = name;
			
			if (v.indexOf("|") > -1) {		
				String[] tokens = v.split("|");
				
				for (String token : tokens)
					addProperty(token);
			}
			else
				this.addProperty(v);
		}
		
		public ActionAttribute(String name) {
			this();
			attributeName = name;
		}

		public void initializeValues() {
			values = new ArrayList<String>();
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			
			if (values != null) {
				for (int i = 0; i < values.size(); ++i) {
					String value = values.get(i);
					if (value != null && i == 0)
						sb.append("=");
					if (i > 0)
						sb.append(URLEncoder.encode("|"));
					sb.append(value);
				}
			}
			return attributeName + sb.toString();
		}
		
		public void setUniqProperty(String value) {
			clear();
			addProperty(value);
		}
		
		public void addProperty(String value) {
			values.add(value);
		}
		
		public void replaceProperty(String value) {
			setUniqProperty(value);
		}

		public String getAttributeName() {
			return attributeName;
		}

		public void clear() {
			values.clear();
		}
	}
	
	protected String actionValue;
	
	protected String actionName;
	
	protected HashMap<String, ActionAttribute> attributes;
	
	protected String commonUrl;
	
	protected HashMap<String, ActionAttribute> variables;

    protected String apiUrl;

    /**
     * Response Parser
     */
    private ObjectParser parser;
	
	public ApiBase() {
		initializeValues();
		
//		this.setFormat("json");
	}
	
	public ApiBase(String actionName, String actionValue, String v) {	
		this(actionName, actionValue);
		
		String[] tokens = v.split("&");
		
		for (String token : tokens) {
			String[] attrPair = token.split("=");
			addAttribute(attrPair[0], attrPair[1]);
		}
	}
	
	public ApiBase(String name, String actionValue) {
		this();
		
		this.actionName = name;
		this.actionValue = actionValue;
	}

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void initializeValues() {
		commonUrl = null;
		attributes = new HashMap<String, ActionAttribute>();
		variables = new HashMap<String, ActionAttribute>();
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer(actionName + "=" + actionValue);
		
//		Collection<ActionAttribute> set = attributes.values();
//		Iterator<ActionAttribute> it = set.iterator();
//		while (it.hasNext()) {
//			ActionAttribute attr = it.next();
//			s.append("&" + attr.toString());
//		}
		attributeToString(s, attributes.values(), variables);
		attributeToString(s, variables.values(), null);
		
		return s.toString();
	}
	
	private void attributeToString(StringBuffer s,
			Collection<ActionAttribute> set, HashMap<String, ActionAttribute> set2) {
		Iterator<ActionAttribute> it = set.iterator();
		int count = 0;
		while (it.hasNext()) {
			ActionAttribute attr = it.next();
			if (null != set2 && set2.containsKey(attr.getAttributeName()))
				continue;
			/**
			 * TODO 
			 * 
			 * the & might not be needed where there is no action name and attribute
			 */
			if (count > 0)
				s.append("&");
			s.append(attr.toString());
			++count;
		}
	}
	
	protected void add(HashMap<String, ActionAttribute> attrs, String name, String v, boolean b) {
		String encodedV = URLEncoder.encode(v);
		ActionAttribute attr = attrs.get(name);
		if (attr == null)
			attrs.put(name, new ActionAttribute(name, encodedV));
		else {
			if (b) attr.replaceProperty(encodedV); else attr.addProperty(encodedV);
		}
	}
	
	protected void add(HashMap<String, ActionAttribute> attrs, String name, List<String> v, boolean b) {
		if (b) {
			ActionAttribute attr = attrs.get(name);
			if (attr != null)
				attr.clear();
		}
		for (String str : v)
			this.add(attrs, name, str, false);
	}
	
	public void setAttribute(String name, String v) {
		addAttribute(name, v, true);
	}
	
	protected void addAttribute(String name, String v) {
		addAttribute(name, v, false);
	}
	
	protected void addAttribute(String name, List<String> v) {
		addAttribute(name, v, false);
	}
	
	protected void addAttribute(String name, String v, boolean b) {
		add(attributes, name, v, b);
	}
	
	protected void addAttribute(String name, List<String> v, boolean b) {
		this.add(attributes, name, v, b);
	}
	
	public void setVariableAttribute(String name, String v) {
		addVariableAttribute(name, v, true);
	}
	
	protected void addVariableAttribute(String name, String v) {
		addVariableAttribute(name, v, false);
	}
	
	protected void addVariableAttribute(String name, List<String> v) {
		addVariableAttribute(name, v, false);
	}

	protected void addVariableAttribute(String name, String v, boolean b) {
		add(variables, name, v, b);
	}
	
	protected void addVariableAttribute(String name, List<String> v, boolean b) {
		this.add(variables, name, v, b);
	}
	
	public String attributeToString(Collection<ActionAttribute> collection, HashMap<String, ActionAttribute> set2) {
		StringBuffer s = new StringBuffer();
		attributeToString(s, collection, set2);
		return s.toString();
	}
	
	public void setFormat(String v) {
		this.setAttribute("format", v);
	}
	
	public void setTitle(String title) {
		this.setVariableAttribute("title", title);
	}
	
	public void setPageId(int id) {
		this.setVariableAttribute("pageid", String.valueOf(id));
	}
	
	public String getCommonUrl() {
		if (commonUrl == null) {
			StringBuffer s = new StringBuffer(actionName + "=" + actionValue);
			String attrsStr = attributeToString(attributes.values(), variables);
			if (attrsStr.length() > 0)
				s.append("&" + attrsStr);
	
			commonUrl = s.toString();
		}
		return commonUrl;
	}
	
	public void resetCommonUrl() {
		commonUrl = null;
	}
	
	public String getUrl() {
		return getCommonUrl() + (commonUrl.length() > 0 ? "&" : "") + attributeToString(variables.values(), null);
	}

    /**
     *
     * @param page
     * @return
     * @throws Exception
     */
	public String get(WikiPage page) throws Exception {
        HttpConnection connection = HttpPool.getInstance().getConnection();
        String url = createRequestUrl(page);
        return connection.get(url);
	}

    /**
     *
     * @param page
     * @return
     */
    protected String createRequestUrl(WikiPage page) {
        setTitle(page.getTitle());
        String url = apiUrl + getUrl();
        return url;
    }

    protected HttpConnection.HttpRequest createHttpRequest(WikiPage page) {
        String url = createRequestUrl(page);
        HttpConnection.HttpRequest settings = new HttpConnection.HttpRequest(url);
        return settings;
    }

	public String post(WikiPage page) throws Exception {
        HttpConnection connection = HttpPool.getInstance().getConnection();
        return connection.postForResult(createHttpRequest(page));
	}

	protected List parseAsList(String text) {
        return new ArrayList();
    }

	public List getList(WikiPage page) throws Exception {
        String text = get(page);
        return parseAsList(text);
    }

    /**
     *
     * @param inputStream
     * @param asCls
     * @return
     * @throws IOException
     */
    protected T parseAs(InputStream inputStream, Class<T> asCls) throws IOException {
        return parser.parseAndClose(inputStream, Charsets.UTF_8, asCls);
    }

    public T getAs(WikiPage page, Class<T> asCls) throws Exception {
        HttpConnection connection = HttpPool.getInstance().getConnection();
        String url = createRequestUrl(page);
        InputStream inputStream = connection.getAsInputStream(url);
        return parseAs(inputStream, asCls);
    }
}
