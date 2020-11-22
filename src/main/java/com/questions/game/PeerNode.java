package com.questions.game;

import com.questions.CommandOuterClass.*;
import com.questions.red.Neighbour;
import com.questions.red.Node;

import java.io.IOException;

public class PeerNode extends Node {

    public PeerNode(String username, int port) throws IOException {
        super(username, port);
    }

    // ------------------------- commands send -------------------------

    // Initialize game
    public void sendGameRequest() {

    }

    public void sendCreateMesh() {
        CreateMesh.Builder cmd = CreateMesh.newBuilder();
        for (Neighbour neighbour: this.neighbours.values()) {
            cmd.addAddresses(Address.newBuilder()
                    .setAlias(neighbour.getAlias())
                    .setHost(neighbour.getHost())
                    .setPort(neighbour.getPort())
                    .build()
            );
        }

        for (Neighbour neighbour: this.neighbours.values()) {
            try {
                neighbour.send(Command.newBuilder().setCreateMesh(cmd).build());
            } catch (IOException e) {
                System.out.println("error on connect to ["+neighbour.getAlias()+"]: " + e.getMessage());
            }
        }
    }

    public void sendMeshReady(Neighbour initialHost, boolean ok) {
        try {
            initialHost.send(Command.newBuilder().setReadyMesh(ReadyMesh
                    .newBuilder()
                    .setOk(ok)
                    .build()
            ).build());
        } catch (IOException e) {
            System.out.println("error on connect to ["+initialHost.getAlias()+"]: " + e.getMessage());
        }
    }

    // ------------------------- commands receive -------------------------
    @Override
    protected void receive(Neighbour neighbour, Command command) {
        if (command.hasCreateMesh()) this.receiveCreateMesh(neighbour, command.getCreateMesh());
    }

    private void receiveCreateMesh(Neighbour neighbour, CreateMesh createMesh) {
        for (Address addr: createMesh.getAddressesList()) {
            if (this.neighbours.containsKey(addr.getAlias())) continue;

            try {
                this.connect(addr.getHost(), addr.getPort());
            } catch (IOException e) {
                System.out.println("error on connect to: " + addr.getAlias());
            }
        }

        this.sendMeshReady(neighbour, true);
    }
}
