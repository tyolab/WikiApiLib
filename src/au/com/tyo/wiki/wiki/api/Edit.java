package au.com.tyo.wiki.wiki.api;

import java.net.URLEncoder;

public class Edit extends ApiActionTokenNeeded {
	
	private static Edit instance;
	
	/**
	 * the text pushed to the server
	 */
	private String text;

	public Edit() {
		super("edit");
		
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void createPage(String title, String text) {
		/*
		 * TODO
		 * 
		 * set create mode
		 * 1) createonly
		 * 2) recreate
		 * 
		 */
		
		editPage(title, text);
	}
	
	public void editPage(String title, String text) {
		setTitle(title);
		setText(text);
	}
	
	@Override
	public String getUrl() {
		return super.getUrl() + "&text=" + URLEncoder.encode(text);
	}

	public static Edit getInstance(String token) {
		if (instance == null) {
			instance = new Edit();
			instance.setToken(token);
		}
		return instance;
	}

}
