package com.questions.game.host.peer;

import com.questions.console.Console;
import com.questions.game.host.events.EventAnswer;
import com.questions.game.host.events.EventHostRound;
import com.questions.game.host.questionnaire.Answer;
import com.questions.game.host.questionnaire.Question;

import java.io.*;

public class Peer extends Thread {

    public final String username;

    private final BufferedReader reader;
    private final PrintWriter writer;

    public EventAnswer eventAnswer;
    public EventHostRound eventHostRound;

    public Peer(BufferedReader reader, PrintWriter writer, String username) {
        this.reader = reader;
        this.writer = writer;
        this.username = username;
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
            case "answer":
                if (this.eventAnswer != null) {
                    String[] answer = dataCommand.split(";", 2);
                    this.eventAnswer.newAnswer(this, Integer.parseInt(answer[0]), answer[1]);
                }
                break;
            case "host":
                if (this.eventHostRound != null) {
                    String[] addr = dataCommand.split(";", 2);
                    this.eventHostRound.event(addr[0], Integer.valueOf(addr[1]));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + typeCommand);
        }
    }

    public void sendQuestion(Question question) {
        this.writer.println("question:"+question.id + ";" + question.title + ";" + question.type);
    }

    public void sendResults(Answer answerCorrect, boolean win, String usernameWin) {
        this.writer.println("result:"+win+";"+usernameWin+";"+answerCorrect.getAnswer());
    }

    public void sendNewHostRound(String host, Integer port) {
        this.writer.println("round:"+host+";"+port);
    }
}
