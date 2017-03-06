package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class HttpRequestTest {

	private Request request;

	@Before
	public void SetUp() {
		
		String rawRequest = "POST /test/uri?query=string&is=present HTTP/1.1\n" 
				+ "Host: localhost:6060\n"
				+ "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0\n"
				+ "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n"
				+ "Accept-Language: en-US,en;q=0.5\n" 
				+ "Accept-Encoding: gzip, deflate\n" 
				+ "Content-Type: application/x-www-form-urlencoded\n"
				+ "Content-Length: 25\n"
				+ "Connection: keep-alive\n"
				+ "Upgrade-Insecure-Requests: 1\n"
				+ "\n"
				+ "username=aaa&password=bbb"; // Content-Length must be equal to post data length
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new InputStream() {
			
			byte[] stream = rawRequest.getBytes();
			int index = 0;
			
			@Override
			public int read() throws IOException {
				try{
				 	return stream[index++];
				} catch(IndexOutOfBoundsException exc) {
					return -1;
				}
			}
		}));
		
		try {
			request = new HttpRequest(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {
		request = null;
	}

	@Test
	public void testHasHeader() {
		assertTrue(request.hasHeader("User-Agent"));
	}

	@Test
	public void testGetHeaderExists() {
		assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", request.getHeader("Accept"));
	}
	
	@Test
	public void testGetHeaderNull() {
		assertEquals(null, request.getHeader("Not-Existing-Header"));
	}

	@Test
	public void testGetMethod() {
		assertEquals("POST", request.getMethod());
	}

	@Test
	public void testGetRequestUri() {
		assertEquals("/test/uri", request.getRequestUri());
	}

	@Test
	public void testGetQueryString() {
		assertEquals("query=string&is=present", request.getQueryString());
	}

	@Test
	public void testGetProtocol() {
		assertEquals("HTTP/1.1", request.getProtocol());
	}

	@Test
	public void testPostDataItems() {
		assertEquals("aaa", request.getParameter("username"));
	}
	
	@Test
	public void testAllPrameterMapContents() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("password", "bbb");
		map.put("query", "string");
		map.put("is", "present");
		map.put("username", "aaa");
		assertEquals(map, request.getParameterMap());
	}
}
