package com.questions.client;

import com.questions.client.events.EventQuestion;
import com.questions.client.events.EventNewHostRound;
import com.questions.client.events.EventResult;
import com.questions.client.events.EventFinish;
import com.questions.game.Commands;
import com.questions.utils.Connection;
import com.questions.utils.Console;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    private final Connection conn;

    public EventQuestion newQuestion;
    public EventNewHostRound newRound;
    public EventResult result;
    public EventFinish finishRound;

    public Client(ClientConfig config) throws ClientError {
        try {
            this.conn = new Connection(
                    new Socket(config.getHost(), config.getPort())
            );
        } catch (IOException e) { throw new ClientError("cannot connect"); }

        this.login(config.getUserName());
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                String[] data = this.receive().split(":", 2);

                String typeCommand = data.length > 0 ? data[0] : "";
                String dataCommand = data.length > 1 ? data[1] : "";

                this.newCommand(typeCommand, dataCommand);

            } catch (ClientError clientError) { 
                Console.writer.println(clientError.getMessage());
            }
        }
    }

    private void login(String userName) throws ClientError {
        Commands.Credentials credentials = Commands.Credentials.newBuilder()
                .setUsername(userName)
                .build();

        try {
            this.conn.send(credentials.toByteArray());

            Commands.CredentialsResponse response = Commands.CredentialsResponse.parseFrom(this.conn.receive());
            if (!response.getError().isEmpty()) {
                throw new ClientError("error from host: " + response.getError());
            }
        } catch (IOException e) {
            throw new ClientError("error in connection: " + e.getMessage());
        }
    }

    private void newCommand(String typeCommand, String dataCommand) {
        switch (typeCommand) {
            case "question":
                if (this.newQuestion == null) return;
                String[] question = dataCommand.split(";");
                this.newQuestion.event(Integer.parseInt(question[0]), question[1], question[2]);
                break;
            case "result":
                if (this.result == null) return;
                String[] result = dataCommand.split(";");
                this.result.event(Boolean.parseBoolean(result[0]), result[1], result[2]);
                break;
            case "round":
                if (this.newRound == null) return;
                String[] host = dataCommand.split(";");
                this.newRound.event(host[0], Integer.parseInt(host[1]));
                break;
            case "finish":
                if (this.finishRound == null) return;
                this.finishRound.event(Boolean.parseBoolean(dataCommand));
                break;
        }
    }

    public void sendAnswer(int questionId, String answer) {
        this.send("answer:" + questionId + ";" + answer);
    }

    public void sendHostRound(String host, int port) {
        this.send("host:" + host + ";" + port);
    }

    private void send(String msg) {
        try {
            this.conn.send(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String receive() throws ClientError {
        try { return new String(this.conn.receive()); }
        catch (IOException e) { throw new ClientError("cant get message from host: " + e.getMessage()); }
    }

    public void close() throws ClientError {
        try { this.conn.close(); }
        catch (IOException e) {
            throw new ClientError("error on close connection: " + e.getMessage());
        }
    }
}
