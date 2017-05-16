package au.com.tyo.wiki.wiki.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.tyo.services.HttpConnection;

public class Login extends ApiAction  {
	
	public static final int SUCCESS = 1;
	
	public static final int ERROR = -1; // general
	
	public static final int ERROR_SERVER = -2; // can't contact the server
	
	public static final int ERROR_PARSE = -3; // parse result error
	
	public static final int ERROR_NEEDTOKEN = -4; // need token

	private static final int ERROR_USER_NOT_EXISTS = -5;
	
	public static final int READY = 0;
	
	private String sessionId;
	
	private int id;
	
	private String coookiePrefix;
	
	private String token;
	
	private String name;
	
	private String password;
	
	private int status;
	
	private Map<String, String> cookies;

	public Login() {
		super("login");
		
		setFormat("json");

		status = READY;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getCoookiePrefix() {
		return coookiePrefix;
	}

	public void setCoookiePrefix(String coookiePrefix) {
		this.coookiePrefix = coookiePrefix;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getLoginUrl() {
		return getUrl();
	}
	
	public List<HttpConnection.Parameter> buildParams(String name, String password) {
		List<HttpConnection.Parameter> list = new ArrayList<>();
		list.add(new HttpConnection.Parameter("lgname", name));
		list.add(new HttpConnection.Parameter("lgpassword", password));
		return list;	
	}

	public String getLoginUrl(String name, String password) {
		this.setVariableAttribute("lgname", name);
		this.setVariableAttribute("lgpassword", password);
		
		return getUrl();
	}
	
	/**
	 * 
	 * @return "success" or the token
	 * 
	 * {"login":{"result":"NeedToken","token":"d655688b0ab4c4ebec19ab7f32ad2b29","cookieprefix":"wikipedia_zh","sessionid":"dqbdnbu0divpi5vnb9d29b17f5"}}
	 * @throws JSONException 
	 */
	public static int parseLoginResultJson(Login login, String text) throws JSONException {
		JSONObject obj = new JSONObject(text);
		int s = ERROR_PARSE;
		
		if (obj.has("login")) {
			JSONObject loginObj = obj.getJSONObject("login");
			
			String result = loginObj.getString("result");
			
			if (result.equals("NeedToken")) {	
				s = ERROR_NEEDTOKEN;
						
				login.setToken(loginObj.getString("token"));
			}
			else if (result.equals("NotExists")) {
				s = ERROR_USER_NOT_EXISTS;
			}
			else if (result.equals("Success")) {
				s = SUCCESS;
				
				login.setId(loginObj.getInt("lguserid"));
				login.setName(loginObj.getString("lgusername"));
				
				login.setToken(loginObj.getString("lgtoken"));
			}

			login.setCoookiePrefix(loginObj.getString("cookieprefix"));
			login.setSessionId(loginObj.getString("sessionid"));
		}
		return s;
	}
}
