package com.questions.client;

import com.google.protobuf.GeneratedMessageV3;

import com.questions.game.Commands.*;
import com.questions.utils.Connection;
import com.questions.utils.Console;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    private final Connection conn;
    private final EventsClient events;

    public Client(ClientConfig config, EventsClient events) throws ClientError {
        this.events = events;

        try {
            this.conn = new Connection(new Socket(config.getHost(), config.getPort()));
        } catch (IOException e) {
            throw new ClientError("error to connect host: " + e.getMessage());
        }

        this.sent(Credentials.newBuilder()
                .setUsername(config.getUserName())
                .build()
        );

        try {
            CredentialsResponse response = CredentialsResponse.parseFrom(this.conn.receive());
            if (!response.getError().isEmpty()) {
                throw new ClientError(response.getError());
            }
        } catch (IOException e) {
            throw new ClientError("error to register: " + e.getMessage());
        }
    }

    private void sent(GeneratedMessageV3 protoMessage) throws ClientError {
        try { this.conn.send(protoMessage.toByteArray()); }
        catch (IOException e) { throw new ClientError("error to sent message: " + e.getMessage()); }
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                HostCommand cmd = HostCommand.parseFrom(this.conn.receive());

                if (cmd.hasChangeRound()) this.events.changeHostRound(cmd.getChangeRound());
                else if (cmd.hasFinish()) this.events.finish(cmd.getFinish());
                else if (cmd.hasQuestion()) this.events.question(cmd.getQuestion());
                else if (cmd.hasResult()) this.events.result(cmd.getResult());

            } catch (IOException err) {
                Console.writer.println("error in connection: " + err.getMessage());
                break;
            }
        }
    }

    public void sendAnswer(int questionId, String answerString) throws ClientError {
        this.sent(ClientCommand.newBuilder()
                .setAnswer(Answer.newBuilder()
                        .setQuestionId(questionId)
                        .setAnswer(answerString)
                        .build())
                .build()
        );
    }

    public void sendHostRound(String host, int port) throws ClientError {
        this.sent(ClientCommand.newBuilder()
                .setChangeRound(ChangeHostRound.newBuilder()
                        .setHost(host)
                        .setPort(port)
                        .build())
                .build()
        );
    }

    public void close() throws ClientError {
        try {
            this.conn.close();
        } catch (IOException e) {
            throw new ClientError("error on close connection: " + e.getMessage());
        }
    }
}
