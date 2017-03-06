 package server;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tsvetelin Tsonev <tsvetelin.tsonev@yahoo.co.uk>
 */
public class HttpResponse implements Response {
	
	private PrintWriter writer = null;
	private List<String> headers = null;
	private String messageBody = "";
	private HttpStatus status;
	
	public HttpResponse(PrintWriter writer) {
		this.writer = writer;
		headers = new ArrayList<String>();
	}
	
	@Override
	public void send() {
		if( status == HttpStatus.NOT_FOUND ) {
			writer.println("HTTP/1.1 404 NOT FOUND");
			writer.println("Content-Type: text/html; charset=utf-8");
			writer.println("Connection: keep-alive");
			writeHeaders();
			writer.println("Server: Java NTT Server 1.0.0");
			writer.println();
			writer.println(messageBody);
			writer.flush();
		} else {
			writer.println("HTTP/1.1 200 OK");
			writer.println("Content-Type: text/html; charset=utf-8");
			writer.println("Content-Length: " + String.valueOf(messageBody.length()));
			writer.println("Connection: keep-alive");
			writeHeaders();
			writer.println("Server: Java NTT Server 1.0.0");
			writer.println();
			writer.println(messageBody);
			writer.flush();
		}
	}
	
	private void writeHeaders() {
		for(String header : headers) {
			writer.println(header);
		}
	}
	
	@Override
	public HttpStatus getStatus() {
		return status;
	}
	
	@Override
	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	
	@Override
	public void addHeader(String header) {
		headers.add(header);
	}

	@Override
	public void setMesssageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	@Override
	public String getMessageBody() {
		return messageBody;
	}
	
	@Override
	public int getContentLength() {
		return messageBody.length();
	}
	
}
