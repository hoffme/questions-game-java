package com.questions.red;

import com.questions.CommandOuterClass.*;

import java.io.IOException;

public class Neighbour extends Thread {

	private final Connection connection;
	private final NodeReceiver command;

	private final String username;
	private final String alias;

    public Neighbour(Connection connection, String nodeUsername, NodeReceiver command) throws IOException {
		this.connection = connection;
		this.command = command;

		this.send(
				Command
				.newBuilder()
				.setReport(Report
						.newBuilder()
						.setUsername(nodeUsername)
				).build()
		);

		Report report = Command.parseFrom(this.connection.receive()).getReport();

		this.username = report.getUsername();
		this.alias = this.username + "@" + this.getHost() + ":" + this.getPort();
	}

	public void run() {
		while (true) {
			try {
				this.command.command(this, Command.parseFrom(this.connection.receive()));
			} catch (IOException e) {
				System.out.println("error on receive from ("+this.alias+"): "+ e.getMessage());
			}
		}
	}

	public void send(Command cmd) throws IOException { this.connection.send(cmd.toByteArray()); }

	public String getUsername() { return this.username; }

	public String getAlias() { return this.alias; }

	public String getHost() { return this.connection.getHost(); }

	public int getPort() { return this.connection.getPort(); }
}
