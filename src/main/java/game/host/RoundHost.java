package game.host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class RoundHost extends Thread {

    private final HashSet<Peer> peers;
    private final ServerSocket socket;
    private boolean started;
    private final Questionnaire questionnaire;

    public RoundHost(HostConfig config, EventWinner eventWinner) throws IOException {
        this.questionnaire = new Questionnaire(config.getQuestions(), eventWinner);
        this.peers = new HashSet<>();
        this.socket = new ServerSocket(config.getPort());
        this.started = false;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                Socket sock = this.socket.accept();
                if (this.started) break;

                Peer peer = new Peer(sock);
                peer.start();

                this.peers.add(peer);
            } catch (IOException e) { e.printStackTrace();  }
        }

        System.out.println("killed");
    }

    public void activeRound() { this.started = true; }

    public void sendQuestion() {
        System.out.println("sending");

        this.questionnaire.next();
        this.questionnaire.open();

        for (Peer peer: this.peers) {
            peer.sendQuestion(
                    this.questionnaire.getQuestion(),
                    (peerAnswer, answer) -> {
                        try {
                            if (this.questionnaire.answer(peerAnswer, answer)) {
                                peerAnswer.sendAnswerCorrect();
                            } else {
                                peerAnswer.sendAnswerIncorrect();
                            }
                        } catch (Exception e) {
                            peerAnswer.sendAnswerLater();
                        }
                    }
            );
        }
    }

    public Map<Peer, List<Answer>> results() {
        return this.questionnaire.results();
    }
}
