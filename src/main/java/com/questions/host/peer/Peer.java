package com.questions.host.peer;

import com.questions.game.Commands.*;
import com.questions.utils.Connection;
import com.questions.utils.Console;
import com.questions.host.questionnaire.Answer;
import com.questions.host.questionnaire.Question;

import java.io.*;

public class Peer extends Thread {

    public final String username;
    private final Connection conn;
    private final EventsPeer events;

    public Peer(Connection conn, String username, EventsPeer events) {
        this.conn = conn;
        this.username = username;
        this.events = events;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                ClientCommand cmd = ClientCommand.parseFrom(this.conn.receive());

                if (cmd.hasAnswer()) this.events.answer(this, cmd.getAnswer());
                else if (cmd.hasChangeRound()) this.events.changeRound(this, cmd.getChangeRound());

            } catch (IOException e) { Console.writer.println(e.getMessage()); }
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
