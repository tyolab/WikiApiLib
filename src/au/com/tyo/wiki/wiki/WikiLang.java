package au.com.tyo.wiki.wiki;

import java.util.ArrayList;

import au.com.tyo.wiki.wiki.WikipediaSite.Site;

public class WikiLang {
	String code;
	String name;
	String english;
	
	int index;
	
	ArrayList<Site> sites; // = new ArrayList<Site>();

	public synchronized String getCode() {
		return code;
	}

	public synchronized void setCode(String code) {
		this.code = code;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized String getEnglish() {
		return english;
	}

	public synchronized void setEnglish(String english) {
		this.english = english;
	}

	public synchronized int getIndex() {
		return index;
	}

	public synchronized void setIndex(int index) {
		this.index = index;
	}

	public synchronized ArrayList<Site> getSites() {
		return sites;
	}

	public synchronized void setSites(ArrayList<Site> sites) {
		this.sites = sites;
	}
}