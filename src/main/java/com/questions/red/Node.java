package com.questions.red;

import com.questions.CommandOuterClass.Command;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public abstract class Node extends Server {

	public final Map<String, Neighbour> neighbours;

	public final String alias;
	public final String host;
	public final int port;

	public Node(String username, String host, int port) throws IOException {
		super(port);

		this.neighbours = new HashMap<>();

		this.alias = username + "@" + host + ":" + port;
		this.host = host;
		this.port = port;

		this.start();
	}

	private void addNeighbour(Connection connection) {
		Neighbour neighbour = null;
		try {
			neighbour = new Neighbour(connection, this.alias, this::receive);
		} catch (IOException e) {
			System.out.println("error on connect to neighbour: " + e.getMessage());
			return;
		}

		this.neighbours.put(neighbour.getAlias(), neighbour);
	}

	@Override
	protected void newConnection(Connection connection) { this.addNeighbour(connection); }

	public void connect(String host, int port) throws IOException {
		this.addNeighbour(new Connection(new Socket(host, port)));
	}

	public void send(String alias, Command cmd) throws IOException {
		this.neighbours.get(alias).send(cmd);
	}

	public String getAlias() {
		return this.alias;
	}

	abstract protected void receive(Neighbour neighbour, Command command);
}

