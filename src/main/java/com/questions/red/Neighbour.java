package com.questions.red;

import com.questions.CommandOuterClass.*;

import java.io.IOException;

public class Neighbour extends Thread {

	private final Connection connection;
	private final NeighbourListener listener;

	private final String alias;

    public Neighbour(Connection connection, String nodeAlias, NeighbourListener listener) throws IOException {
		this.connection = connection;
		this.listener = listener;

		this.connection.send(
			Command
			.newBuilder()
			.setRegister(Register.newBuilder().setAlias(nodeAlias))
			.build()
			.toByteArray()
		);

		Register register = Command.parseFrom(this.connection.receive()).getRegister();
		this.alias = register.getAlias();

		this.start();
	}

	public void run() {
		while (true) {
			try {
				this.listener.peerCommand(this, Command.parseFrom(this.connection.receive()));
			} catch (IOException e) {
				this.listener.peerDisconnected(this);
				break;
			}
		}
	}

	public void send(Command cmd) {
		try {
			this.connection.send(cmd.toByteArray());
		} catch (IOException e) {
			this.listener.peerDisconnected(this);
		}
	}

	public String getAlias() { return this.alias; }

	public void close() throws IOException {
		this.connection.close();
		this.listener.peerDisconnected(this);
	}
}
