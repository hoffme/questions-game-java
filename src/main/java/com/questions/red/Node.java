package com.questions.red;

import com.questions.CommandOuterClass;
import com.questions.CommandOuterClass.Command;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public abstract class Node extends Server {

	protected final Map<String, Neighbour> neighbours;
	protected final String username;

	public Node(String username, int port) throws IOException {
		super(port);

		this.neighbours = new HashMap<>();
		this.username = username;

		this.start();
	}

	private void addNeighbour(Connection connection) {
		Neighbour neighbour = null;
		try {
			neighbour = new Neighbour(connection, this.username, this::receive);
		} catch (IOException e) {
			System.out.println("error on connect to neighbour: " + e.getMessage());
			return;
		}

		this.neighbours.put(neighbour.getAlias(), neighbour);
	}

	@Override
	protected void newConnection(Connection connection) { this.addNeighbour(connection); }

	protected void connect(String host, int port) throws IOException {
		this.addNeighbour(new Connection(new Socket(host, port)));
	}

	protected void send(String alias, Command cmd) throws IOException {
		this.neighbours.get(alias).send(cmd);
	}

	protected abstract void receive(Neighbour neighbour, Command command);
}

