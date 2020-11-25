package com.questions.red;

import com.questions.CommandOuterClass.Command;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public abstract class Node extends Server implements NeighbourListener {

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

	private void newNeighbour(Connection connection) {
		try {
			Neighbour neighbour = new Neighbour(connection, this.alias, this);
			this.neighbours.put(neighbour.getAlias(), neighbour);
		} catch (IOException e) {
			System.out.println("error on connect to neighbour: " + e.getMessage());
		}
	}

	protected void sendAll(Command cmd) {
		for (Neighbour neighbour: this.neighbours.values()) neighbour.send(cmd);
	}

	@Override
	protected void newConnection(Connection connection) { this.newNeighbour(connection); }

	public void connect(String host, int port) throws IOException {
		this.newNeighbour(new Connection(new Socket(host, port)));
	}

	@Override
	public void peerDisconnected(Neighbour neighbour) {
		this.neighbours.remove(neighbour.getAlias());
	}

	@Override
	abstract public void peerCommand(Neighbour neighbour, Command cmd);
}

