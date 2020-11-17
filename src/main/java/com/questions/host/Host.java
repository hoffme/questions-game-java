package com.questions.host;

import com.questions.game.Commands;
import com.questions.game.Commands.*;
import com.questions.host.peer.EventsPeer;
import com.questions.host.questionnaire.Answer;
import com.questions.host.peer.Peer;
import com.questions.host.questionnaire.Questionnaire;
import com.questions.host.questionnaire.QuestionnaireEvents;
import com.questions.utils.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Host extends Thread implements EventsPeer, QuestionnaireEvents {

    private final HostConfig config;

    private final HashMap<String, Peer> peers;
    private final Questionnaire questionnaire;
    private final EventsHost events;

    private boolean stateRegister;

    public Host(HostConfig config, EventsHost events) {
        this.events = events;
        this.config = config;

        this.peers = new HashMap<>();

        this.questionnaire = new Questionnaire(config.getQuestions(), this);

        this.stateRegister = true;
    }

    @Override
    public void run() {
        super.run();

        try {
            ServerSocket server = new ServerSocket(this.config.getPort());
            while (this.stateRegister) {
                Socket sock = server.accept();
                if (!this.stateRegister) return;

                this.register(sock);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void register(Socket socket) throws IOException {
        Connection conn = new Connection(socket);

        Credentials credentials = Credentials.parseFrom(conn.receive());

        boolean validRegister = !this.peers.containsKey(credentials.getUsername());

        CredentialsResponse response = CredentialsResponse.newBuilder()
                .setError(validRegister ? "" : "username already register")
                .build();
        conn.send(response.toByteArray());

        Peer peer = new Peer(conn, credentials.getUsername(), this);

        this.peers.put(credentials.getUsername(), peer);
        this.events.peerConnection(peer);
    }

    public void startRound() {
        this.stateRegister = false;
        this.sendNewQuestion();
    }

    private void sendNewQuestion() {
        if (!this.questionnaire.next()) return;

        this.questionnaire.open();

        for (Peer peer: this.peers.values()) {
            peer.sendQuestion(this.questionnaire.getQuestion());
        }
    }

    private void sendResults(Peer peerResponse, Answer answer) {
        this.questionnaire.close();

        for (Peer peer: this.peers.values()) {
            boolean responseCorrect = false;
            if (peer == peerResponse) responseCorrect = true;
            peer.sendResults(answer, responseCorrect, peerResponse.username);
        }
    }

    @Override
    public void winner(String peer, List<Answer> answers) {
        events.winner(this.peers.get(peer), answers);
    }

    @Override
    public void answer(Peer peer, Commands.Answer answerCmd) {
        Answer answer = this.questionnaire.answer(answerCmd.getQuestionId(), peer.username, answerCmd.getAnswer());

        boolean answerActualQuestion = this.questionnaire.actualQuestion().id == answerCmd.getQuestionId();
        boolean answerLater = !this.questionnaire.isOpen();
        boolean answerCorrect = answer.correct();

        this.events.answer(answer);

        if (answerActualQuestion && !answerLater && answerCorrect) {
            this.sendResults(peer, answer);
            this.sendNewQuestion();
        }
    }

    @Override
    public void changeRound(Peer peerHost, ChangeHostRound changeRound) {
        for (Peer peer: this.peers.values()) {
            peer.sendNewHostRound(changeRound.getHost(), changeRound.getPort());
        }
    }
}
