package game.host;

import game.questions.Question;

import java.io.*;
import java.net.Socket;

public class Peer extends Thread {

    public final String username;

    private final BufferedReader reader;
    private final PrintWriter writer;
    private EventAnswer eventAnswer;

    public Peer(Socket sock) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        this.writer = new PrintWriter(sock.getOutputStream(), true);

        this.username = this.reader.readLine();
        this.writer.println("ok");

        System.out.println("register:" + this.username);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            String answer = "";
            try {
                answer = this.reader.readLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            this.eventAnswer.newAnswer(this, answer);
        }
    }

    public void sendQuestion(Question question, EventAnswer event) {
        this.writer.println(question.title);
        this.eventAnswer = event;
    }

    public void sendAnswerLater() {
        this.writer.println("2");
    }

    public void sendAnswerCorrect() {
        this.writer.println("0");
    }

    public void sendAnswerIncorrect() {
        this.writer.println("1");
    }
}
