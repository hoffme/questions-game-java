package com.questions;

import com.questions.client.ClientController;
import com.questions.client.ClientError;
import com.questions.host.HostController;
import com.questions.utils.Console;

public class App {

    private final HostController host;
    private final ClientController client;

    public App() {
        this.host = new HostController();
        this.client = new ClientController();

        this.host.eventHostRound = (String host, int port) -> {
            Console.writer.println("\nChanging to the new host [" + host + ":" + port + "]\n");

            this.client.config.setConnectionHost(host);
            this.client.config.setConnectionPort(port);

            this.client.start();
            this.host.stop();
            this.client.waitFinish();
        };

        this.client.eventWinner = () -> {
            Console.writer.println("\nConfigure the host to next round:\n");

            this.host.config.configure();
            this.host.start();

            try {
                this.client.notifyHostReady(this.host.config.getHost(), this.host.config.getPort());
            } catch (ClientError clientError) {
                Console.writer.println("error send host round: " + clientError.getMessage());
                this.host.stop();
                return;
            }

            this.client.stop();
            this.host.waitFinish();
        };
    }

    public void startHost() {
        this.host.config.configure();
        this.host.start();
        this.host.waitFinish();
    }

    public void startClient() {
        this.client.config.configure();
        this.client.start();
        this.client.waitFinish();
    }
}
