package com.questions.red;

import com.questions.CommandOuterClass.*;

import java.io.IOException;

public class Neighbour extends Thread {

	private final Connection connection;
	private final NodeReceiver command;

	private final String alias;

    public Neighbour(Connection connection, String nodeAlias, NodeReceiver command) throws IOException {
		this.connection = connection;
		this.command = command;

		this.send(
				Command
				.newBuilder()
				.setRegister(Register
						.newBuilder()
						.setAlias(nodeAlias)
				).build()
		);

		Register register = Command.parseFrom(this.connection.receive()).getRegister();
		this.alias = register.getAlias();
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

	public String getAlias() { return this.alias; }
}
