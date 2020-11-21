package com.questions.red;

import java.io.IOException;

public class Neighbour extends Thread {

	private final Connection connection;
	private final EventReceive command;

	private final String nodeUsername;

	private String username;
	private String alias;

    public Neighbour(Connection connection, String nodeUsername, EventReceive command) {
		this.connection = connection;
		this.command = command;
		this.nodeUsername = nodeUsername;
	}

	public void run() {
		while (true) {
			try {
				this.command.command(connection.receive());
			} catch (IOException e) {
				System.out.println("error on receive: "+ e.getMessage());
			}
		}
	}

	public void sent(byte[] data) throws IOException {
		this.connection.send(data);
	}

	public String getUsername() { return this.username; }

	public String getAlias() { return this.alias; }

	public void setAliasUsername(String alias) { this.alias = alias; }
}
