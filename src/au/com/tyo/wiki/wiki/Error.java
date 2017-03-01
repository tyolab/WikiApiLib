package au.com.tyo.wiki.wiki;

import java.io.Serializable;

public class Error implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1015877326883656779L;

	public static final String ERROR_INFO_UNKNOWN = "UNKNOWN";
	
	String info;
	String code;
	
	public Error() {
		info = ERROR_INFO_UNKNOWN;
		code = "Error";
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
}
