package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tsvetelin Tsonev <tsvetelin.tsonev@yahoo.co.uk>
 */
public class HttpRequest implements Request {

	private String scheme;
	private String rawRequest;
	private String method;
	private String requestUri;
	private String queryString;
	private String postData;
	private Map<String, String> parameterMap = null;
	private Map<String, String> getParameterMap = null;
	private Map<String, String> postParameterMap = null;
	private String protocol;
	private Map<String, String> headers = null;

	public HttpRequest(BufferedReader reader) throws IOException {
		scheme = "http";
		headers = new HashMap<String, String>();
		parameterMap = new HashMap<String, String>();
		getParameterMap = new HashMap<String, String>();
		postParameterMap = new HashMap<String, String>();
		parse(reader);
	}

	private void parse(BufferedReader reader) {
		String rawRequest, line, eol;
		String contentLengthHeader = "Content-Length";
		int contentLength = 0;
		StringBuilder sb = new StringBuilder();

		// End of line (EOL) character e.g. \n, \r, \r\n
		eol = System.lineSeparator();
		int eolLength = eol.length();
		try {
			while ((line = reader.readLine()) != null && !line.isEmpty()) {
				if (line.startsWith(contentLengthHeader)) {
					contentLength = Integer.parseInt(line.substring(contentLengthHeader.length() + 1).trim());
				}
				sb.append(line);
				sb.append(eol);
			}

			rawRequest = sb.toString();

			if (rawRequest.startsWith("POST")) {
				char[] packet = new char[contentLength];
				reader.read(packet);
				postData = URLDecoder.decode(String.valueOf(packet), "UTF-8");
				setParameters(postParameterMap, postData);
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

		mergeParameters();
		rawRequest = sb.toString();
		// Trim EOL from the end.
		this.rawRequest = rawRequest.substring(0, rawRequest.length() - eolLength);
		parseRawRequest();
	}

	private void parseRawRequest() {
		String regex = "\\n|\\r|\\r\\n";
		String[] lines = rawRequest.split(regex);
		parseRequestLine(lines);
		parseHeaders(lines);
	}

	private void parseRequestLine(String[] lines) {
		// e.g. GET /test HTTP/1.1
		String[] requestLine = lines[0].split("\\s+");
		this.method = requestLine[0];
		parseUrl(requestLine[1]);
		this.protocol = requestLine[2];
	}

	private void parseUrl(String url) {
		if (url.indexOf("?") != -1) {
			String[] urlArr = url.split("\\?");
			try {
				this.queryString = URLDecoder.decode(urlArr[1], "UTF-8");
				setParameters(getParameterMap, this.queryString);
			} catch (UnsupportedEncodingException e) {
				// Should never happen.
			}
		} else {
		}
		this.requestUri = url;
	}

	private void parseHeaders(String[] headers) {
		// Skip request line . It has been parsed.
		for (int i = 1; i < headers.length; i++) {
			// Make sure the header has correct format e.g. "Connection:
			// keep-alive"
			if ((headers[i].indexOf(": ") != -1) && !headers[i].equals("")) {
				parseHeader(headers[i]);
			}
		}
	}

	private void parseHeader(String header) {
		String key = header.substring(0, header.indexOf(": ")).trim();
		String value = header.substring(header.indexOf(": ") + 1, header.length()).trim();
		this.headers.put(key, value);
	}

	private void mergeParameters() {
		parameterMap.putAll(getParameterMap);
		parameterMap.putAll(postParameterMap);
	}

	private void setParameters(Map<String, String> params, String queryString) {
		if (queryString.length() > 0) {
			String[] parameterPairs = queryString.split("&");
			for (String pair : parameterPairs) {
				String[] halves = pair.split("=", 2);
				params.put(halves[0], halves[1]);
			}
		}
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public String getParameter(String key) {
		return parameterMap.get(key);
	}

	@Override
	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	@Override
	public boolean hasHeader(String key) {
		return headers.containsKey(key);
	}

	@Override
	public String getHeader(String key) {
		return headers.get(key);
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getRequestUri() {
		return requestUri;
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public boolean isPost() {
		return method.equalsIgnoreCase("POST");
	}

	@Override
	public boolean isGet() {
		return method.equalsIgnoreCase("GET");
	}

	@Override
	public String toString() {
		return "HttpRequest [method=" + method + ", requestUri=" + requestUri + ", queryString=" + queryString
				+ ", postData=" + postData + ", parameterMap=" + parameterMap + ", protocol=" + protocol + ", headers="
				+ headers + "]";
	}

	public Map<String, String> getGetParameterMap() {
		return getParameterMap;
	}

	public Map<String, String> getPostParameterMap() {
		return postParameterMap;
	}

}