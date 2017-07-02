package au.com.tyo.wiki.wiki.api;

import java.util.ArrayList;
import java.util.List;

import au.com.tyo.services.HttpConnection;
import au.com.tyo.services.HttpConnection.Parameter;
import au.com.tyo.wiki.wiki.WikiPage;

public abstract class ApiAction<T> extends ApiBase<T> {
	
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

	@Override
	protected HttpConnection.HttpRequest createHttpRequest(WikiPage page) {
		HttpConnection.HttpRequest request = super.createHttpRequest(page);
		request.setParams(getParamsPost());
		request.setAutomaticLoadCookie(true);
		return request;
	}
	
}
