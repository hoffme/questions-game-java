package com.questions.game.host.peer;

import com.questions.console.Console;
import com.questions.game.host.events.EventAnswer;
import com.questions.game.host.questionnaire.Answer;
import com.questions.game.host.questions.Question;

import java.io.*;
import java.net.Socket;

public class Peer extends Thread {

    public final String username;

    private final BufferedReader reader;
    private final PrintWriter writer;

    private EventAnswer eventAnswer;

    public Peer(Socket sock) throws PeerError {
        try {
            this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            this.writer = new PrintWriter(sock.getOutputStream(), true);
        } catch (IOException e) {
            throw new PeerError("error to connect to peer " + e.getMessage());
        }

        this.username = this.register();
    }

    private String register() throws PeerError {
        String username = "";
        try { username = this.reader.readLine(); }
        catch (IOException e) {
            throw new PeerError("error on register peer " + e.getMessage());
        }

        this.writer.println("");

        return username;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                String[] data = this.reader.readLine().split(":", 2);

                String typeCommand = data.length > 0 ? data[0] : "";
                String dataCommand = data.length > 1 ? data[1] : "";

                this.newCommand(typeCommand, dataCommand);
            } catch (IOException e) {
                Console.writer.println(e.getMessage());
            }
        }
    }

    private void newCommand(String typeCommand, String dataCommand) {
        switch (typeCommand) {
            case ("answer") -> {
                String[] answer = dataCommand.split(";", 2);
                this.eventAnswer.newAnswer(this, Integer.parseInt(answer[0]), answer[1]);
            }
            case ("host") -> {
                String[] host = dataCommand.split(";", 2);
            }
        }
    }

    public void sendQuestion(Question question, EventAnswer event) {
        this.writer.println("question:"+question.id + ";" + question.title + ";" + question.type);
        this.eventAnswer = event;
    }

    public void sendResults(Answer answerCorrect, boolean win, String usernameWin) {
        this.writer.println("result:"+win+";"+usernameWin+";"+answerCorrect.getAnswer());
    }
}
