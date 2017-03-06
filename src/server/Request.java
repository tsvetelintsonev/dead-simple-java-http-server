package server;

import java.util.Map;

/**
 * @author Tsvetelin Tsonev <tsvetelin.tsonev@yahoo.co.uk>
 */
public interface Request {
	String getScheme();
	String getParameter(String key);
	public Map<String, String> getGetParameterMap();
	public Map<String, String> getPostParameterMap();
	Map<String, String> getParameterMap();
	String getHeader(String key);
	boolean hasHeader(String key);
	String getMethod();
	String getRequestUri();
	String getQueryString();
	String getProtocol();
	Map<String, String> getHeaders();
	boolean isPost();
	boolean isGet();
}
