package com.questions.game.host;

import com.questions.console.Console;
import com.questions.game.host.events.EventNewAnswer;
import com.questions.game.host.events.EventNewPeer;
import com.questions.game.host.events.EventWinner;
import com.questions.game.host.questionnaire.Answer;
import com.questions.game.host.peer.Peer;
import com.questions.game.host.peer.PeerError;
import com.questions.game.host.questionnaire.Questionnaire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Host extends Thread {

    private final HashMap<String, Peer> peers;
    private final ServerSocket socket;
    private final Questionnaire questionnaire;

    private boolean stateRegister;

    public EventWinner eventWinner;
    public EventNewPeer eventNewPeer;
    public EventNewAnswer eventNewAnswer;

    public Host(HostConfig config) throws HostError {
        this.questionnaire = new Questionnaire(config.getQuestions());
        this.questionnaire.eventWinner = this::winner;

        this.peers = new HashMap<>();

        try { this.socket = new ServerSocket(config.getPort()); }
        catch (IOException e) {
            throw new HostError("error to init host: " + e.getMessage());
        }

        this.stateRegister = true;
    }

    @Override
    public void run() {
        super.run();

        while (this.stateRegister) {
            try {
                Socket sock = this.socket.accept();
                if (!this.stateRegister) break;
                this.register(sock);
            } catch (IOException e) {
                Console.writer.println("error register peer: " + e.getMessage());
            }
        }
    }

    private void register(Socket sock) throws IOException {
        BufferedReader sockReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        PrintWriter sockWriter = new PrintWriter(sock.getOutputStream(), true);

        String username = sockReader.readLine();
        sockWriter.println("");

        if (this.peers.containsKey(username)) {
            sockWriter.println("username already register");
        }

        Peer peer = new Peer(sockReader, sockWriter, username);
        peer.eventAnswer = this::newAnswer;
        peer.eventHostRound = this::hostRound;

        this.peers.put(username, peer);

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
