package com.questions.host.peer;

import com.google.protobuf.GeneratedMessageV3;
import com.questions.game.Commands;
import com.questions.game.Commands.*;
import com.questions.utils.Connection;
import com.questions.utils.Console;
import com.questions.host.questionnaire.Answer;
import com.questions.host.questionnaire.Question;

import java.io.*;

public class Peer extends Thread {

    public final String username;
    private final Connection conn;
    private final EventsPeer events;

    private boolean connect;

    public Peer(Connection conn, String username, EventsPeer events) {
        this.conn = conn;
        this.username = username;
        this.events = events;
        this.connect = true;
    }

    @Override
    public void run() {
        while (this.connect) {
            try {
                ClientCommand cmd = ClientCommand.parseFrom(this.conn.receive());
                if (!this.connect) break;

                if (cmd.hasAnswer()) this.events.newAnswer(this, cmd.getAnswer());
                else if (cmd.hasChangeRound()) this.events.hostChangeRound(this, cmd.getChangeRound());

            } catch (IOException e) {
                Console.writer.println("error with command: " + e.getMessage());
            }
        }
    }

    public void sendQuestion(Question question) throws PeerError {
        this.sentCommand(HostCommand.newBuilder()
            .setQuestion(Commands.Question.newBuilder()
                .setId(question.id)
                .setTitle(question.title)
                .setType(question.type)
                .build()
            ).build()
        );
    }

    public void sendResults(Answer answerCorrect, boolean win) throws PeerError {
        this.sentCommand(HostCommand.newBuilder()
                .setResult(Result.newBuilder()
                        .setAnswer(answerCorrect.getAnswer())
                        .setMe(win)
                        .setQuestionId(answerCorrect.getQuestion().id)
                        .build()
                ).build()
        );
    }

    public void sendNewHostRound(String host, Integer port) throws PeerError {
        this.sentCommand(HostCommand.newBuilder()
                .setChangeRound(ChangeHostRound.newBuilder()
                    .setHost(host).setPort(port).build()
                ).build()
        );
    }

    public void closeConnection() throws PeerError {
        this.connect = false;
        try { this.conn.close(); }
        catch (IOException e) {
            throw new PeerError("error on close connection: " + e.getMessage());
        }
    }

    private void sentCommand(GeneratedMessageV3 protoMessage) throws PeerError {
        try { this.conn.send(protoMessage.toByteArray()); }
        catch (IOException e) { throw new PeerError("error to sent message: " + e.getMessage()); }
    }
}
