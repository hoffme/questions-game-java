package com.questions.game.host;

import com.questions.console.Console;
import com.questions.game.host.events.EventWinner;
import com.questions.game.host.questionnaire.Answer;
import com.questions.game.host.peer.Peer;
import com.questions.game.host.peer.PeerError;
import com.questions.game.host.questionnaire.Questionnaire;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Host extends Thread {

    private final HashSet<Peer> peers;
    private final ServerSocket socket;
    private final Questionnaire questionnaire;

    private boolean registerClient;

    public Host(HostConfig config, EventWinner eventWinner) throws HostError {
        this.questionnaire = new Questionnaire(config.getQuestions(), eventWinner);
        this.peers = new HashSet<>();

        try { this.socket = new ServerSocket(config.getPort()); }
        catch (IOException e) {
            throw new HostError("error to init host: " + e.getMessage());
        }

        this.registerClient = true;
    }

    @Override
    public void run() {
        super.run();

        while (this.registerClient) {
            try {
                Socket sock = this.socket.accept();
                if (!this.registerClient) break;

                Peer peer = new Peer(sock);
                peer.start();

                this.peers.add(peer);
            } catch (PeerError e) {
                Console.writer.println(e.getMessage());
            } catch (IOException e) {
                Console.writer.println("error on accept peer connection " + e.getMessage());
            }
        }
    }

    public void startRound() {
        this.registerClient = false;
        this.sendNewQuestion();
    }

    private void sendNewQuestion() {
        this.questionnaire.next();
        this.questionnaire.open();

        for (Peer peer: this.peers) {
            peer.sendQuestion(this.questionnaire.getQuestion(), this::newAnswer);
        }
    }

    private void newAnswer(Peer peer, int questionID, String answerString) {
        Answer answer = this.questionnaire.answer(questionID, peer, answerString);

        boolean answerActualQuestion = this.questionnaire.actualQuestion().id == questionID;
        boolean answerLater = !this.questionnaire.isOpen();
        boolean answerCorrect = answer.correct();

        if (answerActualQuestion && !answerLater && answerCorrect) {
            this.sendResults(peer, answer);
            this.sendNewQuestion();
        }
    }

    private void sendResults(Peer peerResponse, Answer answer) {
        this.questionnaire.close();

        for (Peer peer: this.peers) {
            boolean win = false;
            if (peer == peerResponse) win = true;
            peer.sendResults(answer, win, peerResponse.username);
        }
    }
}
