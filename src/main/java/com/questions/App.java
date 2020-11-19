package com.questions;

import com.questions.client.ClientConfig;
import com.questions.client.ClientController;
import com.questions.client.ClientError;
import com.questions.host.HostConfig;
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

            ClientConfig config = this.client.config;
            config.setConnectionHost(host);
            config.setConnectionPort(port);

            this.client.start(config);
            this.host.stop();
            this.client.waitFinish();
        };

        this.client.eventWinner = () -> {
            Console.writer.println("\nConfigure the host to next round:\n");

            this.host.config.configure();
            this.host.start(this.host.config);

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
        HostConfig config = new HostConfig();
        config.configure();

        this.host.start(config);
        this.host.waitFinish();
    }

    public void startClient() {
        ClientConfig config = new ClientConfig();
        config.configure();

        this.client.start(config);
        this.client.waitFinish();
    }
}
