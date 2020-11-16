package com.questions.game.client;

import com.questions.console.Console;
import com.questions.game.client.events.EventQuestion;
import com.questions.game.client.events.EventNewHostRound;
import com.questions.game.client.events.EventResult;
import com.questions.game.client.events.EventFinish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {

    private final Socket socket;
    private final PrintWriter printer;
    private final BufferedReader reader;

    public EventQuestion newQuestion;
    public EventNewHostRound newRound;
    public EventResult result;
    public EventFinish finishRound;

    public Client(ClientConfig config) throws ClientError {
        try {
            this.socket = new Socket(config.getHost(), config.getPort());

            this.printer = new PrintWriter(this.socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            String err = this.sendCredentials(config.getUserName());
            if (!err.isEmpty()) {
                throw new ClientError(err);
            }

        } catch (IOException e) {
            throw new ClientError("cannot connect to: " + config.getHost() + ":" + config.getPort());
        }
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

    private String sendCredentials(String userName) throws ClientError {
        this.send(userName);
        return this.receive();
    }

    public void sendAnswer(int questionId, String answer) {
        this.send("answer:" + questionId + ";" + answer);
    }

    public void sendHostRound(String host, int port) {
        this.send("host:" + host + ";" + port);
    }

    private void send(String msg) {
        this.printer.println(msg);
    }

    private String receive() throws ClientError {
        try { return this.reader.readLine(); }
        catch (IOException e) { throw new ClientError("cant get message from host: " + e.getMessage()); }
    }

    public void close() throws ClientError {
        try { this.socket.close(); }
        catch (IOException e) {
            throw new ClientError("error on close socket: " + e.getMessage());
        }
    }
}
