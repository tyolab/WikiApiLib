package au.com.tyo.wiki.wiki;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import au.com.tyo.io.ItemSerializable;

public class WikiPageSection extends ItemSerializable {

	/**
	 * 
	 */
	private static final long 			serialVersionUID = 5155340566721660429L;

	private String 						title;
	
	private int							id = -1;
	
	private String 						text;
	
	private int							level = 0;
	
	private String 						idName;  // 0, 1, 2, ..., 1.1, 1.2, 1.3, 1.1.1, 1.1.2, 1.1.3, ... something like that
	
	private WikiPageSection 			parent;
	
	private ArrayList<WikiPageSection> 	children;
	
	private int							offset;  // offset with the highest level
	
	public WikiPageSection() {
		init();
	}

	public WikiPageSection(String title, int id, String text, int level) {
		init();
		
		this.title = title;
		this.id = id;
		this.text = text;
		this.level = level;
	}
	
	private void init() {
		offset = -1;
		parent = null;
		children = new ArrayList<WikiPageSection>();
	}

	@Override
	public void serialise(ObjectOutputStream stream) throws IOException {
        stream.writeObject(title);
        stream.writeInt(id);
        stream.writeObject(text);
        stream.writeInt(level);
        stream.writeObject(idName);
        stream.writeObject(parent);
        stream.writeObject(children);
        stream.writeInt(offset);
	}

	@Override
	public void deserialise(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        title = (String) stream.readObject();;
        id = stream.readInt();
        text = (String) stream.readObject();;
        level = stream.readInt();
        idName = (String) stream.readObject();;
        parent = (WikiPageSection) stream.readObject();;
        children = (ArrayList<WikiPageSection>) stream.readObject();;
        offset = stream.readInt();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public void addChild(WikiPageSection child) {
		children.add(child);
	}
	
	public int countChildren() {
		return children.size();
	}
	
	public WikiPageSection getChild(int index) {
		if (index > -1 && index < countChildren() && countChildren() > 0)
			return children.get(index);
		return null;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public WikiPageSection getParent() {
		return parent;
	}

	public void setParent(WikiPageSection parent) {
		this.parent = parent;
	}

//	public WikiPageSection getNext() {
//		return next;
//	}
//
//	public void setNext(WikiPageSection next) {
//		this.next = next;
//	}
	
	public static void buildUpRelation(WikiPageSection parent, WikiPageSection child) {
		parent.addChild(child);
		child.setParent(parent);	
	}

	/**
	 * <h3> title </h3> for default
	 * @return
	 */
	public String toHtml() {
		return toHtml(3);
	}
	
	/**
	 * <div class="wikisection" id="section2">
	 *	    <a href="#h-secc2" class="hide" id="h-secc2">+</a><a href="#s-section2" class="show" id="s-secc2">-</a><a href="#" class="clickme" onclick="toggleDiv('secc2')"><div class="html_section_title"><h3>Evolution</h3></div></a>
	 *		<hr>	<div class="section_content" id="secc2">
	 * @param headerSize
	 * @return
	 */
	public String toHtml(int headerSize) {
		/*
		 * DEBUG
		 */
//		if (id == 11)
//			System.out.println("Stop here");
		
		StringBuffer sectionHtml = new StringBuffer();
		
		String sectionName = "section" + idName;
		String contentId = "secc" + idName;
		sectionHtml.append(String.format(WikiHtml.html_section_div, sectionName));
		boolean hideOrShowMade = false;
		
		if (getTitle() != null && getTitle().length() > 0) {
			String titleHtml;
			if (offset == 0) {
//				titleHtml = String.format(WikiHtml.html_section_title, contentId, contentId, headerSize, getTitle(), headerSize);
				titleHtml = String.format(WikiHtml.html_section_title, headerSize, contentId, idName, getTitle(), headerSize);
//				sectionHtml.append(String.format(WikiHtml.html_section_hide_n_show, contentId, contentId, contentId, contentId));
				hideOrShowMade = true;
			}
			else
				titleHtml = String.format(WikiHtml.html_subsection_title, headerSize, getTitle(), headerSize);
			sectionHtml.append(titleHtml);
//			sectionHtml.append("<hr>");
		}
				
		String sectionContentName = "";
		for (int i = 0; i < offset; ++i) {
			if (i > 3)
				break;
			sectionContentName = "sub" + sectionContentName;
		}
		
		if (hideOrShowMade) 
			sectionHtml.append(String.format(WikiHtml.html_section_content, sectionContentName, contentId, getText()));
		else
			sectionHtml.append(String.format(WikiHtml.html_section_content_no_hide_n_show, contentId, getText()));

		
		for (WikiPageSection subSection : children) 
			sectionHtml.append(subSection.toHtml(headerSize + 1));
		
//		sectionHtml.append("<br>");
		
		WikiHtml.closeDiv(sectionHtml);
		WikiHtml.closeDiv(sectionHtml);

		return sectionHtml.toString();
	}

	public void setupIdName() {
		if (parent == null) 
			idName = "" + id;
		else {
			WikiPageSection temp = this;
			while (temp.getParent() != null) {
				idName = "" + temp.getParent().getId() + "." + id;
				temp = temp.getParent();
			}
		}
	}
	
	
}
