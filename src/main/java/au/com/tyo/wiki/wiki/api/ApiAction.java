package au.com.tyo.wiki.wiki.api;

import java.util.ArrayList;
import java.util.List;

import au.com.tyo.services.HttpConnection.Parameter;

public abstract class ApiAction extends ApiBase {
	
	protected List<Parameter> paramsPost; // params for POST method

	public ApiAction(String actionValue) {
		super("action", actionValue);
		
		paramsPost = null;
	}
	
	protected void initializeParams() {
		paramsPost = new ArrayList<Parameter>();
	}

	public List<Parameter> getParamsPost() {
		return paramsPost;
	}
	
}
