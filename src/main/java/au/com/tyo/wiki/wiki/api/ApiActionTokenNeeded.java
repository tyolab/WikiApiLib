package au.com.tyo.wiki.wiki.api;

import au.com.tyo.services.HttpConnection.Parameter;

public class ApiActionTokenNeeded  extends ApiAction {
	
	public ApiActionTokenNeeded(String action) {
		super(action);
		
		initializeParams();
	} 
	
	public void setToken(String token) {
		/*
		 * token must be posted
		 */
		paramsPost.add(new Parameter("token", token));
//		this.setVariableAttribute("token", token);
	}
}
