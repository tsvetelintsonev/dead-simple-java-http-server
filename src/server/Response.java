package server;

/**
 * @author Tsvetelin Tsonev <tsvetelin.tsonev@yahoo.co.uk>
 */
public interface Response {
	void send();
	void setStatus(HttpStatus status);
	HttpStatus getStatus();
	void addHeader(String header);
	void setMesssageBody(String messageBody);
	String getMessageBody();
	int getContentLength();
}
