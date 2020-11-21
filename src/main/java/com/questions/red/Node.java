package com.questions.red;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public abstract class Node extends Server {

	private final Map<String, Neighbour> neighbours;
	private final String username;

	public Node(String username, int port) throws IOException {
		super(port);

		this.neighbours = new HashMap<>();
		this.username = username;

		this.start();
	}

	private void addNeighbour(Connection connection) {
		Neighbour neighbour = new Neighbour(connection, this.username, this::receive);
		neighbour.start();

		for (int i = 0; this.neighbours.containsKey(neighbour.getAlias()); i++) {
			neighbour.setAliasUsername(neighbour.getUsername() + "[" + i + "]");
		}

		this.neighbours.put(neighbour.getAlias(), neighbour);
	}

	@Override
	protected void newConnection(Connection connection) { this.addNeighbour(connection); }

	protected void connect(String host, int port) throws IOException {
		this.addNeighbour(new Connection(new Socket(host, port)));
	}

	protected void send(String alias, byte[] data) throws IOException {
		this.neighbours.get(alias).sent(data);
	}

	protected boolean sentAll(byte[] data) {
		boolean allSent = true;
		for (Neighbour neighbour: this.neighbours.values()) {
			try { neighbour.sent(data); }
			catch (IOException ignored) { allSent = false; }
		}
		return allSent;
	}

	abstract void receive(byte[] data);
}

