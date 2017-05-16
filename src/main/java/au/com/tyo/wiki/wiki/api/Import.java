package au.com.tyo.wiki.wiki.api;

import au.com.tyo.services.HttpConnection;
import au.com.tyo.services.HttpConnection.Parameter;

public class Import extends ApiActionTokenNeeded {

	public Import() {
		super("import");

		paramsPost.add(new Parameter("action", "import"));
		paramsPost.add(new Parameter("format", "json"));
	}

	public void setXml(String xml) {
		Parameter param = new Parameter("xml", xml);
		param.setContentType(HttpConnection.MIME_TYPE_XML);
		param.addExtra("filename", "file.xml");
		paramsPost.add(param);
	}
}
