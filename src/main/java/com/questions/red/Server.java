package com.questions.red;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server extends Thread {

	private final ServerSocket serverSocket;
	protected boolean acceptConnections;
	
	public Server(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
	}

	public void run() {
		this.acceptConnections = true;

		try {
			while (this.acceptConnections) {
				Socket sock = serverSocket.accept();
				this.newConnection(new Connection(sock));
			}
		} catch (IOException e) {
			System.out.println("error on connect: " + e.getMessage());
		}

	}

	protected abstract void newConnection(Connection connection);
}

