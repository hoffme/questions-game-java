package com.questions.host;

import com.questions.game.Commands;
import com.questions.host.events.EventNewAnswer;
import com.questions.host.events.EventNewPeer;
import com.questions.host.events.EventWinner;
import com.questions.host.questionnaire.Answer;
import com.questions.host.peer.Peer;
import com.questions.host.questionnaire.Questionnaire;
import com.questions.utils.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Host extends Thread {

    private final HostConfig config;

    private final HashMap<String, Peer> peers;
    private final Questionnaire questionnaire;

    private boolean stateRegister;

    public EventWinner eventWinner;
    public EventNewPeer eventNewPeer;
    public EventNewAnswer eventNewAnswer;

    public Host(HostConfig config) {
        this.config = config;

        this.questionnaire = new Questionnaire(config.getQuestions());
        this.questionnaire.eventWinner = this::winner;

        this.peers = new HashMap<>();

        this.stateRegister = true;
    }

    @Override
    public void run() {
        super.run();

        try {
            ServerSocket server = new ServerSocket(this.config.getPort());
            while (this.stateRegister) {
                this.waitAndRegister(server);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitAndRegister(ServerSocket server) throws IOException {
        Socket sock = server.accept();
        if (!this.stateRegister) return;

        Connection conn = new Connection(sock);

        Commands.Credentials credentials = Commands.Credentials.parseFrom(conn.receive());

        String err = "";
        if (this.peers.containsKey(credentials.getUsername())) {
            err = "username already register";
        }

        Commands.CredentialsResponse response = Commands.CredentialsResponse.newBuilder()
                .setError(err)
                .build();
        conn.send(response.toByteArray());

        Peer peer = new Peer(conn, credentials.getUsername());
        peer.eventAnswer = this::newAnswer;
        peer.eventHostRound = this::hostRound;

        this.peers.put(credentials.getUsername(), peer);
        if (this.eventNewPeer != null) this.eventNewPeer.event(peer);
    }

    private void hostRound(String host, Integer port) {
        for (Peer peer: this.peers.values()) {
            peer.sendNewHostRound(host, port);
        }
    }

    public void startRound() {
        this.stateRegister = false;
        this.sendNewQuestion();
    }

    private void sendNewQuestion() {
        this.questionnaire.next();
        this.questionnaire.open();

        for (Peer peer: this.peers.values()) {
            peer.sendQuestion(this.questionnaire.getQuestion());
        }
    }

    private void newAnswer(Peer peer, int questionID, String answerString) {
        Answer answer = this.questionnaire.answer(questionID, peer.username, answerString);

        boolean answerActualQuestion = this.questionnaire.actualQuestion().id == questionID;
        boolean answerLater = !this.questionnaire.isOpen();
        boolean answerCorrect = answer.correct();

        if (this.eventNewAnswer != null) this.eventNewAnswer.event(answer);

        if (answerActualQuestion && !answerLater && answerCorrect) {
            this.sendResults(peer, answer);
            this.sendNewQuestion();
        }
    }

    private void sendResults(Peer peerResponse, Answer answer) {
        this.questionnaire.close();

        for (Peer peer: this.peers.values()) {
            boolean win = false;
            if (peer == peerResponse) win = true;
            peer.sendResults(answer, win, peerResponse.username);
        }
    }

    public void winner(String peer, List<Answer> answers) {
        this.eventWinner.win(peer, answers);
    }
}
