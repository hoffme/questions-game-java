package com.questions.host.peer;

import com.questions.utils.Connection;
import com.questions.utils.Console;
import com.questions.host.events.EventAnswer;
import com.questions.host.events.EventHostRound;
import com.questions.host.questionnaire.Answer;
import com.questions.host.questionnaire.Question;

import java.io.*;

public class Peer extends Thread {

    public final String username;

    private final Connection conn;

    public EventAnswer eventAnswer;
    public EventHostRound eventHostRound;

    public Peer(Connection conn, String username) {
        this.conn = conn;
        this.username = username;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                String[] data = new String(this.conn.receive()).split(":", 2);

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
        try {
            this.conn.send(("question:"+question.id + ";" + question.title + ";" + question.type).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendResults(Answer answerCorrect, boolean win, String usernameWin) {
        try {
            this.conn.send(("result:"+win+";"+usernameWin+";"+answerCorrect.getAnswer()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNewHostRound(String host, Integer port) {
        try {
            this.conn.send(("round:"+host+";"+port).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
