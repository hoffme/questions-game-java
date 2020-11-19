package com.questions.host;

import com.questions.game.Commands;
import com.questions.game.Commands.*;
import com.questions.host.peer.EventsPeer;
import com.questions.host.peer.PeerError;
import com.questions.host.questionnaire.Answer;
import com.questions.host.peer.Peer;
import com.questions.host.questionnaire.Questionnaire;
import com.questions.host.questionnaire.EventsQuestionnaire;
import com.questions.utils.Connection;
import com.questions.utils.Console;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Host extends Thread implements EventsPeer, EventsQuestionnaire {

    private final HostConfig config;

    private ServerSocket server;

    private final HashMap<String, Peer> peers;
    private final Questionnaire questionnaire;
    private final EventsHost events;

    private boolean registersOpened;

    public Host(HostConfig config, EventsHost events) {
        this.events = events;
        this.config = config;
        this.peers = new HashMap<>();
        this.questionnaire = new Questionnaire(config.getQuestions(), this);
        this.registersOpened = true;
    }

    @Override
    public void run() {
        super.run();

        this.server = null;
        try { this.server = new ServerSocket(this.config.getPort()); }
        catch (IOException e) {
            Console.writer.println("error on start server: " + e.getMessage());
            return;
        }

        this.waitPeers();
    }

    private void waitPeers() {
        while (this.registersOpened) {
            try {
                Socket sock = this.server.accept();
                if (!this.registersOpened) return;

                this.registerPeer(sock);

            } catch (IOException e) {
                Console.writer.println("error on register peer: " + e.getMessage());
            }
        }
    }

    private void registerPeer(Socket socket) throws IOException {
        Connection conn = new Connection(socket);

        Credentials credentials = Credentials.parseFrom(conn.receive());

        boolean validRegister = !this.peers.containsKey(credentials.getUsername());

        CredentialsResponse response = CredentialsResponse.newBuilder()
                .setError(validRegister ? "" : "username already register")
                .build();
        conn.send(response.toByteArray());

        if (!validRegister) {
            conn.close();
            return;
        }

        Peer peer = new Peer(conn, credentials.getUsername(), this);
        peer.start();

        this.peers.put(credentials.getUsername(), peer);
        this.events.peerConnection(peer);
    }

    public void startRound() {
        this.registersOpened = false;
        this.sendNewQuestion();
    }

    private void sendNewQuestion() {
        if (!this.questionnaire.next()) return;

        this.questionnaire.open();
        for (Peer peer: this.peers.values()) {
            try { peer.sendQuestion(this.questionnaire.getQuestion()); }
            catch (PeerError peerError) {
                Console.writer.println(peerError.getMessage());
            }
        }
    }

    private void sendResults(Peer peerResponse, Answer answer) {
        this.questionnaire.close();

        for (Peer peer: this.peers.values()) {
            boolean responseCorrect = false;
            if (peer == peerResponse) responseCorrect = true;

            try { peer.sendResults(answer, responseCorrect); }
            catch (PeerError peerError) {
                Console.writer.println(peerError.getMessage());
            }
        }
    }

    @Override
    public void newWinner(String peer, List<Answer> answers) {
        events.winner(this.peers.get(peer), answers);
    }

    @Override
    public void newAnswer(Peer peer, Commands.Answer answerCmd) {
        Answer answer = this.questionnaire.answer(answerCmd.getQuestionId(), peer.username, answerCmd.getAnswer());

        boolean answerActualQuestion = this.questionnaire.actualQuestion().id == answerCmd.getQuestionId();
        boolean answerLater = !this.questionnaire.isOpen();

        this.events.newAnswer(answer);

        if (answerActualQuestion && !answerLater && answer.isCorrect()) {
            this.sendResults(peer, answer);
            this.sendNewQuestion();
        } else if (answerActualQuestion && this.questionnaire.numberResponse() == this.peers.size()) {
            this.sendResults(null, this.questionnaire.correctAnswer());
            this.sendNewQuestion();
        }
    }

    @Override
    public void hostChangeRound(Peer peerHost, ChangeHostRound changeRound) {
        for (Peer peer: this.peers.values()) {
            try { peer.sendNewHostRound(changeRound.getHost(), changeRound.getPort()); }
            catch (PeerError peerError) {
                Console.writer.println(peerError.getMessage());
            }
        }

        this.events.newHostRound(changeRound.getHost(), changeRound.getPort());
    }

    public void close() throws HostError {
        try {
            for (Peer peer: this.peers.values()) peer.closeConnection();
            this.server.close();
        } catch (IOException | PeerError e) {
            throw new HostError(e.getMessage());
        }

    }
}
