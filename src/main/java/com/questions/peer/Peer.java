package com.questions.peer;

import com.questions.CommandOuterClass.*;
import com.questions.red.Neighbour;
import com.questions.red.Node;
import com.questions.utils.Console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Peer extends Node {

    public PeerListener listener;

    public Peer(String username, String host, int port) throws IOException {
        super(username, host, port);
    }

    public void actionConnect() {
        String[] peers = Console.input("> ").replaceAll("\\s+","").split(",");
        for (String peer: peers) {
            Console.print("connecting " + peer + " -> ");
            try {
                String host = peer.split(":")[0];
                int port = Integer.parseInt(peer.split(":")[1]);
                this.connect(host, port);
                Console.println("successfully");
            } catch (Exception e) {
                Console.println("error: " + e.getMessage());
            }
        }
    }

    public void actionShowConnected() {
        for (Neighbour neighbour: this.neighbours.values()) {
            Console.println(neighbour.getAlias());
        }
    }

    // host
    public void sendRoundRequest(long id) {
        Console.print("sending round request -> ");
        this.sendAll(Command.newBuilder()
                .setRoundRequest(RoundRequest.newBuilder()
                        .setHost(Address
                                .newBuilder()
                                .setAlias(this.alias)
                                .setHost(this.host)
                                .setPort(this.port)
                        )
                        .setId(this.alias + "?" + id)
                ).build()
        );
        Console.println("successfully");
    }

    public void sendRoundRequest(RoundRequest cmd) {
        Console.print("sending round request -> ");
        this.sendAll(Command.newBuilder().setRoundRequest(cmd).build());
        Console.println("successfully");
    }

    public void sendQuestion(com.questions.utils.Question question) {
        this.sendAll(Command.newBuilder()
                .setQuestion(Question.newBuilder()
                        .setId(question.id)
                        .setTitle(question.title)
                        .setType(question.type)
                ).build()
        );
    }

    public void sendAnswerResult() {

    }

    public void sendRoundResult(Neighbour winner) {
        this.sendAll(Command.newBuilder()
                .setRoundResult(RoundResult.newBuilder()
                        .setAliasWinner(winner.getAlias())
                ).build()
        );
    }

    //client
    public void sendAnswer(Neighbour host, int questionId, List<String> answers) {
        Answer.Builder answerPeer = Answer.newBuilder().setQuestionId(questionId);
        for (String answer: answers) answerPeer.addData(answer);
        host.send(Command.newBuilder().setAnswer(answerPeer).build());
    }

    public void sendAnswer(Neighbour host, int questionId, String answer) {
        ArrayList<String> answers = new ArrayList<>();
        answers.add(answer);
        this.sendAnswer(host, questionId, answers);
    }

    public void sendRoundResponse(Neighbour host, boolean ok) {
        host.send(Command.newBuilder()
                .setRoundResponse(RoundResponse.newBuilder().setOk(ok))
                .build()
        );
    }

    @Override
    public void peerCommand(Neighbour neighbour, Command cmd) {
        if (cmd.hasRoundRequest()) this.listener.cmdRoundRequest(neighbour, cmd.getRoundRequest());
        else if (cmd.hasRoundResponse()) this.listener.cmdRoundResponse(neighbour, cmd.getRoundResponse());
        else if (cmd.hasQuestion()) this.listener.cmdQuestion(neighbour, cmd.getQuestion());
        else if (cmd.hasAnswer()) this.listener.cmdAnswer(neighbour, cmd.getAnswer());
        else if (cmd.hasAnswerResult()) this.listener.cmdAnswerResult(neighbour, cmd.getAnswerResult());
        else if (cmd.hasRoundResult()) this.listener.cmdRoundResult(neighbour, cmd.getRoundResult());
    }
}
