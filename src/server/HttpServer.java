/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author Tsvetelin Tsonev <tsvetelin.tsonev@yahoo.co.uk>
 */
public class HttpServer extends Thread {

	private int port;
	private String address;
	private String name;
	private String remoteAddress;
	private boolean shutdown = false;
	private Socket socket = null;
	private SocketUtil socketUtil = null;

	public HttpServer() {
		this(6060);
	}

	public HttpServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			address = server.getInetAddress().getHostAddress();

			while (!shutdown) {
				// Listen.
				socket = server.accept();

				SocketAddress socketAddress = socket.getRemoteSocketAddress();
				this.remoteAddress = ((InetSocketAddress) socketAddress).getHostString();

				socketUtil = new SocketUtil(socket);

				Request request = new HttpRequest(socketUtil.getReader());
				// Check for Host header, and set server name if present.
				if (request.hasHeader("Host")) {
					name = request.getHeader("Host");
				} else {
					name = server.getInetAddress().getHostName();
				}

				Response response = new HttpResponse(socketUtil.getWriter());
				response.setStatus(HttpStatus.OK);

				try {
					String html = FilesUtil.getFileContents(request.getHeader("Response-File"));
					response.setMesssageBody(html);
				} catch (FileNotFoundException exc) {
					response.setStatus(HttpStatus.NOT_FOUND);
				}
				response.send();
				socketUtil.closeSocket();
			}
			// Terminate the app after the loop has finished.
			terminateApplication();
		} catch (Exception ex) {
			ex.printStackTrace();
			// Terminate
			terminateApplication();
		} finally {
			try {
				if (server != null) {
					server.close();
				}
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}

	public void shutdown() {
		shutdown = true;
	}

	private void terminateApplication() {
		System.exit(1);
	}

	public int getPort() {
		return port;
	}

	public String getAddress() {
		return address;
	}

	public String getServerName() {
		return name;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	@Override
	public String toString() {
		return "HttpServer [port=" + port + ", address=" + address + ", name=" + name + ", remoteAddress="
				+ remoteAddress + "]";
	}

}
