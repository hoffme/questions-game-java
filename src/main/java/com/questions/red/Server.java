package com.questions.red;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public abstract class Server extends Thread {

	private final ServerSocket serverSocket;
	
	public Server(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
	}

	public void run() {
		try {
			while (true) {
				Socket sock = serverSocket.accept();
				this.newConnection(new Connection(sock));
			}
		} catch (IOException e) {
			System.out.println("error on connect: " + e.getMessage());
		}
	}

	protected abstract void newConnection(Connection connection);
}

